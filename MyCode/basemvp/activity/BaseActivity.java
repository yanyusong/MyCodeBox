package cn.aibianli.sdot.base.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import cn.aibianli.sdot.SdotApplication;
import cn.aibianli.sdot.base.module.base.IPermissionsDispatcherView;
import cn.aibianli.sdot.base.module.network.BaseNetContract;
import cn.aibianli.sdot.modules.login.LoginActivity;
import permissions.dispatcher.PermissionRequest;


public class BaseActivity extends RxAppCompatActivity implements BaseNetContract.INetView, IPermissionsDispatcherView {

    public Context mContext;
    public ProgressDialog pDialog;
    private Toast toast;
    private AlertDialog toLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        SdotApplication.getInstance().addActivity(this);
        /**
         * 设置为竖屏
         */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SdotApplication.getInstance().removeActivity(this);
    }

    public void showToast(String content) {
        if (toast == null) {
            toast = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    @Override
    public void showLoading(boolean cancelable, @Nullable final ILoadingCancelListener listener) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(mContext);
        }
        pDialog.setCancelable(cancelable);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (listener != null) {
                    listener.onLoadCancelListener();
                }
                hideLoading();
            }
        });
        pDialog.setMessage("Loading...");
        Log.e("Thread", "showLoading线程的名字是---------" + Thread.currentThread().getName());
        pDialog.show();

    }

    @Override
    public void hideLoading() {
        if (pDialog != null && pDialog.isShowing()) {
            Log.e("Thread", "hideLoading线程的名字是---------" + Thread.currentThread().getName());
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void showLoadingError() {
        showToast("获取失败");
    }

    public void showEmptyPage() {
        showToast("暂无数据");
    }

    public void showNoNetWork() {
        showToast("网络连接失败");
    }

    @Override
    public void showToLoginDialog() {
        if (toLoginDialog == null) {
            toLoginDialog = new AlertDialog.Builder(mContext)
                    .setTitle("重新登录")
                    .setMessage("登录过期,请重新登录")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent toLogin = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(toLogin);
                            dialog.dismiss();
                        }
                    }).create();
        }
        toLoginDialog.show();
    }

    @Override
    public void showRationaleDialog(String message, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(message)
                .show();
    }
}
