package net.xichiheng.yulewa.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.WalkRouteResult;

import android.os.Bundle;
import net.xichiheng.yulewa.base.BaseMapFragment;

public class MapLocationFragment extends BaseMapFragment<HashMap<String, Object>> {

	@Override
	public List<HashMap<String, Object>> initMarkersList() {
		List<HashMap<String, Object>> mList = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> mLocation = new HashMap<String, Object>();
		Bundle mBundle = getArguments();
		if (mBundle!=null) {
			mLocation.put(TAG_ITEM_ID, mBundle.get("storeId"));
			mLocation.put(TAG_ITEM_TYPE, mBundle.get("storeType"));
			mLocation.put(TAG_ITEM_NAME, mBundle.get("storeName"));
			mLocation.put(TAG_ITEM_LAT, mBundle.get("storeLat"));
			mLocation.put(TAG_ITEM_LNG, mBundle.get("storeLng"));
			mLocation.put(TAG_IS_ROUTE, mBundle.getBoolean("isRoute"));
			mList.add(mLocation);
		}
		return mList;
	}

	@Override
	public List<HashMap<String, Object>> updateMarkersData(
			List<HashMap<String, Object>> mksData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMapType() {
		// TODO Auto-generated method stub
		return 1;
	}

}
