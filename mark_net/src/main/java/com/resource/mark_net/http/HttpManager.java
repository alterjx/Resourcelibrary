package com.resource.mark_net.http;

import java.lang.ref.SoftReference;


import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.common.BaseResultEntity;
import com.resource.mark_net.client.Retrofit2Client;
import com.resource.mark_net.exception.RetryWhenNetworkException;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.subscribers.ProgressSubscriber;
import com.resource.mark_net.utils.ErrorUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.plugins.RxJavaHooks.onError;

/**
 * http交互处理类
 * Created by newbiefly on 2017/05/02.
 */
public class HttpManager {
    private volatile static HttpManager INSTANCE;

    //构造方法私有
    private HttpManager() {
    }

    //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 处理http请求
     *
     * @param basePar 封装的请求数据
     */
    public void requestData(final BaseApi basePar, Object input) {
        if (basePar == null || basePar.getUrlFactory() == null) {
            return;
        }
        final Object data = getRequestData(basePar, input);
        HttpService service = Retrofit2Client.getInstance().getApiService(HttpService.class, basePar.getUrlFactory());
        if (basePar.getUrlFactory().getReturnCall()) { //Call处理
            final HttpListener httpOnNextListener = basePar.getListener();
            Call<BaseResultEntity> call = service.callData(basePar.getUrlFactory().getRelativePath(),data);
            call.enqueue(new Callback<BaseResultEntity>() {
                @Override
                public void onResponse(Call<BaseResultEntity> call, Response<BaseResultEntity> response) {
                    if (httpOnNextListener == null ) {
                        return;
                    }
                    try {
                        httpOnNextListener.onSuccess(basePar.call(response.body()));
                    } catch (Exception e) {
                        if (e == null || StringUtils.isNullOrEmpty(e.getMessage())) {
                            LogUtils.e("com.resource.app_httpmanager","Exception is NUll" );
                        } else {
                            httpOnNextListener.onError(e);
                            ErrorUtil.errorLog(e);
                        }

                    }

                }

                @Override
                public void onFailure(Call<BaseResultEntity> call, Throwable t) {
                    ErrorUtil.errorLog(t);
                    if ( httpOnNextListener == null) {
                        return;
                    }
                    httpOnNextListener.onError(t);
                }
            });

        } else {
             /*rx处理*/
            ProgressSubscriber subscriber = new ProgressSubscriber(basePar);
            Observable observable = null;
            if (basePar.getUrlFactory().isPost()) {
                observable = service.postData(basePar.getUrlFactory().getRelativePath(),data).retryWhen(new RetryWhenNetworkException(basePar.getUrlFactory().getRetryCount(),
                        basePar.getUrlFactory().getRetryDelay(), basePar.getUrlFactory().getRetryIncreaseDelay()))
                        .compose(basePar.getLifecycleProvider().bindToLifecycle())
                        //.compose(basePar.getLifecycleProvider().bindUntilEvent(ActivityEvent.DESTROY))
                /*http请求线程*/
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                        .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                        .map(basePar);
            } else {
                observable = service.loadData(basePar.getUrlFactory().getRelativePath(),data).retryWhen(new RetryWhenNetworkException(basePar.getUrlFactory().getRetryCount(),
                        basePar.getUrlFactory().getRetryDelay(), basePar.getUrlFactory().getRetryIncreaseDelay()))
                        .compose(basePar.getLifecycleProvider().bindToLifecycle())
                        //.compose(basePar.getLifecycleProvider().bindUntilEvent(ActivityEvent.DESTROY))
                /*http请求线程*/
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                        .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                        .map(basePar);
            }
            if (observable == null) {
                return;
            }
        /*链接式对象返回*/
            HttpListener httpOnNextListener = basePar.getListener();
            if (httpOnNextListener != null) {
                httpOnNextListener.onNext(observable);
            }
        /*数据回调*/
            observable.subscribe(subscriber);
        }
    }

    public  void download(String url, final HttpListener listener) {
        if (listener == null) {
            return;
        }
        HttpService service = Retrofit2Client.getInstance().getApiService(HttpService.class,ApiConfig.with("").build());
        Call<ResponseBody> call = service.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onError(t);
            }
        });
    }

    /**
     * 获取请求的参数
     * @return
     */
    public static Object getRequestData(BaseApi api, Object d) {
        if (null == api || d == null) {
            return null;
        }
        Object data = null;
        String jsonString = null;
        try {
            jsonString = JsonUtils.encode(d);
        } catch (Exception e) {
            LogUtils.e("error",e.getMessage());
        }
        data = jsonString;
        return data;
    }
}
