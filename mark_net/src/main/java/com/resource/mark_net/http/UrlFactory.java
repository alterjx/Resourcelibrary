package com.resource.mark_net.http;


import com.resource.mark_net.interceptor.DownloadInterceptor;

/**
 * REST接口地址
 */
public abstract class UrlFactory {
    protected static final int DEFAULT_TIME_OUT = 3;
    // 接口超时时间
    protected int mTimeout = DEFAULT_TIME_OUT;
    //有网情况下的本地缓存时间默认60秒
    protected int mCookieNetWorkTime = 60;
    //无网络的情况下本地缓存时间默认30天
    protected int mCokieNoNetWorkTime = 24 * 60 * 60 * 30;
    //失败后retry次数
    protected int mRetryCount = 1;
    //失败后retry延迟
    protected int mRetryDelay = 100;
    //失败后retry叠加延迟
    protected int mRetryIncreaseDelay = 10;
    // 接口的相对路径F
    protected String mRelativePath;
    // 接口的地址
    protected String mBaseUrl;
    // 接口写死的地址
    protected String mHardUrl;
    //是否为固定地址
    protected boolean mIsHardUrl;
    // 是否使用https连接
    protected boolean mIsHttps = false;
    // 是否走缓存
    protected boolean mIsCache = false;
    // 是否只走缓存，不走网络
    protected boolean mUseCacheOnly = false;
    // 默认GET，不是GET就是POST
    protected boolean mIsPost = false;
    // retrofit返回rxjava，否则返回Call<>类型
    protected boolean mReturnCall = false;
    // 一键切换为https开关
    private boolean mConvertToHttps = false;
    protected boolean mIsDownLoad = false;
    protected DownloadInterceptor mDownloadInterceptor;


    public int getCookieNetWorkTime() {
        return mCookieNetWorkTime;
    }
    public int getCokieNoNetWorkTime() {
        return mCokieNoNetWorkTime;
    }
    public boolean getReturnCall() {
        return mReturnCall;
    }
    public int getRetryCount() {
        return mRetryCount;
    }
    public int getRetryDelay() {
        return mRetryDelay;
    }
    public int getRetryIncreaseDelay() {
        return mRetryIncreaseDelay;
    }
    public String getBaseUrl() {
        return mBaseUrl;
    }
    public DownloadInterceptor getDownloadInterceptor() {
        return mDownloadInterceptor;
    }

    public String getRelativePath() {
        return mRelativePath;
    }
    public int getTimeout() {
        return mTimeout;
    }
    public boolean isHttps() {
        return mIsHttps;
    }
    public boolean isDownLoad() {
        return mIsDownLoad;
    }
    public boolean isHardUrl() {
        return mIsHardUrl;
    }
    public boolean isOnlyCache() {
        return mUseCacheOnly;
    }
    public boolean isCache() {
        return mIsCache;
    }
    public boolean isPost() {
        return mIsPost;
    }
    public void setConvertToHttpsEnable() {
        mConvertToHttps = true;
    }
    public boolean isConvertToHttps() {
        return mConvertToHttps;
    }
}
