package net.aibianli.pastead.common.helper.httploadhelper;

import com.android.volley.VolleyError;

/**
 * Created by mac on 16/3/3.
 */
public class Response {

    //string请求
    public interface RequestCallback{

        void start(String reqTag);

        void success(String response);

        void error(VolleyError error);

        void finish(String reqTag);

        void cancel(String reqTag);

    }

    //jsonObject请求

    //jsonArray请求

    //...
}
