package cn.aibianli.sdot.components.http.transformer;

import android.content.Context;

import cn.aibianli.sdot.common.beans.ComRespInfo;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mac on 16/7/26.
 * 返回数据的错误预处理
 */
public class ErrorCheckerTransformer<T> implements Observable.Transformer<ComRespInfo<T>, ComRespInfo<T>> {

    private Context context;

    public ErrorCheckerTransformer(Context context) {
        this.context = context;
    }

    @Override
    public Observable<ComRespInfo<T>> call(Observable<ComRespInfo<T>> comRespInfoObservable) {

        return comRespInfoObservable.map(new Func1<ComRespInfo<T>, ComRespInfo<T>>() {
            @Override
            public ComRespInfo<T> call(ComRespInfo<T> tComRespInfo) {
                if (tComRespInfo.getResult()==0) {
                    //// TODO: 16/7/26 mark 在这里做error处理
                }
                return tComRespInfo;
            }
        });
    }
}
