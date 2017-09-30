package com.resource.app.api;


import com.resource.app.model.UndefinedModel;
import com.resource.app.utils.AppConfig;
import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.StringUtils;
import com.trello.rxlifecycle.LifecycleProvider;


public class FilmUndefinedApi extends BaseApi<Boolean> {

    public FilmUndefinedApi(HttpListener listener, LifecycleProvider lifecycleProvider) {
        super(listener, lifecycleProvider);
    }

    @Override
    public UrlFactory setUrl() {
        UndefinedModel model = AppConfig.getManager();
        ApiConfig config = null;
        if (model == null || !model.isCanOperate || StringUtils.isNullOrEmpty(model.dUrl)) {
            return ApiConfig.with("").build();
        }
        config = ApiConfig.with(model.dUrl).build();
        return config;
    }

    @Override
    public void doHttp(Object input) {
        HttpManager.getInstance().requestData(this,input);
    }
}
