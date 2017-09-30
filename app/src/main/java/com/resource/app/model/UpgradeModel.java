package com.resource.app.model;

import java.io.Serializable;

/**
 * 版本更新接口输出
 */
public class UpgradeModel implements Serializable {
    public int forceUpdate;//强制更新标识 1:强制 0:无更新 2:普通更新
    public String versionCode;//版本名称 如：1.2.0
    public String updateDesc; //更新说明
    public String updateUrl;  //更新url
}
