package com.resource.mark_net.http;

import com.resource.mark_net.common.BaseResultEntity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 */

public interface HttpService {
    @GET
    Observable<BaseResultEntity> loadData(@Url String url, @Query("d") Object var2);

    @POST
    Observable<BaseResultEntity> postData(@Url String url, @Body Object var2);

    @GET
    Call<BaseResultEntity> callData(@Url String url, @Query("d") Object var2);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String   fileUrl);

    /*断点续传下载接口*/
    @Streaming/*大文件需要加入这个判断，防止下载过程中写入到内存中*/
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String start, @Url String url);

}
