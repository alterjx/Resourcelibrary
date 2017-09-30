package com.resource.mark_net.interceptor;

import java.io.IOException;

import com.resource.mark_net.listener.DownloadProgressListener;
import com.resource.mark_net.body.DownloadResponseBody;
import com.resource.mark_net.utils.log.LogUtils;

import okhttp3.Interceptor;
import okhttp3.Response;
/**
 * 成功回调处理

 */
public class DownloadInterceptor implements Interceptor {

    private DownloadProgressListener listener;

    public DownloadInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        LogUtils.w("pighand","apk update chain");
        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), listener))
                .build();
    }
}
