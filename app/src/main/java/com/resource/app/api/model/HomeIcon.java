package com.resource.app.api.model;

import java.io.Serializable;

/**
 * 首页icon配置
 * Created by yangzhiling on 2016/3/11.
 */
public class HomeIcon implements Serializable {
    public String title;//string  //广告名
    public String image;//:string  //广告图片
    public String url;//:string    //广告跳转url
    public String ad_des; // 广告描述 908 用于出行服务页面的 说明
}
