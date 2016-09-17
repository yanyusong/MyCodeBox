package cn.aibianli.sdot.base.module.network_refresh;


import android.content.Context;

import cn.aibianli.sdot.base.module.network.BaseNetPresenter;
import cn.aibianli.sdot.common.beans.ComRespInfo;
import cn.aibianli.sdot.components.http.ObservableFactory;
import cn.aibianli.sdot.components.http.Subscriber.NetCheckerSubscriber;
import rx.Observable;

/**
 * Created by mac on 16/6/11.
 * DATA:表示ComRespInfo<DATA> 中的DATA的bean
 * D:表示每一个item的bean
 */
public abstract class BaseRefreshPresenter<DATA> extends BaseNetPresenter implements BaseRefreshContract.IBaseRefreshPresenter {

    private Context context;
    private BaseRefreshContract.IBaseRefreshView mView;

    public BaseRefreshPresenter(Context context, BaseRefreshContract.IBaseRefreshView mView) {
        super(mView);
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void start() {
        super.start();
    }

    public NetCheckerSubscriber getDefaultSubscriber() {
        return new NetCheckerSubscriber<DATA>(context, mView) {

            @Override
            public void onCompleted() {
                mView.hideRefreshInfication();
            }

            @Override
            public void onError(Throwable e) {
                mView.showLoadingError();
                mView.hideRefreshInfication();
            }

            @Override
            public void onNext(ComRespInfo<DATA> dataComRespInfo) {
                super.onNext(dataComRespInfo);
                if (dataComRespInfo.getResult() == 1) {
                    mView.onBindViewData(dataComRespInfo);
                } else {
                    mView.showToast(dataComRespInfo.getMsg());
                }
            }
        };
    }

    public abstract Observable<ComRespInfo<DATA>> getRequestObservable();

    public void loadData(Observable<ComRespInfo<DATA>> observable) {
        NetCheckerSubscriber subscriber = getDefaultSubscriber();
        ObservableFactory.createNetObservable(context, observable, mView.getRxView())
                .subscribe(subscriber);
    }

    @Override
    public void onRefreshData() {
        loadData(getRequestObservable());
    }

}








































