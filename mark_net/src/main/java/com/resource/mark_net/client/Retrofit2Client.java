package com.resource.mark_net.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.resource.mark_net.utils.RxRetrofitApp;
import com.resource.mark_net.converfactory.StringConverterFactory;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.interceptor.ApiRequestInterceptor;
import com.resource.mark_net.utils.CacheUtil;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by newbiefly on 2016/8/5.
 */
public class Retrofit2Client  implements RetrofitService {

    private final Retrofit.Builder mRetrofit;
    private final Map<String, Object> mServiceInstances = new HashMap<String, Object>();
    private static Retrofit2Client mRetrofit2Client;

    private Retrofit2Client() {
        mRetrofit = new Retrofit.Builder()
                //设置OKHttpClient
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //String转换器
                .addConverterFactory(StringConverterFactory.create())
                //gson转化器
                .addConverterFactory(GsonConverterFactory.create());
    }
    public static Retrofit2Client getInstance() {
        if (mRetrofit2Client == null) {
            synchronized (Retrofit2Client.class) {
                if (mRetrofit2Client == null) {
                    mRetrofit2Client = new Retrofit2Client();
                }
            }
        }
        return mRetrofit2Client;
    }

    private OkHttpClient getClient(UrlFactory url) {
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        OkHttpClient.Builder build = client.newBuilder()
                .readTimeout(url.getTimeout(), TimeUnit.SECONDS)
                .connectTimeout(url.getTimeout(), TimeUnit.SECONDS);

        if (url.isDownLoad() && url.getDownloadInterceptor() != null) {
            build.addInterceptor(url.getDownloadInterceptor());
        }

        if (!url.isHardUrl()) {
            build.addInterceptor(new ApiRequestInterceptor());
        }

        if (url.isCache()) {
            //设置Cache目录
            build.addNetworkInterceptor(OkHttpUtils.getOfflineCacheControlInterceptor());
            build.cache(CacheUtil.getCache(RxRetrofitApp.getApplication()));
        }

        if (RxRetrofitApp.isDebug()) {
            //打印日志
            build.addInterceptor(OkHttpUtils.getHttpLoggingInterceptor());
        }
        return build.build();
    }

    @Override
    public <T> T getApiService(Class<T> clazz,UrlFactory url) {
        if (url == null) {
            return null;
        }
        mRetrofit.client(getClient(url));
        T  service = mRetrofit.baseUrl(url.getBaseUrl()).build().create(clazz);
        return service;
    }
}
