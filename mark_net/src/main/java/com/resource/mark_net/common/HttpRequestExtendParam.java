package com.resource.mark_net.common;

public class HttpRequestExtendParam {
    private String v; //String APP当前的版本号
    private long loginTime;
    private int p;  //int APP的渠道P值

    public String getV() {
        return v;
    }
    public void setV(String v) {
        this.v = v;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
}
