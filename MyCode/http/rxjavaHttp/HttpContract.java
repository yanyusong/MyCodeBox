package cn.aibianli.sdot.data.http;

import android.content.Context;

/**
 * Created by mac on 16/7/19.
 */
public interface HttpContract {

    LoginService loginHttp();

    MerchantService merchantHttp(Context mContext);

    CommonService commonHttp(Context mContext);

}
