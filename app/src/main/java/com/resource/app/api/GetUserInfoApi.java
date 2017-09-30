package com.resource.app.api;

import com.resource.app.model.UserInfo;
import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.trello.rxlifecycle.LifecycleProvider;


public class GetUserInfoApi extends BaseApi<UserInfo> {

    public GetUserInfoApi(HttpListener listener, LifecycleProvider lifecycleProvider) {
        super(listener, lifecycleProvider);
    }

    @Override
    public UrlFactory setUrl() {
        return ApiConfig.with("pic_server/user/getUserInfo").setRetryCount(6).build();
    }

    @Override
    public void doHttp(Object input) {
        HttpManager.getInstance().requestData(this,input);
    }
}
