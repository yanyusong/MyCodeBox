package cn.aibianli.sdot.components.http.Subscriber;

import android.content.Context;
import android.support.annotation.CallSuper;

import cn.aibianli.sdot.base.module.network.BaseNetContract;
import cn.aibianli.sdot.common.beans.ComRespInfo;
import cn.aibianli.sdot.common.utils.DeviceUtils;
import rx.Subscriber;

/**
 * Created by mac on 16/7/27.
 */
public abstract class NetCheckerSubscriber<T> extends Subscriber<ComRespInfo<T>> {

    private Context context;
    private BaseNetContract.INetView netView;

    public NetCheckerSubscriber(Context context, BaseNetContract.INetView netView) {
        this.context = context;
        this.netView = netView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!new DeviceUtils(context).isHasNetWork()) {
            if (!isUnsubscribed()) {
                unsubscribe();
            }
            netView.showNoNetWork();
        }
    }

    @CallSuper
    @Override
    public void onNext(ComRespInfo<T> tComRespInfo) {
        if ((tComRespInfo.getResult() != 1) && tComRespInfo.getResultcode() == 99 || tComRespInfo.getResultcode() == 97) {
            netView.showToLoginDialog();
        }
    }
}
