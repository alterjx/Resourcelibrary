package com.resource.app.model;

public class UserInfo {
	public String id;
	public String nickname;
	public int gender; //0是男  1是女 3未知
	public String headIcon;
	public String platform;
	public int vipType; //0普通用户 88银牌会员 99终身会员
	public String codeUrl;
	public int point; //用户积分
	public long loginTime; //登录时间
	public long registerTime; //注册时间
	public long vipStartTime; 
	public long vipEndTime;
	public UndefinedModel undefinedModel;

}
