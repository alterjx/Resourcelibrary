package com.resource.app.utils;

import com.google.gson.reflect.TypeToken;
import com.resource.app.api.AppConfigInfoApi;
import com.resource.app.api.model.AppConfigInfoOutput;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.model.UndefinedModel;
import com.resource.app.model.UpgradeModel;
import com.resource.app.model.UserInfo;
import com.resource.app.preference.AccountPreference;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * TODO: description
 * Date: 2017-07-17
 */

public class AppConfig {
    public static boolean isLogin() {
        UserInfo  userInfo = getUserInfo();
        if (userInfo == null || StringUtils.isNullOrEmpty(userInfo.id)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isVip() {
        UserInfo  userInfo = getUserInfo();
        return userInfo.vipType > 0;
    }

    public static UserInfo getUserInfo() {
        UserInfo info = null;
        String userInfo = AccountPreference.getInstance().getUserInfo();
        if (StringUtils.isNullOrEmpty(userInfo)) {
            return null;
        } else {
            try {
                info = JsonUtils.decode(userInfo,UserInfo.class);
            } catch (Exception e) {
                LogUtils.e("com.resource.app", e.getMessage());
                return null;
            }
        }
        return info;
    }

    public static void updateUserInfoExceptLoginTime(UserInfo info ) {
        if (info == null) {
            return;
        }
        UserInfo localUserInfo = AppConfig.getUserInfo();
        if (localUserInfo != null) {
            info.loginTime = localUserInfo.loginTime;
        }
        AccountPreference.getInstance().setUserInfo(JsonUtils.encode(info));
    }

    public static UndefinedModel getManager() {
        UserInfo userInfo = getUserInfo();
        return userInfo == null ? null : userInfo.undefinedModel;
    }

    public static long getLoginTime() {
        UserInfo  userInfo = getUserInfo();
        if (userInfo == null) {
            return 0;
        }
        return userInfo.loginTime;
    }


    public static void clearLoginInfo() {
        AccountPreference.getInstance().setUserInfo("");
    }

    public static LinkedHashMap<String, String> getPicMenu() {

        LinkedHashMap<String, String> picMenus = new LinkedHashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(AccountPreference.getInstance().getPicMenu())) {
            try {
                picMenus = JsonUtils.decode(AccountPreference.getInstance().getPicMenu(),new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            } catch (Exception e) {
                LogUtils.e("com.resource.app", e.getMessage());
            }
        }
        return picMenus;
    }


    public static LinkedHashMap<String, String> getFilmMenu() {

        LinkedHashMap<String, String> filmMenus = new LinkedHashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(AccountPreference.getInstance().getFilmMenu())) {
            try {
                filmMenus = JsonUtils.decode(AccountPreference.getInstance().getFilmMenu(),new TypeToken<LinkedHashMap<String, String>>(){}.getType());
            } catch (Exception e) {
                LogUtils.e("com.resource.app", e.getMessage());
            }
        }
        return filmMenus;
    }

    public static HashMap<String, String> getDomianUrl() {
        HashMap<String, String>  domainUrls = new HashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(AccountPreference.getInstance().getDomainUrl())) {
            try {
                domainUrls = JsonUtils.decode(AccountPreference.getInstance().getDomainUrl(),new TypeToken<HashMap<String, String>>(){}.getType());
            } catch (Exception e) {
                LogUtils.e("com.resource.app", e.getMessage());
            }
        }

        if (domainUrls.isEmpty()) {
            getAppConfig();
        }
        return domainUrls;
    }


    public static String getSearchTab() {
        HashMap<String, String>  textMap = getTextMap();
        if (textMap == null || textMap.isEmpty()) {
            return "";
        }
        return textMap.get(GlobalConstant.AppTextConfig.SEARCH_PIC_TABS);
    }

    public static String getAppDeclare() {
        HashMap<String, String>  textMap = getTextMap();
        if (textMap == null || textMap.isEmpty()) {
            return "";
        }
        return textMap.get(GlobalConstant.AppTextConfig.APP_DECLARE);
    }

