package com.resource.app.api;

import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.trello.rxlifecycle.LifecycleProvider;

import java.util.TreeMap;


public class GetOrderInfoApi extends BaseApi<TreeMap<String,String>> {

    public GetOrderInfoApi(HttpListener listener, LifecycleProvider lifecycleProvider) {
        super(listener, lifecycleProvider);
    }

    @Override
    public UrlFactory setUrl() {
        return ApiConfig.GET_ORDER_INFO;
    }

    @Override
    public void doHttp(Object input) {
        HttpManager.getInstance().requestData(this,input);
    }
}
