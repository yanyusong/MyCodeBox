package net.boyazhidao.cgb.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;

import net.boyazhidao.cgb.CGBapplication;
import net.boyazhidao.cgb.activity.LoginActivity;
import net.boyazhidao.cgb.http_volley.RequestInfo;
import net.boyazhidao.cgb.http_volley.VolleyLoader;
import net.boyazhidao.cgb.http_volley.VolleyResponse;
import net.boyazhidao.cgb.model.ComRespInfo;
import net.boyazhidao.cgb.model.PageModel;
import net.boyazhidao.cgb.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 16/3/3.
 */
public abstract class BaseRecyclerViewFragment<T> extends RecyclerViewFragment<T> {

    public static final int REQUEST_CODE_TOKEN_INABLE = 123;

    private static final String REQUEST_TAG = "reqTag";
    private static final String REQUEST_INFO = "reqInfo";
    private static final String REQUEST_SHOW_DIALOG = "showDialog";
    private static final String REQUEST_CANCELABLE = "cancelable";
    //    private static final String REQUEST_CALLBACK = "strReqCallback";

    private String reqTag = "BaseRecyclerFrag";
    private RequestInfo reqInfo;
    private Boolean isShowDialog = true;
    private Boolean cancelable = false;
    //是否已经在加载了，true则无法刷新或加载更多
    private volatile Boolean isLoading = false;

    private volatile boolean isClear = false;//是否清空列表所有数据
    private List<T> items = new ArrayList<>();// list中当前最新页的数据

    protected volatile int page = 0;//分页的页码
    protected volatile int pageSize = 10;//每页有多少条目


    protected static Bundle data2Bundle(String reqTag, RequestInfo reqInfo, boolean isShowDialog,
                                        boolean cancelable, int itemLayoutId) {
        Bundle bundle = new Bundle();
        bundle.putString(REQUEST_TAG, reqTag);
        if (reqInfo != null) {
            bundle.putSerializable(REQUEST_INFO, reqInfo);
        } else {
            Log.e("BaseRecyclerFrag", "Error:reqInfo不能为null");
        }
        bundle.putBoolean(REQUEST_SHOW_DIALOG, isShowDialog);
        bundle.putBoolean(REQUEST_CANCELABLE, cancelable);
        bundle.putInt(ITEM_LAYOUT_ID, itemLayoutId);
        return bundle;
    }

    public void init(String reqTag, RequestInfo reqInfo, boolean isShowDialog,
                     boolean cancelable, int itemLayoutId) {
        Bundle bundle = data2Bundle(reqTag, reqInfo, isShowDialog, cancelable, itemLayoutId);
        setArguments(bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.reqTag = args.getString(REQUEST_TAG);
            this.reqInfo = (RequestInfo) args.getSerializable(REQUEST_INFO);
            this.isShowDialog = args.getBoolean(REQUEST_SHOW_DIALOG, true);
            this.cancelable = args.getBoolean(REQUEST_CANCELABLE, false);
            this.itemLayoutId = args.getInt(ITEM_LAYOUT_ID) == -1 ? android.R.layout.simple_list_item_1 : args.getInt(ITEM_LAYOUT_ID);
            Log.e("BaseRecyclerFrag", "reqTag =" + reqTag + ",reqInfo =" + reqInfo + ",isShowDialog =" + isShowDialog + ",cancelable =" + cancelable + ",itemLayoutId =" + itemLayoutId);
        } else {
            Log.e("BaseRecyclerFrag", "Error:请调用BaseRecyclerViewFragment的init()方法进行初始化");
        }
    }

    @Override
    public void onInitData() {
        if (!isLoading) {
            isLoading = true;
            page = 0;
            isClear = true;
            loadMore(reqInfo, isShowDialog, cancelable);
        }
    }

    @Override
    public void onLoadMore() {
        if (!isLoading) {
            isLoading = true;
            loadMore(reqInfo, false, true);
        }
    }

    @Override
    public void onLoadRefresh() {
        if (!isLoading) {
            isLoading = true;
            page = 0;
            isClear = true;
            loadMore(reqInfo, true, true);
        }
    }

    /**
     * 请求参数改变后，重新发送服务器请求，常用于重新排序或者筛选等功能
     */
    public void reLoadData(RequestInfo requestInfo) {
        if (!isLoading) {
            isLoading = true;
            page = 0;
            isClear = true;
            reqInfo = requestInfo;
            loadMore(reqInfo, false, false);
        }
    }

    /**
     * 请求参数不变，重新发送服务器请求
     */
    public void reLoadData() {
        if (!isLoading) {
            isLoading = true;
            page = 0;
            isClear = true;
            loadMore(reqInfo, false, false);
        }
    }

    //    private void loadMore(boolean showDialog, boolean canCancel) {
    //        page++;
    //        reqInfo.addBodyParams("page", String.valueOf(page));
    //        reqInfo.addBodyParams("pageSize", String.valueOf(pageSize));
    //        VolleyLoader.start(ct).post(reqTag, reqInfo, showDialog, canCancel, new VolleyResponse.strReqCallback() {
    //            @Override
    //            public void success(String response) {
    //                handleResult(response);
    //                completeRefreshing();
    //            }
    //
    //            @Override
    //            public void error(VolleyError error) {
    //                completeRefreshing();
    //                showToast("数据获取失败");
    //            }
    //        });
    //    }

