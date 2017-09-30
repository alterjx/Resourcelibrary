package com.resource.mark_net.interceptor;


import java.io.IOException;

import com.resource.mark_net.common.HttpRequestExtendParam;
import com.resource.mark_net.utils.AppUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.RxRetrofitApp;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by newbiefly on 2016/6/25.
 *
 * 离线读取本地缓存，在线获取最新数据(读取单个请求的请求头，亦可统一设置)
 */
public class ApiRequestInterceptor implements Interceptor {

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter("c", getNewSchemaExtendParamJson());

        oldRequest = oldRequest.newBuilder()
                .header("Accept", "application/json").url(authorizedUrlBuilder.build()).build();

        return chain.proceed(oldRequest);
    }

    /**
     * 返回新接口方案定义的Client扩展参数的json串
     *
     * @return
     */
    public static String getNewSchemaExtendParamJson() {
        String result = "";
        try {
            HttpRequestExtendParam extendParam = new HttpRequestExtendParam();
            extendParam.setV(AppUtil.getCurrentVersionName(RxRetrofitApp.getApplication()));
            result = JsonUtils.encode(extendParam);
        } catch (RuntimeException e) {
        }
        return result;
    }
}

