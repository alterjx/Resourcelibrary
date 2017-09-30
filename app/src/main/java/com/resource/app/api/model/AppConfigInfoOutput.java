package com.resource.app.api.model;

import java.util.LinkedHashMap;

import com.resource.app.model.UpgradeModel;

public class AppConfigInfoOutput {
	public LinkedHashMap<String, String> urlDomainMap;
	public LinkedHashMap<String, String> picMenuMap;
	public LinkedHashMap<String, String> filmMenuMap;
	public LinkedHashMap<String, String> textMap;
	public UpgradeModel upgradeModel;
	public int isShowAdv; //  是否展示广告 0展示 1不展示 默认展示

}
