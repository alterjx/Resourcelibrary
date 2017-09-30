package com.resource.mark_net.common;

import java.lang.ref.SoftReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.widget.Toast;

import com.resource.mark_net.exception.CustomException;
import com.resource.mark_net.exception.HttpException;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.RxRetrofitApp;
import com.resource.mark_net.utils.log.LogUtils;
import com.trello.rxlifecycle.LifecycleProvider;
import rx.functions.Func1;

/**
 * 请求数据统一封装类
 * Created by newbiefly on 2017/05/02.
 */
public abstract class BaseApi<T> implements Func1<BaseResultEntity, T> {
    //rx生命周期管理
    private SoftReference<LifecycleProvider> mLifecycleProvider;
    /*回调*/
    private HttpListener listener;
    public BaseApi(HttpListener listener, LifecycleProvider rxAppCompatActivity) {
        setListener(listener);
        setLifecycleProvider(rxAppCompatActivity);
    }

    public BaseApi(HttpListener listener) {
        setListener(listener);
    }

    /**
     * 设置参数
     * @return
     */
    //public abstract Observable getObservable(Retrofit retrofit);

    public abstract UrlFactory setUrl();
    public abstract void doHttp(Object input);
    public UrlFactory getUrlFactory() {
        return setUrl();
    }
    public HttpListener getListener() {
        return listener;
    }
    public void setListener(HttpListener listener) {
        this.listener = listener;
    }
    public void setLifecycleProvider(LifecycleProvider rxAppCompatActivity) {
        this.mLifecycleProvider = new SoftReference(rxAppCompatActivity);
    }


    /*
         * 获取当前rx生命周期
         * @return
         */
    public LifecycleProvider getLifecycleProvider() {
        return mLifecycleProvider.get();
    }

    @Override
    public T call(BaseResultEntity httpResult) {
        if (httpResult == null) { //统一处理的错误
            throw new HttpException(HttpException.BODY_EMPTY);
        }

        if (!httpResult.success) { //需要的处理的才处理
            throw new CustomException(httpResult.errorCode);
        }
        try {
            if (httpResult.data == null) {
                return null;
            }
            return (T)JsonUtils.decode(httpResult.data,getType());
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpException(HttpException.CONVERT_ERROR);
        }
    }

    protected Type getType() {
        Type superclass;
        for(superclass = this.getClass().getGenericSuperclass(); superclass instanceof Class && !superclass.equals(BaseApi.class); superclass = ((Class)superclass).getGenericSuperclass()) {
            ;
        }
        if(superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        } else {
            ParameterizedType parameterized = (ParameterizedType)superclass;
            return parameterized.getActualTypeArguments()[0];
        }
    }
}