    public static HashMap<String, String> getTextMap() {
        HashMap<String, String>  textMap = new HashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(AccountPreference.getInstance().getNoticeUrl())) {
            try {
                textMap = JsonUtils.decode(AccountPreference.getInstance().getNoticeUrl(),new TypeToken<HashMap<String, String>>(){}.getType());
            } catch (Exception e) {
                LogUtils.e("com.resource.app", e.getMessage());
            }
        }
        return textMap;
    }

    public static UpgradeModel getUpgradeData() {
        UpgradeModel upgradle  = null;
        if (!StringUtils.isNullOrEmpty(AccountPreference.getInstance().getUpgradeData())) {
            try {
                upgradle = JsonUtils.decode(AccountPreference.getInstance().getUpgradeData(),UpgradeModel.class);
            } catch (Exception e) {
                LogUtils.e("com.resource.app", e.getMessage());
            }
        }
        return upgradle;
    }

    public static boolean isShowAdv() {
        return AccountPreference.getInstance().getShowAdv() == 0;
    }




    public static void getAppConfig() {
        new AppConfigInfoApi(new HttpListener<AppConfigInfoOutput>() {
            @Override
            public void onSuccess(AppConfigInfoOutput appConfigInfoOutput) {
                if (appConfigInfoOutput == null) {
                    return;
                }
                //图片菜单栏
                if (appConfigInfoOutput.picMenuMap != null && !appConfigInfoOutput.picMenuMap.isEmpty()) {

                    try {
                        String picMenuJson = JsonUtils.encode(appConfigInfoOutput.picMenuMap);
                        AccountPreference.getInstance().setPicMenu(picMenuJson);
                    } catch (Exception e) {
                        LogUtils.e("com.resource.app", e.getMessage());
                    }
                }

                //电影菜单栏
                if (appConfigInfoOutput.filmMenuMap != null && !appConfigInfoOutput.filmMenuMap.isEmpty()) {

                    try {
                        String filmMenuJson = JsonUtils.encode(appConfigInfoOutput.filmMenuMap);
                        AccountPreference.getInstance().setFilmMenu(filmMenuJson);
                    } catch (Exception e) {
                        LogUtils.e("com.resource.app", e.getMessage());
                    }
                }

                //图片域名
                if (appConfigInfoOutput.urlDomainMap != null && !appConfigInfoOutput.urlDomainMap.isEmpty()) {
                    try {
                        String domainUrl = JsonUtils.encode(appConfigInfoOutput.urlDomainMap);
                        AccountPreference.getInstance().setDomainUrl(domainUrl);
                    } catch (Exception e) {
                        LogUtils.e("com.resource.app", e.getMessage());
                    }
                }
                //通知
                if (appConfigInfoOutput.textMap != null && !appConfigInfoOutput.textMap.isEmpty()) {
                    try {
                        String domainUrl = JsonUtils.encode(appConfigInfoOutput.textMap);
                        AccountPreference.getInstance().setNoticeUrl(domainUrl);
                    } catch (Exception e) {
                        LogUtils.e("com.resource.app", e.getMessage());
                    }
                } else {
                    AccountPreference.getInstance().setNoticeUrl("");
                }
                /*//搜索标签
                AccountPreference.getInstance().setSearchTab(appConfigInfoOutput.searchTab);

                //app声明
                AccountPreference.getInstance().setAppDeclare(appConfigInfoOutput.appDeclare);*/

                //版本更新
                if (appConfigInfoOutput.upgradeModel != null) {
                    try {
                        String data = JsonUtils.encode(appConfigInfoOutput.upgradeModel);
                        AccountPreference.getInstance().setUpgradeData(data);
                    } catch (Exception e) {
                        LogUtils.e("com.resource.app", e.getMessage());
                    }
                } else {
                    AccountPreference.getInstance().setUpgradeData("");
                }

                AccountPreference.getInstance().setShowAdv(appConfigInfoOutput.isShowAdv);
            }
        }).doHttp(null);
    }

}
