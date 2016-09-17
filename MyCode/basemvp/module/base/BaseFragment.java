package cn.aibianli.sdot.base.module.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxFragment;

import permissions.dispatcher.PermissionRequest;


/**
 * Created by mac on 16/3/1.
 */
public class BaseFragment<T extends BaseContract.IBasePresenter> extends RxFragment implements BaseContract.IBaseView<T> {

    public Context ct;
    private BaseContract.IBasePresenter mPresenter;
    private Toast toast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ct = getActivity();
    }

    @Override
    public void showToast(String content) {
        if (toast == null) {
            toast = Toast.makeText(ct, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    @CallSuper
    @Override
    public void setPresenter(T presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public RxFragment getRxView() {
        return this;
    }

    @Override
    public void showRationaleDialog(String message, final PermissionRequest request) {
        new AlertDialog.Builder(ct)
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
