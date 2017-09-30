package com.resource.app.api;

import com.resource.app.api.model.PatchInfoOutput;
import com.resource.mark_net.common.BaseApi;
import com.resource.mark_net.http.ApiConfig;
import com.resource.mark_net.http.HttpManager;
import com.resource.mark_net.http.UrlFactory;
import com.resource.mark_net.listener.HttpListener;

public class PatchDownLoadApi extends BaseApi<PatchInfoOutput> {

    public PatchDownLoadApi(HttpListener listener) {
        super(listener);
    }

    @Override
    public UrlFactory setUrl() {
        return ApiConfig.PATCH_DOWNLOAD_API;
    }

    @Override
    public void doHttp(Object input) {
        if (input == null) {
            return;
        }
        HttpManager.getInstance().requestData(this, input);
    }
}
