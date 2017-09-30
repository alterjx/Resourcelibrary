package com.resource.mark_net.listener;

import rx.Observable;

/**
 * TODO: description
 * Date: 2017-05-02
 * @author newbiefly
 */
public abstract class HttpListener<T> {
    /**
     * 成功后回调方法
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 緩存回調結果
     * @param string
     */
    public void onCacheNext(String string){

    }

    /**
     * 成功后的ober返回，扩展链接式调用
     * @param observable
     */
    public void onNext(Observable observable){

    }

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     * @param e
     */
    public  void onError(Throwable e){

    }

    /**
     * 取消回調
     */
    public void onCancel(){

    }

    /**
     * 发请求之前
     */
    public void onPre() {

    }

    /**
     * 请求完成
     */
    public void onComplete() {

    }


}
