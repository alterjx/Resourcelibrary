package com.resource.app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.resource.app.BaseApplication;
import com.resource.app.R;
import com.resource.app.constant.GlobalConstant;
import com.resource.mark_net.utils.log.LogUtils;

/**
 * @author Administrator
 * @time 2015-7-15 上午10:59:15
 * @des 和ui相关的工具类
 *
 * @version $Rev: 8 $
 * @updateAuthor $Author: admin $
 * @updateDate $Date: 2015-07-15 17:06:45 +0800 (星期三, 15 七月 2015) $
 * @updateDes TODO
 */
public class UIUtils{

	/** 得到Resouce对象 */
	public static Resources getResource() {
		return BaseApplication.getInstance().getResources();
	}

	/** 得到String.xml中的字符串 */
	public static String getString(int resId) {
		return getResource().getString(resId);
	}

	/** 得到String.xml中的字符串 */
	public static String getString(int resId, String content) {
		return getResource().getString(resId, content);
	}

	/** 得到String.xml中的字符串数组 */
	public static String[] getStringArr(int resId) {
		return getResource().getStringArray(resId);
	}

	/** 得到colors.xml中的颜色 */
	public static int getColor(int colorId) {
		return getResource().getColor(colorId);
	}

	/** 得到colors.xml中的颜色 */
	public static Drawable getDrawable(int colorId) {
		return getResource().getDrawable(colorId);
	}



	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		DisplayMetrics dm = BaseApplication.getInstance().getResources()
				.getDisplayMetrics();
		float density = dm.density;
		return (int) (dpValue * density + 0.5f);
	}

	/**
	 * 隐藏软键盘
	 * @param windowToken like this: EditText.getWindowToken()
	 */
	public static void hideSoftInput(IBinder windowToken) {

		InputMethodManager imm = (InputMethodManager) BaseApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
	}


	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(int pxValue) {
		final float scale = BaseApplication.getInstance().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	/**
	 * 隐藏软键盘
	 * xujun,add
	 */
	public static void hideKeyBord(EditText edt) {
		InputMethodManager imm = (InputMethodManager) BaseApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
	}

	public static int getStatusBarHeight(){
		int statusBarHeight = 0;
		int resourceId = BaseApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = BaseApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
		}
		LogUtils.w("statusBarHeight:", statusBarHeight + "");
		return statusBarHeight;
	}
	/**
	 * 获取屏幕宽度
	 */
	public static int getScreenW() {
		int w = 0;
		if (Build.VERSION.SDK_INT > 13) {
			Point p = new Point();
			((WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getSize(p);
			w = p.x;
		} else {
			w = ((WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getWidth();
		}
		return w;
	}


	/**
	 * 获取屏幕高度
	 */
	public static int getScreenH() {
		int w = 0;
		if (Build.VERSION.SDK_INT > 13) {
			Point p = new Point();
			((WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getSize(p);
			w = p.y;
		} else {
			w = ((WindowManager) BaseApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay().getHeight();
		}
		return w;
	}

	public static void setUnderLine(TextView textView, String content) {
		textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
		textView.getPaint().setAntiAlias(true);//抗锯齿
		textView.setText(Html.fromHtml(content));
	}

	/**
	 * 颜色变化过度
	 *
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public static Object evaluateColor(float fraction, Object startValue, Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (startA + (int) (fraction * (endA - startA))) << 24 |
				(startR + (int) (fraction * (endR - startR))) << 16 |
				(startG + (int) (fraction * (endG - startG))) << 8 |
				(startB + (int) (fraction * (endB - startB)));
	}


}
