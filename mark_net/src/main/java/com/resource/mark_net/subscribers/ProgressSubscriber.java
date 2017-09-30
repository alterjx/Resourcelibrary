/**
 * Copyright (C) 2006-2017 Tuniu All rights reserved
 */
package com.resource.mark_net.subscribers;

import java.lang.ref.SoftReference;

import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.ErrorUtil;
import com.trello.rxlifecycle.LifecycleProvider;
import rx.Subscriber;

/**
 * TODO: description
 * Date: 2017-05-02
 *
 * @author newbiefly
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    private HttpListener mSubscriberOnNextListener;
    /*软引用反正内存泄露*/
    private SoftReference<LifecycleProvider> mLifecycleProvider;
    /*请求数据*/
    private BaseApi api;


    /**
     * 构造
     *
     * @param api
     */
    public ProgressSubscriber(BaseApi api) {
        this.api = api;
        this.mSubscriberOnNextListener = api.getListener();
        this.mLifecycleProvider = new SoftReference<>(api.getLifecycleProvider());
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onPre();
        }
        /*缓存并且有网*//*
        if (api.isCache() && AppUtil.isNetworkAvailable(RxRetrofitApp.getApplication())) {
             *//*获取缓存数据*//*
            CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(api.getUrl());
            if (cookieResulte != null) {
                long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                if (time < api.getCookieNetWorkTime()) {
                    if (mSubscriberOnNextListener.get() != null) {
                        mSubscriberOnNextListener.get().onCacheNext(cookieResulte.getResulte());
                    }
                    onCompleted();
                    unsubscribe();
                }
            }
        }*/
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onComplete();
        }
    }

    /**
     * 对错误进行统一处理
     * @param t
     */
    @Override
    public void onError(Throwable t) {
        try {
            ErrorUtil.errorLog(t);
            ErrorUtil.errorDo(t);
            if (mSubscriberOnNextListener != null) {
                mSubscriberOnNextListener.onError(t);
            }

        } catch (Exception e) {
            if (mSubscriberOnNextListener != null) {
                mSubscriberOnNextListener.onError(e);
            }

        }
       /* *//*需要緩存并且本地有缓存才返回*//*
        if (api.isCache()) {
            Observable.just(api.getUrl()).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    errorLog(e);
                }

                @Override
                public void onNext(String s) {
                    *//*获取缓存数据*//**//*
                    CookieResulte cookieResulte = CookieDbUtil.getInstance().queryCookieBy(s);
                    if (cookieResulte == null) {
                        throw new HttpException("网络错误");
                    }
                    long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                    if (time < api.getCookieNoNetWorkTime()) {
                        if (mSubscriberOnNextListener.get() != null) {
                            mSubscriberOnNextListener.get().onCacheNext(cookieResulte.getResulte());
                        }
                    } else {
                        CookieDbUtil.getInstance().deleteCookie(cookieResulte);
                        throw new HttpException("网络错误");
                    }*//*
                }
            });
        } else {
            errorLog(e);
        }*/
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onSuccess(t);
        }
    }
}
