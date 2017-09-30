package com.resource.mark_net.http;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import com.resource.mark_net.client.Retrofit2Client;
import com.resource.mark_net.common.DownInfo;
import com.resource.mark_net.interceptor.DownloadInterceptor;
import com.resource.mark_net.common.DownState;
import com.resource.mark_net.exception.HttpException;
import com.resource.mark_net.exception.RetryWhenNetworkException;
import com.resource.mark_net.subscribers.ProgressDownSubscriber;
import com.resource.mark_net.utils.AppUtil;
import com.resource.mark_net.utils.DbDownUtil;
import com.resource.mark_net.utils.log.LogUtils;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * http下载处理类
 */
public class HttpDownManager {
    /*记录下载数据*/
    private Set<DownInfo> downInfos;
    /*回调sub队列*/
    private HashMap<String,ProgressDownSubscriber> subMap;
    /*单利对象*/
    private volatile static HttpDownManager INSTANCE;
    /*数据库类*/
    private DbDownUtil db;

    private HttpDownManager(){
        downInfos=new HashSet<>();
        subMap=new HashMap<>();
        //db= DbDownUtil.getInstance();
    }

    /**
     * 获取单例
     * @return
     */
    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * 开始下载
     */
    public void startDown(final DownInfo info){
        /*正在下载不处理*/
        if(info==null||subMap.get(info.getUrl())!=null){
            subMap.get(info.getUrl()).setDownInfo(info);
            return;
        }
        /*添加回调处理类*/
        ProgressDownSubscriber subscriber=new ProgressDownSubscriber(info);
        /*记录回调sub*/
        subMap.put(info.getUrl(),subscriber);
        /*获取service，多次请求公用一个sercie*/
        HttpService httpService;
        if (downInfos.contains(info)) {
            httpService = info.getService();
        } else {
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            UrlFactory factory = ApiConfig.with(AppUtil.getRelUrl(info.getUrl())).hardUrl(AppUtil.getBasUrl(info.getUrl())).download(interceptor).setTimeOut(info.getConnectonTime()).build();
            httpService = Retrofit2Client.getInstance().getApiService(HttpService.class, factory);
            info.setService(httpService);
            downInfos.add(info);
        }
        /*得到rx对象-上一次下載的位置開始下載*/
        httpService.download("bytes=" + info.getReadLength() + "-",info.getUrl())
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                   /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*读取下载写入文件*/
                .map(new Func1<ResponseBody, DownInfo>() {
                    @Override
                    public DownInfo call(ResponseBody responseBody) {
                        try {
                            LogUtils.w("pighand","apk length:" + responseBody.contentLength());
                            if (info.getListener() != null) {
                                info.getListener().saveFile(responseBody);
                            }
                        } catch (Exception e) {
                            /*失败抛出异常*/
                            throw new HttpException(e.getMessage());
                        }
                        return info;
                    }
                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*数据回调*/
                .subscribe(subscriber);

    }


    /**
     * 停止下载
     */
    public void stopDown(DownInfo info){
        if(info==null)return;
        info.setState(DownState.STOP);
        info.getListener().onStop();
        if(subMap.containsKey(info.getUrl())) {
            ProgressDownSubscriber subscriber=subMap.get(info.getUrl());
            subscriber.unsubscribe();
            subMap.remove(info.getUrl());
        }
        /*保存数据库信息和本地文件*/
        db.save(info);
    }


    /**
     * 暂停下载
     * @param info
     */
    public void pause(DownInfo info){
        if(info==null)return;
        info.setState(DownState.PAUSE);
        info.getListener().onPuase();
        if(subMap.containsKey(info.getUrl())){
            ProgressDownSubscriber subscriber=subMap.get(info.getUrl());
            subscriber.unsubscribe();
            subMap.remove(info.getUrl());
        }
        /*这里需要讲info信息写入到数据中，可自由扩展，用自己项目的数据库*/
        db.update(info);
    }

    /**
     * 停止全部下载
     */
    public void stopAllDown(){
        for (DownInfo downInfo : downInfos) {
            stopDown(downInfo);
        }
        subMap.clear();
        downInfos.clear();
    }

    /**
     * 暂停全部下载
     */
    public void pauseAll(){
        for (DownInfo downInfo : downInfos) {
            pause(downInfo);
        }
        subMap.clear();
        downInfos.clear();
    }


    /**
     * 返回全部正在下载的数据
     * @return
     */
    public Set<DownInfo> getDownInfos() {
        return downInfos;
    }

    /**
     * 移除下载数据
     * @param info
     */
    public void remove(DownInfo info){
        subMap.remove(info.getUrl());
        downInfos.remove(info);
    }

}
