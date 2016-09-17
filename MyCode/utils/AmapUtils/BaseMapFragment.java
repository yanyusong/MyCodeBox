package net.xichiheng.yulewa.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.xichiheng.yulewa.R;
import net.xichiheng.yulewa.YLWApplication;
import net.xichiheng.yulewa.activity.MyInfoActivity;
import net.xichiheng.yulewa.activity.StoreDetailActivity;
import net.xichiheng.yulewa.adapter.GeneralListAdapter;
import net.xichiheng.yulewa.adapter.ViewHolder;
import net.xichiheng.yulewa.model.City;
import net.xichiheng.yulewa.util.OutLog;
import net.xichiheng.yulewa.util.ToastShowMessage;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseMapFragment<T> extends Fragment implements LocationSource, AMapLocationListener, OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMapLoadedListener, OnRouteSearchListener {
	
	public static final LatLng XIAN = new LatLng(34.341568, 108.940174);// 西安市经纬度
	public static final LatLng ZHENGZHOU = new LatLng(34.7466, 113.625367);// 郑州市经纬度
	public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
	
	//经度。纬度。storeId。storeType。storeName
	//经度。纬度。peopleId。peopleSex。peopleNickname	
	public static final String TAG_ITEM_TYPE = "ItemType";
	public static final String TAG_ITEM_ID = "ItemId";
	public static final String TAG_ITEM_NAME = "ItemName";
	public static final String TAG_ITEM_LAT = "ItemLat";
	public static final String TAG_ITEM_LNG = "ItemLng";
	
	public static final String TAG_IS_ROUTE = "IsRoute";//是否路径规划
	
	//声明变量
	private YLWApplication application;
    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy aMapManager;
    
    
    
    private RouteSearch routeSearch;
    private LatLng myLocation = new LatLng(39.90403, 116.407525);//我的位置,定位失败则默认北京
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果
    private Boolean isRoute = false;
    
    private List<HashMap<String, Object>> markersList = new ArrayList<HashMap<String,Object>>();
	private int mapType;
	private Context ct;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_map, null);
		mapView = (MapView) view.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap==null) {			
			aMap = mapView.getMap();
		}

		ct = getActivity();
		
		application = (YLWApplication) ((BaseFragmentActivity)ct).getApplication();
		
		markersList = initMarkersList();
		
		mapType = getMapType();
		
		mUiSettings = aMap.getUiSettings();
		
		aMap.setLocationSource(this);// 设置定位监听
		mUiSettings.setMyLocationButtonEnabled(true);//是否显示默认的定位按钮
		aMap.setMyLocationEnabled(true);//是否可触发定位并显示定位层
		mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//高德logo在左下方
		mUiSettings.setScaleControlsEnabled(true);//设置地图默认的比例尺是否显示
		mUiSettings.setCompassEnabled(true);//设置地图默认指南针是否显示
		aMap.setOnMapLoadedListener(this);
		
		if ((markersList != null)&&(markersList.size()>0)) {
			isRoute = (Boolean) markersList.get(0).get(TAG_IS_ROUTE);
			//if ((isRoute == null)||(isRoute == false)) {
			if ((isRoute == null)||(isRoute == false)) {			
				addMarkersToMap();	
				initMarkersListeners();
			}
		}
		return view;
	}

	
	private void initMarkersListeners() {
//		// 自定义系统定位蓝点
//		MyLocationStyle myLocationStyle = new MyLocationStyle();
//		// 自定义定位蓝点图标
//		 myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//		        fromResource(R.drawable.location_marker));
//		// 自定义精度范围的圆形边框颜色
//		 myLocationStyle.strokeColor(Color.BLACK);
//		//自定义精度范围的圆形边框宽度
//		myLocationStyle.strokeWidth(5);
//		// 将自定义的 myLocationStyle 对象添加到地图上
//		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setInfoWindowAdapter(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setOnMarkerClickListener(this);
	}
	/**
	 * 在地图上添加markers
	 */
	private void addMarkersToMap() {
		Builder boundsBuilder = new LatLngBounds.Builder();
			for (int i = 0; i < markersList.size(); i++) {
				HashMap<String, Object> marker = new HashMap<String, Object>();
				marker = markersList.get(i);	
				MarkerOptions markerOptions = new MarkerOptions();
				LatLng latLng = new LatLng(Double.valueOf(marker.get(TAG_ITEM_LAT).toString()),//经度
										   Double.valueOf(marker.get(TAG_ITEM_LNG).toString()));//纬度
				markerOptions.position(latLng);
				if (mapType == 1) {
					switch (Integer.valueOf(marker.get(TAG_ITEM_TYPE).toString())) {
					case 1:
						markerOptions.title("KTV");
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_ktv));
						break;
					case 2:
						markerOptions.title("商务KTV");
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_suktv));
						break;
					case 3:
						markerOptions.title("酒吧");
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_bar));
						break;
					default:
						break;			
					}	
				}else if (mapType == 2){
					switch (Integer.valueOf(marker.get(TAG_ITEM_TYPE).toString())) {
					case 0:
						markerOptions.title("女");
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_map_nv));
						break;
					case 1:
						markerOptions.title("男");
						markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_map_nan));
						break;
					default:
						break;			
					}	
				}
				
				Marker mk = aMap.addMarker(markerOptions);	
				mk.setObject(marker);//传入该marker的hashmap封装的数据
				boundsBuilder.include(latLng);
			}
				
		LatLngBounds bounds = boundsBuilder.build();		
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
		//aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
	}
	
	/**
	 * 更新地图中的markers
	 * @param mksData
	 */
	public void updateMarkers(List<T> mksData){
		aMap.clear();
		markersList = updateMarkersData(mksData);
		if ((markersList != null)&&(markersList.size()>0)) {
			isRoute = (Boolean) markersList.get(0).get(TAG_IS_ROUTE);
			//if ((isRoute == null)||(isRoute == false)) {
			if ((isRoute == null)||(isRoute == false)) {			
				addMarkersToMap();	
				initMarkersListeners();
			}
		}
	}
	/**
	 * 在地图中加载更多markers
	 * @param mksData
	 */
	public void loadMoreMarkers(List<T> mksData){
		//aMap.clear();
		markersList = updateMarkersData(mksData);
		if ((markersList != null)&&(markersList.size()>0)) {
			isRoute = (Boolean) markersList.get(0).get(TAG_IS_ROUTE);
			//if ((isRoute == null)||(isRoute == false)) {
			if ((isRoute == null)||(isRoute == false)) {			
				addMarkersToMap();	
				initMarkersListeners();
			}
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}
	//激活定位
	@Override
	public void activate(OnLocationChangedListener arg0) {
		startLocate(arg0);
	}
	//停止定位
	@Override
	public void deactivate() {
		stopLocate();
	}
	
	private void startLocate(OnLocationChangedListener arg0){
		mListener = arg0;
		if (aMapManager == null) {
			aMapManager = LocationManagerProxy.getInstance(getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
			// Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
			aMapManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, -1, 10, this);
		}
	}
	
	private void stopLocate(){
		mListener = null;
		if (aMapManager != null) {
			aMapManager.removeUpdates(this);
			aMapManager.destroy();
		}
		aMapManager = null;
	}
	
	/**
	 * 以下5个函数是请求定位后的监听事件
	 * @param location
	 */
	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 定位成功后回调函数
	 * @param arg0
	 */
	@Override
	public void onLocationChanged(AMapLocation arg0) {
		
		 if(arg0 != null && arg0.getAMapException().getErrorCode() == 0){
			 myLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
				if (mListener!=null) {
					//mListener.onLocationChanged(arg0);//显示自己当前所在位置，使用系统蓝色小圆点
				}
				if (isRoute==true) {
					searchRouteResult(myLocation);
				} 
	     }else {
	    	 OutLog.e("Location ERR:" + arg0.getAMapException().getErrorMessage());
			 	
		}
	}
	/**
	 * 自驾的路径规划
	 */
	public void searchRouteResult(LatLng myLocation){
		routeSearch = new RouteSearch(ct);
		routeSearch.setRouteSearchListener(this);
		LatLonPoint startPoint = new LatLonPoint(myLocation.latitude, myLocation.longitude);
		LatLonPoint endPoint = new LatLonPoint(Double.valueOf(markersList.get(0).get(TAG_ITEM_LAT).toString()),//经度
				   Double.valueOf(markersList.get(0).get(TAG_ITEM_LNG).toString()));//纬度
		RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
		//时间最少，距离最短，速度最快，避免收费。。。等路径规划限制在下面设置，默认速度最快
		//当前设置为最短距离
		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingShortDistance, null, null, "");
		routeSearch.calculateDriveRouteAsyn(query);
	}
	/**
	 * 设置点击marker事件监听器
	 */
	@Override
	public boolean onMarkerClick(Marker arg0) {
		Boolean isDefault = false;
		if ((isRoute==null)||(isRoute==false)) {
			if (arg0.isInfoWindowShown()) {
				arg0.hideInfoWindow();
			}else {			
				arg0.showInfoWindow();
			}
			isDefault = true;
		}else {
			if (arg0.isInfoWindowShown()) {
				arg0.hideInfoWindow();
			}else {			
				arg0.showInfoWindow();
			}
			isDefault = false;
		}
		return isDefault;//返回false表示点击marker的默认操作是显示它的信息窗口（如果可用）
	}
	/**
	 * 设置点击infoWindow事件的监听器
	 * @param arg0
	 */
	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	//这个方法只有在getInfoWindow(Marker)返回null 时才会被调用
	//提供了一个给默认信息窗口定制内容的方法
	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	//当使用接口 AMap.InfoWindowAdapter（显示信息窗口）的 getInfoWindow(Marker) 方法时，请确认标记（Marker）的 title 或 snippet 已赋值。
	//提供了一个个性化定制信息窗口的marker 对象
	@SuppressWarnings("unchecked")
	@Override
	public View getInfoWindow(Marker arg0) {
		if ((isRoute==null)||(isRoute==false)) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.map_infowindow, null);
		ListView mListview = (ListView) view.findViewById(R.id.map_info_list);
		final List<HashMap<String, Object>> markers = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < 1; i++) {
			HashMap<String, Object> marker = new HashMap<String, Object>();
			marker = (HashMap<String, Object>) arg0.getObject();
			markers.add(marker);
		}
		mListview.setAdapter(new MapInfoWindowAdapter(getActivity(), markers, R.layout.item_mapinfo_list));		
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, Object> marker = markers.get(position);
				String itemId = (String) marker.get(TAG_ITEM_ID);
				if (mapType == 1) {
					Intent toStoreDetail = new Intent(getActivity(), StoreDetailActivity.class);
					toStoreDetail.putExtra("storeId", itemId);				
					startActivity(toStoreDetail);
				}else if (mapType == 2) {
					Intent toPeopleDetail = new Intent(getActivity(), MyInfoActivity.class);
					toPeopleDetail.putExtra("userId", Integer.valueOf(itemId));				
					startActivity(toPeopleDetail);				
				}
					
			}
		});
		return view; 
		}else {
			return null;
		}
	}

	class MapInfoWindowAdapter extends GeneralListAdapter<HashMap<String, Object>>{

		public MapInfoWindowAdapter(Context ct,
				List<HashMap<String, Object>> data, int itemLayoutId) {
			super(ct, data, itemLayoutId);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void SetChildViewData(ViewHolder mViewHolder,
				final HashMap<String, Object> itemData, int position,
				BitmapUtils bitmapUtils) {
			ImageView mImageView = mViewHolder.getChildView(R.id.map_info_img);
			TextView mTextView = mViewHolder.getChildView(R.id.map_info_text);
			if (mapType == 1) {
				switch (Integer.parseInt(itemData.get(TAG_ITEM_TYPE).toString())) {
				case 1:
					mImageView.setImageResource(R.drawable.map_marker_ktv);
					break;
				case 2:
					mImageView.setImageResource(R.drawable.map_marker_suktv);
					break;
				case 3:
					mImageView.setImageResource(R.drawable.map_marker_bar);
					break;
				default:
					break;
				}		
				mTextView.setText(itemData.get(TAG_ITEM_NAME).toString());	
			}else if (mapType == 2) {
				switch (Integer.parseInt(itemData.get(TAG_ITEM_TYPE).toString())) {
				case 0:
					mImageView.setImageResource(R.drawable.img_map_nv);
					break;
				case 1:
					mImageView.setImageResource(R.drawable.img_map_nan);
					break;
				default:
					break;
				}		
				mTextView.setText(itemData.get(TAG_ITEM_NAME).toString());	
			}
			
		}
		
	}
	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在当前可视区域地图中 
		City currentCity = new City();
		currentCity = application.getCity();
		LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(currentCity.getLat(), currentCity.getLng())).build();
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));	
		//aMap.animateCamera(arg0, arg1, arg2);	
	}
	
	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
		if (arg1 == 0) {
			if (arg0 != null && arg0.getPaths() != null
					&& arg0.getPaths().size() > 0) {
			}
				driveRouteResult = arg0;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(ct, aMap, drivePath,
									driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
				drivingRouteOverlay.setNodeIconVisibility(false);
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				ToastShowMessage.showShortToast(getActivity(), "路径规划失败");
			}
	}
	
	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 初始化map中markers的数据，为List<HashMap<String, Object>>类型，
	 * HashMap<String, Object>中必须put进去以下几项tag，
	 * TAG_STORE_TYPE 店铺类型
	 * TAG_STORE_ID	      店铺id
	 * TAG_STORE_NAME 店铺名
	 * TAG_STORE_LAT  店铺经度
	 * TAG_STORE_LNG  店铺纬度
	 * @param <T>
	 * @return List<HashMap<String, Object>>
	 */
	public abstract List<HashMap<String, Object>> initMarkersList();
	/**
	 * 更新map中markers的数据，为List<HashMap<String, Object>>类型，
	 * 若为storesList则HashMap<String, Object>中必须put进去以下几项tag，
	 * TAG_STORE_TYPE 店铺类型
	 * TAG_STORE_ID	      店铺id
	 * TAG_STORE_NAME 店铺名
	 * TAG_STORE_LAT  店铺经度
	 * TAG_STORE_LNG  店铺纬度
	 * @param <T>
	 * @return List<HashMap<String, Object>>
	 */
	public abstract List<HashMap<String, Object>> updateMarkersData(List<T> mksData);
	/**
	 * 得到mapType，1表示店铺类型的地图，2表示人类型的地图
	 * @return mapType
	 */
	public abstract int getMapType();


}























