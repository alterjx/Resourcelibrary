package com.resource.mark_net.common;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import com.resource.mark_net.common.DownState;
import com.resource.mark_net.http.HttpService;
import com.resource.mark_net.listener.HttpDownOnNextListener;

/**
 * apk下载请求数据基础类
 */

@Entity
public class DownInfo{
    @Id
    private long id;
    /*存储位置*/
    private String savePath;
    /*文件总长度*/
    private long countLength;
    /*下载长度*/
    private long readLength;
    /*下载唯一的HttpService*/
    @Transient
    private HttpService service;
    /*回调监听*/
    @Transient
    private HttpDownOnNextListener listener;
    /*超时设置*/
    private  int connectonTime=6;
    /*state状态数据库保存*/
    private int stateInte;
    /*url*/
    private String url;

    private boolean isNeedProgree;

    public DownInfo(String url, HttpDownOnNextListener listener) {
        setUrl(url);
        setListener(listener);
    }

    public DownInfo(String url) {
        setUrl(url);
    }

    @Generated(hash = 69620528)
    public DownInfo(long id, String savePath, long countLength, long readLength,
            int connectonTime, int stateInte, String url, boolean isNeedProgree) {
        this.id = id;
        this.savePath = savePath;
        this.countLength = countLength;
        this.readLength = readLength;
        this.connectonTime = connectonTime;
        this.stateInte = stateInte;
        this.url = url;
        this.isNeedProgree = isNeedProgree;
    }

    @Generated(hash = 928324469)
    public DownInfo() {
    }


    public DownState getState() {
        switch (getStateInte()){
            case 0:
                return DownState.START;
            case 1:
                return DownState.DOWN;
            case 2:
                return DownState.PAUSE;
            case 3:
                return DownState.STOP;
            case 4:
                return DownState.ERROR;
            case 5:
            default:
                return DownState.FINISH;
        }
    }

    public void setState(DownState state) {
        setStateInte(state.getState());
    }


    public int getStateInte() {
        return stateInte;
    }

    public void setStateInte(int stateInte) {
        this.stateInte = stateInte;
    }

    public HttpDownOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpDownOnNextListener listener) {
        this.listener = listener;
    }

    public HttpService getService() {
        return service;
    }

    public void setService(HttpService service) {
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }


    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }


    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getConnectonTime() {
        return this.connectonTime;
    }

    public void setConnectonTime(int connectonTime) {
        this.connectonTime = connectonTime;
    }

    public boolean isNeedProgree() {
        return isNeedProgree;
    }

    public void setNeedProgree(boolean needProgree) {
        isNeedProgree = needProgree;
    }

    public boolean getIsNeedProgree() {
        return this.isNeedProgree;
    }

    public void setIsNeedProgree(boolean isNeedProgree) {
        this.isNeedProgree = isNeedProgree;
    }
}
