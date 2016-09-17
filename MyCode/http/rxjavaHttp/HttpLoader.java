package cn.aibianli.sdot.data.http;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mac on 16/7/19.
 */
public class HttpLoader implements HttpContract {

    //    private static final String BaseUrl = "http://192.168.0.192:1625/";
            private static final String BaseUrl = "http://1.192.218.90:16255/";
//    private static final String BaseUrl = "http://mapi.aibianli.cn/";

    private static final int DEFAULT_TIMEOUT = 10;

    private Retrofit retrofit;

    private HttpLoader() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("okhttp", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okhttpBuiler = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        retrofit = retrofitBuilder
                .baseUrl(BaseUrl)
                .client(okhttpBuiler.build())
                .build();
    }

    public static HttpLoader getInstance() {

        return HttpLoaderHolder.instance;
    }

    //设计模式推荐的内部静态类实现的单例模式
    private static class HttpLoaderHolder {

        private static final HttpLoader instance = new HttpLoader();

    }


    private <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }


    @Override
    public LoginService loginHttp() {
        return createService(LoginService.class);
    }

    @Override
    public MerchantService merchantHttp(Context mContext) {
        return createService(MerchantService.class);
    }

    @Override
    public CommonService commonHttp(Context mContext) {
        return createService(CommonService.class);
    }
}
