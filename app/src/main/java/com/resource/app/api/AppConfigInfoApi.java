package com.resource.app.api;

import com.resource.app.api.model.AppConfigInfoOutput;
import com.resource.app.api.model.PatchInfoOutput;
import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.trello.rxlifecycle.LifecycleProvider;

public class AppConfigInfoApi extends BaseApi<AppConfigInfoOutput> {

    public AppConfigInfoApi(HttpListener listener) {
        super(listener);
    }

    @Override
    public UrlFactory setUrl() {
        return ApiConfig.APP_CONFIG_API;
    }

    @Override
    public void doHttp(Object input) {
        HttpManager.getInstance().requestData(this, input);
    }
}
