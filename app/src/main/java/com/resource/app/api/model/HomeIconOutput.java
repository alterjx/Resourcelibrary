package com.resource.app.api.model;

import java.io.Serializable;

/**
 * 首页icon配置 接口输出参数
 * Created by yangzhiling on 2016/3/11.
 */
public class HomeIconOutput implements Serializable {
    public HomeIcon topBannerIconShowing; // 首页顶部轮播图page焦点
    public HomeIcon topBannerIconUnShowing; //首页顶部轮播图page非焦点
    public HomeIcon exchangeCityButtonIcon; //交换城市按钮图标，可旋转
    public HomeIcon exchangeCityButtonIconInside; //交换城市按钮中间图标，不旋转
    public HomeIcon goAndBackSwitchIconOff; //往返开关（关）
    public HomeIcon goAndBackSwitchIconOn; //往返开关（开）
    public HomeIcon searchButtonIcon; //搜索按钮图标
    public HomeIcon specialOffer; //特价机票
    public HomeIcon myOrder; //我的订单
    public HomeIcon flightDynamic; //航班动态 add by 8.0.4
    public HomeIcon discountSubscribe; //低价订阅 add by 8.0.8
    public String dataString; //日期
}
