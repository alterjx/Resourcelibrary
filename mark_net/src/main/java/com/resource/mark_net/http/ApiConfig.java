package com.resource.mark_net.http;


import com.resource.mark_net.interceptor.DownloadInterceptor;

public class ApiConfig extends UrlFactory {

    public static ApiConfig with(String uri) {
        return new ApiConfig(uri);
    }

    /**
     * 构造接口常量，Build模式使用
     *
     * @param uri        接口的相对路径
     */
    private ApiConfig(String uri) {
        mRelativePath = uri;
    }

    public ApiConfig setTimeOut(int timeOut) {
        mTimeout = timeOut;
        return this;
    }
    public ApiConfig setRetryCount (int retryCount ) {
        mRetryCount  = retryCount;
        return this;
    }

    public ApiConfig returnCall() {
        mReturnCall = true;
        return this;
    }

    public ApiConfig usePost() {
        mIsPost = true;
        return this;
    }
    public ApiConfig useHttps() {
        mIsHttps = true;
        return this;
    }
    public ApiConfig useCache() {
        mIsCache = true;
        return this;
    }

    public ApiConfig hardUrl(String hardUrl) {
        mHardUrl = hardUrl;
        mIsHardUrl = true;
        return this;
    }

    public ApiConfig download(DownloadInterceptor downloadInterceptor) {
        mIsDownLoad = true;
        mDownloadInterceptor = downloadInterceptor;
        return this;
    }

    public ApiConfig useCacheOnly() {
        mUseCacheOnly = true;
        return this;
    }

    public ApiConfig build() {
        if (mIsHardUrl) {
            mBaseUrl = mHardUrl;
            return this;
        }
        StringBuilder mUrlBuilder = new StringBuilder();
        if (mIsHttps) {
            mUrlBuilder.append("https://");
        } else {
            mUrlBuilder.append("http://");
        }
        mUrlBuilder.append("seemovie.duapp.com/");
        //mUrlBuilder.append("172.31.16.112:8080/");
        mBaseUrl = mUrlBuilder.toString();
        return this;
    }

    private ApiConfig() {
    }
    public static ApiConfig PIC_VIP_INFO = ApiConfig.with("pic_server/pic/getDetailPic").build();
    public static ApiConfig PIC_ＬIST_INFO = ApiConfig.with("pic_server/pic/getListPic").build();
    public static ApiConfig FILM_LIST_INFO = ApiConfig.with("pic_server/film/getListFilm").build();
    public static ApiConfig APP_SEARCH_INFO = ApiConfig.with("pic_server/search/search").build();
    public static ApiConfig VIDEO_SEARCH_INFO = ApiConfig.with("pic_server/film/searchFilm").build();
    public static ApiConfig PIC_SEARCH_INFO = ApiConfig.with("pic_server/pic/searchPic").build();
    public static ApiConfig USER_LOGIN_API = ApiConfig.with("pic_server/user/login").usePost().build();
    public static ApiConfig USER_EXIT_API = ApiConfig.with("pic_server/user/exit").usePost().build();
    public static ApiConfig APP_CONFIG_API = ApiConfig.with("pic_server/appConfig/getAppConfig").returnCall().build();
    public static ApiConfig PATCH_DOWNLOAD_API = ApiConfig.with("pic_server/patch/getPatchInfo").returnCall().build();
    public static ApiConfig VIDEO_VIP_INFO = ApiConfig.with("pic_server/film/getDetailFilm").build();
    public static ApiConfig GET_ORDER_INFO = ApiConfig.with("pic_pay_server/pay/getOrderInfo").hardUrl("http://seemovie.duapp.com/").usePost().setRetryCount(1).build();
    public static ApiConfig PAY_CONFIG_API = ApiConfig.with("pic_pay_server/config/getPayConfig").hardUrl("http://seemovie.duapp.com/").setRetryCount(5).build();

}