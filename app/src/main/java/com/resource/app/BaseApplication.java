package com.resource.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hss01248.dialog.MyActyManager;
import com.hss01248.dialog.StyledDialog;
import com.mob.MobApplication;
import com.mob.MobSDK;
import com.resource.mark_net.utils.RxRetrofitApp;
import com.resource.mark_net.utils.log.LogUtils;

import cn.sharesdk.framework.ShareSDK;


public class BaseApplication extends MobApplication {
	@SuppressWarnings("unused")
	private static BaseApplication mContext;
	public static BaseApplication getInstance() {
		return mContext;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		MobSDK.init(mContext);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ShareSDK.getPlatformList();
			}
		}).start();
		ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
				.setDownsampleEnabled(true)
				.build();
		Fresco.initialize(this, config);
		//RxRetrofitApp.init(this, BuildConfig.DEBUG);
		RxRetrofitApp.init(this, BuildConfig.DEBUG);
		LogUtils.init(BuildConfig.DEBUG);
		StyledDialog.init(this);
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

			}

			@Override
			public void onActivityStarted(Activity activity) {

			}

			@Override
			public void onActivityResumed(Activity activity) {
				//在这里保存顶层activity的引用(内部以软引用实现)
				MyActyManager.getInstance().setCurrentActivity(activity);

			}

			@Override
			public void onActivityPaused(Activity activity) {

			}

			@Override
			public void onActivityStopped(Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

			}

			@Override
			public void onActivityDestroyed(Activity activity) {

			}
		});
	}

	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
