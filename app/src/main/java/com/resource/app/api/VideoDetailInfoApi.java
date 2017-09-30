package com.resource.app.api;

import java.util.List;

import com.resource.app.model.VideoInfoModel;
import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;
import com.trello.rxlifecycle.LifecycleProvider;


public class VideoDetailInfoApi extends BaseApi<List<VideoInfoModel>> {

    public VideoDetailInfoApi(HttpListener listener, LifecycleProvider lifecycleProvider) {
        super(listener, lifecycleProvider);
    }

    @Override
    public UrlFactory setUrl() {
        return ApiConfig.VIDEO_VIP_INFO;
    }

    @Override
    public void doHttp(Object input) {
        HttpManager.getInstance().requestData(this,input);
    }
}