    protected void loadMore(RequestInfo reqInfo, boolean showDialog, boolean canCancel) {
        page++;
        pageSize = 10;
        StringBuilder strUrl = new StringBuilder(reqInfo.getUrl());
        strUrl.append("page=" + page).append("&pageSize=" + pageSize);
        getLoadData(new RequestInfo(strUrl.toString()), showDialog, canCancel);
    }

    //get方法获取数据
    protected void getLoadData(RequestInfo reqInfo, boolean showDialog, boolean canCancel) {
        if (DeviceUtils.isHasNetWork()) {
            Log.e("reqinfo",reqInfo.toString());
            VolleyLoader.start(ct).get(reqTag, reqInfo, showDialog, canCancel, new VolleyResponse.strReqCallback() {
                @Override
                public void success(String response) {
                    handleResult(response);
                    completeRefreshing();
                    isLoading = false;
                }

                @Override
                public void error(VolleyError error) {
                    completeRefreshing();
                    isLoading = false;

                    showToast("授权已过期，请重新登录");
                    Intent toLogin = new Intent(ct, LoginActivity.class);
                    ct.startActivity(toLogin);

                    //                String str = new String(error.networkResponse.data);
                    //                ErrorModel errorModel = JSONObject.parseObject(str, ErrorModel.class);
                    //                if (errorModel.getMessage().equals("Authorization has been denied for this request.")
                    //                        ||errorModel.getMessage().equals("已拒绝为此请求授权。")) {
                    //                    showToast("授权已过期，请重新登录");
                    //                    Intent toLogin = new Intent(ct, LoginActivity.class);
                    //                    ct.startActivity(toLogin);
                    //                }else{
                    //                    showToast("数据获取失败");
                    //                }
                }
            });
        } else {
            if(isRefreshing()){
                completeRefreshing();
            }

            showToast("请检查网络连接");
        }
    }

    //post方法获取数据
    protected void postLoadData(RequestInfo reqInfo, boolean showDialog, boolean canCancel) {
        if (DeviceUtils.isHasNetWork()) {
            Log.e("reqinfo",reqInfo.toString());
            VolleyLoader.start(ct).post(reqTag, reqInfo, showDialog, canCancel, new VolleyResponse.strReqCallback() {
                @Override
                public void success(String response) {
                    handleResult(response);
                    completeRefreshing();
                    isLoading = false;
                }

                @Override
                public void error(VolleyError error) {
                    completeRefreshing();
                    isLoading = false;

                    showToast("授权已过期，请重新登录");
                    Intent toLogin = new Intent(ct, LoginActivity.class);
                    getActivity().startActivityForResult(toLogin, REQUEST_CODE_TOKEN_INABLE);

                    //                String str = new String(error.networkResponse.data);
                    //                ErrorModel errorModel = JSONObject.parseObject(str, ErrorModel.class);
                    //                if (errorModel.getMessage().equals("Authorization has been denied for this request.")
                    //                        ||errorModel.getMessage().equals("已拒绝为此请求授权。")) {
                    //                    showToast("授权已过期，请重新登录");
                    //                    Intent toLogin = new Intent(ct, LoginActivity.class);
                    //                    getActivity().startActivityForResult(toLogin, REQUEST_CODE_TOKEN_INABLE);
                    //
                    //                }else{
                    //                    showToast("数据获取失败");
                    //                }
                }
            });
        } else {
            if(isRefreshing()){
                completeRefreshing();
            }
            showToast("请检查网络连接");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_TOKEN_INABLE:
                    reLoadData();
                    break;
                default:
                    break;
            }

        }

    }

    private void handleResult(String response) {
        ComRespInfo comRespInfo = JSON.parseObject(response, ComRespInfo.class);
        PageModel pageModel = JSON.parseObject(comRespInfo.getData(), PageModel.class);
        boolean hasnext = pageModel.getHas_next();
        setHasNextPage(hasnext);
        changeFooterText(!hasnext);
        items.clear();
        items = handleData(pageModel);
//        if (items.size()==0) {
//            changeFooterText(true);
//        }else{
//            changeFooterText(false);
//        }
        if (isClear) {
            itemDatas.clear();
            isClear = false;
        }
        itemDatas.addAll(items);
        updateData();
        if (itemDatas.size() == 0) {
            // TODO: 16/1/6  在此显示无数据时的图片
        }
    }

    /**
     * 得到子类model的class
     *
     * @return Class
     */
    public abstract List<T> handleData(PageModel pageModel);


    @Override
    public void onPause() {
        super.onPause();
        cancelRequests(reqTag);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //但将要被隐藏的时候,中断取消网络请求
            cancelRequests(reqTag);
        }

    }

    //
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//    }
    //主动取消所有网络请求
    public void cancelRequests(Object tag){
        CGBapplication.getInstance().cancelPendingRequests(reqTag);
        isLoading = false;
        if(isRefreshing()){
            completeRefreshing();
        }
    }

}



















