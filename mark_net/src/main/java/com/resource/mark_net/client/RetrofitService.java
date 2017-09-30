package com.resource.mark_net.client;

import com.resource.mark_net.http.UrlFactory;

/**
 * Created by newbiefly on 2016/7/16.
 */
public interface RetrofitService {
    <T> T getApiService(Class<T> clazz, UrlFactory url);
}
