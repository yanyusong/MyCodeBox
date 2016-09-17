package net.boyazhidao.cgb.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import net.boyazhidao.cgb.CGBapplication;
import net.boyazhidao.cgb.http_volley.RequestInfo;
import net.boyazhidao.cgb.http_volley.VolleyLoader;
import net.boyazhidao.cgb.http_volley.VolleyResponse;
import net.boyazhidao.cgb.utils.DeviceUtils;

/**
 * 使用原生toolbar的兼容
 * Created by mac on 2016/2/3.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected CGBapplication cgbApplication = CGBapplication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        cgbApplication.addActivity(this);
    }

    //*************与服务器交互封装start************//

    public void loadData(String reqTag, RequestInfo reqInfo, boolean isShowDialog,
                         boolean cancelable, VolleyResponse.strReqCallback callback){
        if (DeviceUtils.isHasNetWork()) {
            //            reqInfo.getBodyParams().put(); //添加公共参数
            VolleyLoader.start(mContext).post(reqTag, reqInfo, isShowDialog, cancelable, callback);
        } else {
            showToast("请检查网络连接");
        }
    }

    public void getLoadData(String reqTag, RequestInfo reqInfo, boolean isShowDialog,
                         boolean cancelable, VolleyResponse.strReqCallback callback){
        if (DeviceUtils.isHasNetWork()) {
            //            reqInfo.getBodyParams().put(); //添加公共参数
            VolleyLoader.start(mContext).get(reqTag, reqInfo, isShowDialog, cancelable, callback);
        } else {
            showToast("请检查网络连接");
        }
    }
    public void showToast(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }
}
