package com.resource.mark_net.client;


import android.util.Log;
import com.resource.mark_net.interceptor.OfflineCacheControlInterceptor;
import com.resource.mark_net.utils.log.LogUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by newbiefly on 2016/8/5.
 */
public class OkHttpUtils {

    private static final int DEFAULT_TIME_OUT = 10000;
    private static final int TIMEOUT_READ = 15;
    private static final int TIMEOUT_CONNECTION = 15;
    private static final int CACHE_SIZE = 1024 * 1024 * 100;
    private static OkHttpClient mOkHttpClient;
    private static volatile OkHttpUtils mInstance;

    public static OkHttpUtils getInstance() {
        return initClient((OkHttpClient)null);
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if(mInstance == null) {
            Class var1 = OkHttpUtils.class;
            synchronized(OkHttpUtils.class) {
                if(mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }

        return mInstance;
    }
    public OkHttpClient getOkHttpClient() {
        return this.mOkHttpClient;
    }
    public OkHttpUtils(OkHttpClient okHttpClient) {
        if(okHttpClient == null) {
            this.mOkHttpClient = new OkHttpClient();
        } else {
            this.mOkHttpClient = okHttpClient;
        }
    }

    /**
     * 日志输出
     * 自行判定是否添加
     * @return
     */
    public static HttpLoggingInterceptor getHttpLoggingInterceptor(){
        //日志显示级别
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.w("RxRetrofit","Retrofit====Message:"+message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

    /**
     * 日志输出
     * 自行判定是否添加
     * @return
     */
    public static OfflineCacheControlInterceptor getOfflineCacheControlInterceptor(){
        return new OfflineCacheControlInterceptor();
    }
}
