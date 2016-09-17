package net.xichiheng.yulewa.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lidroid.xutils.util.LogUtils;

import android.os.Bundle;
import net.xichiheng.yulewa.base.BaseMapFragment;
import net.xichiheng.yulewa.model.StoreModel;

public class NearbyStoresMapFragment extends BaseMapFragment<StoreModel> {

	@Override
	public List<HashMap<String, Object>> initMarkersList() {
		List<HashMap<String, Object>> markersList = new ArrayList<HashMap<String,Object>>();
		Bundle bundle = getArguments();
		if (bundle != null) {
			List<StoreModel> storeDatas = (List<StoreModel>) bundle.getSerializable("TAG_NEARBY_STORES_EXTRA");
			if (storeDatas.size()>0) {
				for (int i = 0; i < storeDatas.size(); i++) {
					HashMap<String, Object> markerData = new HashMap<String, Object>();
					markerData.put(TAG_ITEM_ID, storeDatas.get(i).getId());
					markerData.put(TAG_ITEM_TYPE, storeDatas.get(i).getStoreType());
					markerData.put(TAG_ITEM_NAME, storeDatas.get(i).getStoreName());
					markerData.put(TAG_ITEM_LAT, storeDatas.get(i).getLat());
					markerData.put(TAG_ITEM_LNG, storeDatas.get(i).getLng());
					markerData.put(TAG_IS_ROUTE, false);
					markersList.add(markerData);
				}
			}
		}else {
			LogUtils.e("fragment间传递的bundle为空");
		}
		return markersList;
	}

	@Override
	public List<HashMap<String, Object>> updateMarkersData(
			List<StoreModel> mksData) {
		List<HashMap<String, Object>> markersList = new ArrayList<HashMap<String,Object>>();
		if (mksData.size()>0) {
		for (int i = 0; i < mksData.size(); i++) {
			HashMap<String, Object> markerData = new HashMap<String, Object>();
			markerData.put(TAG_ITEM_ID, mksData.get(i).getId());
			markerData.put(TAG_ITEM_TYPE, mksData.get(i).getStoreType());
			markerData.put(TAG_ITEM_NAME, mksData.get(i).getStoreName());
			markerData.put(TAG_ITEM_LAT, mksData.get(i).getLat());
			markerData.put(TAG_ITEM_LNG, mksData.get(i).getLng());
			markerData.put(TAG_IS_ROUTE, false);
			markersList.add(markerData); 
		}
		}
		return markersList;
	}

	@Override
	public int getMapType() {
		// TODO Auto-generated method stub
		return 1;
	}


	
	
}
