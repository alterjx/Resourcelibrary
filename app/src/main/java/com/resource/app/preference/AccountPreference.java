package com.resource.app.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.resource.app.BaseApplication;
import com.resource.app.constant.GlobalConstant;

public class AccountPreference {
	
	private static AccountPreference mAccountPreference;
	private SharedPreferences mSharedPreferences;
	private Editor mEditor;
	private String NAME = "userinfo";
	private Context mContext;
	
	private AccountPreference() {
		this.mContext = BaseApplication.getInstance();
		mSharedPreferences = mContext.getSharedPreferences(NAME, 0);
		mEditor = mSharedPreferences.edit();
	}
	
	public static synchronized AccountPreference getInstance() {
		
		if( null == mAccountPreference){
			synchronized (AccountPreference.class) {
				if(null == mAccountPreference){
					mAccountPreference = new AccountPreference();
				}
			}
		}
		return mAccountPreference;
	}
	
	  //  封装操作方法开始
    private void setItemInt(String key, int value) {
        mEditor.putInt(key, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }

    private int getItemInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    private void setItemBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }

    private boolean getItemBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    private void setItemString(String key, String value) {
        mEditor.putString(key, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }

    private String getItemString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    private void setItemFloat(String key, float value) {
        mEditor.putFloat(key, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }

    private float getItemFloat(String key) {
        return mSharedPreferences.getFloat(key, 0);
    }

    private void setItemLong(String key, long value) {
        mEditor.putLong(key, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }

    private long getItemLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }

	public String getPhone() {
		return getItemString("phone");
	}

	public void setPhone(String phone) {
		setItemString("phone", phone);
	}

	public String getPatchVersion() {
        return getItemString("patchversion");
    }

    public void setPatchVersion(String version) {
        setItemString("patchversion", version);
    }
    public void setUserInfo(String userInfo) {
        setItemString(GlobalConstant.IntentPreference.USER_INFO, userInfo);
    }
    public String getUserInfo() {
        return getItemString(GlobalConstant.IntentPreference.USER_INFO);
    }

    public void setPicMenu(String menu) {
        setItemString(GlobalConstant.IntentPreference.PIC_MENU, menu);
    }

    public String getPicMenu() {
        return getItemString(GlobalConstant.IntentPreference.PIC_MENU);
    }

    public void setDomainUrl(String domainUrl) {
        setItemString(GlobalConstant.IntentPreference.DOMAIN_URL, domainUrl);
    }

    public String getDomainUrl() {
        return getItemString(GlobalConstant.IntentPreference.DOMAIN_URL);
    }

    public void setNoticeUrl(String notice) {
        setItemString(GlobalConstant.IntentPreference.APP_NOTICE, notice);
    }

    public String getNoticeUrl() {
        return getItemString(GlobalConstant.IntentPreference.APP_NOTICE);
    }

    public String getHistoryData() {
        return getItemString(GlobalConstant.IntentPreference.SEARCH_HISTORY);
    }
    public void setHistoryData(String historyData) {
        setItemString(GlobalConstant.IntentPreference.SEARCH_HISTORY, historyData);
    }

    public void setSearchTab(String tab) {
        setItemString(GlobalConstant.IntentPreference.SEARCH_TAB, tab);
    }

    public String getSearchTab() {
        return getItemString(GlobalConstant.IntentPreference.SEARCH_TAB);
    }

    public void setUpgradeData(String data) {
        setItemString(GlobalConstant.IntentPreference.UPGRADE_DATA, data);
    }

    public String getUpgradeData() {
        return getItemString(GlobalConstant.IntentPreference.UPGRADE_DATA);
    }

    public void setAppDeclare(String declare) {
        setItemString(GlobalConstant.IntentPreference.APP_DECLARE, declare);
    }

    public String getAppDeclare() {
        return getItemString(GlobalConstant.IntentPreference.APP_DECLARE);
    }

    public void setPayInfo(String payInfo) {
        setItemString(GlobalConstant.IntentPreference.PAY_INFO, payInfo);
    }
    public String getPayInfo() {
        return getItemString(GlobalConstant.IntentPreference.PAY_INFO);
    }

    public void setShowAdv(int show) {
        setItemInt(GlobalConstant.IntentPreference.SHOW_ADV, show);
    }
    public int getShowAdv() {
        return getItemInt(GlobalConstant.IntentPreference.SHOW_ADV);
    }

    public void setFilmMenu(String menu) {
        setItemString(GlobalConstant.IntentPreference.FILM_MENU, menu);
    }

    public String getFilmMenu() {
        return getItemString(GlobalConstant.IntentPreference.FILM_MENU);
    }
}
