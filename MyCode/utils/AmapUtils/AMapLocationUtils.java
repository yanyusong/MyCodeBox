package net.xichiheng.yulewa.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by mac on 16/1/12.
 */
public class AMapLocationUtils {
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    private Context mContext;
    private ProgressDialog loadingDialog;
    private IOnLocationListener iOnLocationListener;
    private static int errorTimes = 0;

    public AMapLocationUtils (Context ct) {
        mContext = ct;
        //初始化定位
        mLocationClient = new AMapLocationClient (ct);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption ();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode (AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress (false);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation (true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan (true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable (false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval (2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption (mLocationOption);

    }

    public interface IOnLocationListener {
        void OnLocationSuccess (double longitude, double latitude);

        void OnLocationFail (int errorCode);

        void OnFinish ();
    }

    public void startLocation (final IOnLocationListener mIOnLocationListener) {
        this.iOnLocationListener = mIOnLocationListener;
        //设置定位回调监听
        mLocationClient.setLocationListener (new AMapLocationListener () {
            @Override
            public void onLocationChanged (AMapLocation aMapLocation) {
                //定位成功后的回调
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode () == 0) {
                        iOnLocationListener.OnLocationSuccess (aMapLocation.getLongitude (), aMapLocation.getLatitude ());
                        mLocationClient.onDestroy ();
                    } else {
                        if (errorTimes < 3) {
                            startLocation (mIOnLocationListener);
                            errorTimes++;
                        } else {
                            mLocationClient.onDestroy ();
                        }
                        iOnLocationListener.OnLocationFail (aMapLocation.getErrorCode ());
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        ViewUtils.showToast (mContext, "定位失败", Toast.LENGTH_SHORT);
                        Log.e ("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode () + ", errInfo:"
                                + aMapLocation.getErrorInfo ());
                    }
                    if (loadingDialog != null && loadingDialog.isShowing ()) {
                        loadingDialog.dismiss ();
                    }
                    iOnLocationListener.OnFinish ();
                }
            }
        });
        //启动定位
        mLocationClient.startLocation ();
        loadingDialog = new ProgressDialog (mContext);
        loadingDialog.setCancelable (false);
        loadingDialog.setMessage ("正在定位，请稍候···");
//        loadingDialog.show();
    }

}
