package cn.aibianli.sdot.components.http.transformer;


import cn.aibianli.sdot.base.module.network.BaseNetContract;
import cn.aibianli.sdot.common.beans.ComRespInfo;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

/**
 * Created by mac on 16/7/26.
 * subscriber订阅subscribe之前和onCompleted()/onError()执行时的操作,
 * 比如:
 * 1,showLoading(),hideLoading(),etc
 */
public class EmitBeforeAndAfterTransformer<T> implements Observable.Transformer<ComRespInfo<T>, ComRespInfo<T>> {

    private BaseNetContract.INetView netView;

    private Subscriber subscriber;

    private boolean canShowLoading;

    private boolean canLoadCelable;

    public EmitBeforeAndAfterTransformer(BaseNetContract.INetView netView, Subscriber subscriber, boolean canShowLoading, boolean canLoadCelable) {
        this.netView = netView;
        this.subscriber = subscriber;
        this.canShowLoading = canShowLoading;
        this.canLoadCelable = canLoadCelable;
    }

    public EmitBeforeAndAfterTransformer(BaseNetContract.INetView netView, Subscriber subscriber) {
        this.netView = netView;
        this.subscriber = subscriber;
        this.canShowLoading = true;
        this.canLoadCelable = false;
    }

    @Override
    public Observable<ComRespInfo<T>> call(Observable<ComRespInfo<T>> comRespInfoObservable) {
        return comRespInfoObservable.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (canShowLoading) {
                    netView.showLoading(canLoadCelable, new BaseNetContract.IBaseNetView.ILoadingCancelListener() {
                        @Override
                        public void onLoadCancelListener() {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.unsubscribe();
                            }
                        }
                    });
                }
            }
        }).doOnTerminate(new Action0() {
            @Override
            public void call() {
                if (canShowLoading) {
                    netView.hideLoading();
                }
            }
        });
    }
}
