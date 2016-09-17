package cn.aibianli.sdot.components.http;

import android.content.Context;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;

import cn.aibianli.sdot.common.beans.ComRespInfo;
import cn.aibianli.sdot.components.http.transformer.ErrorCheckerTransformer;
import cn.aibianli.sdot.components.http.transformer.SchedulerTransformer;
import rx.Observable;

/**
 * Created by mac on 16/7/26.
 */
public class ObservableFactory {

    public static <T> Observable<ComRespInfo<T>> createNetObservable(Context context, Observable<ComRespInfo<T>> observable, RxFragment rxFragment) {

        return observable.compose(new ErrorCheckerTransformer<T>(context))
                .compose(new SchedulerTransformer<T>())
                .compose(rxFragment.<ComRespInfo<T>>bindUntilEvent(FragmentEvent.DESTROY));
    }

    public static <T> Observable<ComRespInfo<T>> createNetObservable(Context context, Observable<ComRespInfo<T>> observable, RxAppCompatActivity rxAppCompatActivity) {

        return observable.compose(new ErrorCheckerTransformer<T>(context))
                .compose(new SchedulerTransformer<T>())
                .compose(rxAppCompatActivity.<ComRespInfo<T>>bindUntilEvent(ActivityEvent.DESTROY));
    }
}
