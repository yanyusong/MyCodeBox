package net.boyazhidao.cgb.http_volley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.boyazhidao.cgb.CGBapplication;

import java.util.Map;

/**
 * Created by mac on 16/3/2.
 */
public class VolleyLoader {

    private Context mContext;
    private ProgressDialog pDialog;
    private CGBapplication application = CGBapplication.getInstance();
    //    private VolleyResponse.strReqCallback strReqCallback;

    public static VolleyLoader start(Context context) {
        return new VolleyLoader(context);
    }

    private VolleyLoader(Context context) {
        mContext = context;
    }

    public void post(String reqTag, RequestInfo reqInfo, boolean isShowDialog, boolean cancelable, VolleyResponse.strReqCallback callback) {
        request(Request.Method.POST, reqTag, reqInfo, isShowDialog, cancelable, callback);
    }

    public void get(String reqTag, String url, boolean isShowDialog, boolean cancelable, VolleyResponse.strReqCallback callback) {
        RequestInfo reqInfo = new RequestInfo(url);
        request(Request.Method.GET, reqTag, reqInfo, isShowDialog, cancelable, callback);
    }

    public void get(String reqTag, RequestInfo reqInfo, boolean isShowDialog, boolean cancelable, VolleyResponse.strReqCallback callback) {
        request(Request.Method.GET, reqTag, reqInfo, isShowDialog, cancelable, callback);
    }

    public void request(int reqMethod, String reqTag, final RequestInfo reqInfo, final boolean isShowDialog, boolean cancelable, final VolleyResponse.strReqCallback callback) {

        final String tag = reqTag;
        if (isShowDialog) {
            if (pDialog == null) {
                pDialog = new ProgressDialog(mContext);
            }
            pDialog.setCancelable(cancelable);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    application.cancelPendingRequests(tag);
                }
            });
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        StringRequest strReq = new StringRequest(reqMethod, reqInfo.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(tag, response.toString());
                callback.success(response);
                if (isShowDialog && pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                String str = new String(error.networkResponse.data);
                Log.e(tag, "Error: " + error.toString());
//                ErrorModel errorModel = JSONObject.parseObject(str, ErrorModel.class);
//                if (errorModel.getMessage().equals("Authorization has been denied for this request.")) {
//                    Intent toLogin = new Intent(mContext, LoginActivity.class);
//                    mContext.startActivity(toLogin);
//                }
                callback.error(error);
                if (isShowDialog && pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return reqInfo.getBodyParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = reqInfo.getHeaders();
                if (headers != null && !headers.isEmpty()) {
                    return headers;
                }
                return super.getHeaders();
            }

//            @Override
//            public RetryPolicy getRetryPolicy() {
//                RetryPolicy retryPolicy = new DefaultRetryPolicy(10000,0,1f);
//                return retryPolicy;
//            }
        };
        //设置请求超时为三十秒
        strReq.setRetryPolicy(new DefaultRetryPolicy(10000,1,2f));
        // Adding request to request queue
        CGBapplication.getInstance().addToRequestQueue(strReq, reqTag);
    }

}
