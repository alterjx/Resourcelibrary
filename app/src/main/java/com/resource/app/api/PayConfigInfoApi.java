package com.resource.app.api;

import com.resource.app.api.model.PayConfigInfoOutput;
import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.trello.rxlifecycle.LifecycleProvider;

public class PayConfigInfoApi extends BaseApi<PayConfigInfoOutput> {

    public PayConfigInfoApi(HttpListener listener,LifecycleProvider lifecycleProvider) {
        super(listener,lifecycleProvider);
    }

    @Override
    public UrlFactory setUrl() {
        return ApiConfig.PAY_CONFIG_API;
    }

    @Override
    public void doHttp(Object input) {
        HttpManager.getInstance().requestData(this, input);
    }
}
