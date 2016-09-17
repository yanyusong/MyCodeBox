package net.aibianli.pastead.common.helper.httploadhelper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.aibianli.pastead.common.utils.BitmapUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/3/2.
 */
public class VolleyLoader {

    private static String TAG = "VolleyLoader";
    private static VolleyLoader mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private Response.RequestCallback callback = null;

    private VolleyLoader(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyLoader(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            //            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HttpClientStack(new DefaultHttpClient()));
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null && callback != null) {
            mRequestQueue.cancelAll(tag);
            callback.cancel(tag);
        }
    }

    public void request(int reqMethod, String reqTag, final RequestInfo reqInfo, final Response.RequestCallback mCallback) {

        final String tag = reqTag;
        if (mCallback != null) {
            callback = mCallback;
        } else {
            Log.e("Error,VolleyLoader", "请求回调不能为null");
            return;
        }

        callback.start(tag);

        StringRequest strReq = new StringRequest(reqMethod, reqInfo.getUrl(), new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(tag, response.toString());
                callback.success(response);
                callback.finish(tag);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(tag, "Error: " + error.toString());
                callback.error(error);
                callback.finish(tag);

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

        };
        //设置请求超时为十秒
        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 2f));
        // Adding request to request queue
        addToRequestQueue(strReq, reqTag);
    }

    public void request(String reqTag, final RequestInfo reqInfo, final String fileKey, final String fileName, final File file, final Response.RequestCallback mCallback) {
        final String tag = reqTag;
        if (mCallback != null) {
            callback = mCallback;
        } else {
            Log.e("Error,VolleyLoader", "请求回调不能为null");
            return;
        }

        callback.start(tag);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(reqInfo.getUrl(), null, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e(tag, response.toString());
                callback.success(new String(response.data));
                callback.finish(tag);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(tag, "Error: " + new String(error.networkResponse.data));
                callback.error(error);
                callback.finish(tag);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return reqInfo.getBodyParams();
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                params.put(fileKey, new DataPart(fileName, BitmapUtil.getBytesFromFile(file), "image/jpeg"));
                return super.getByteData();
            }
        };

        //设置请求超时为十秒
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 2f));
        // Adding request to request queue
        addToRequestQueue(multipartRequest, reqTag);
    }

}
