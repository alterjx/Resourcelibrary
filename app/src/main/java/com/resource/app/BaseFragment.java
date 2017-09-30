package com.resource.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.resource.app.activity.LoginActivity;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.dialog.LoadingDialog;
import com.resource.app.manager.rx.RxManager;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends RxFragment {
	public static final String TAG = BaseFragment.class.getName();
	private CompositeSubscription mCompositeSubscription;
	protected Activity that;
	protected View mRoot;
	public RxManager mRxManager;
	private volatile LoadingDialog mProgressDialog;


	public void addSubscription(Subscription s) {
		if (this.mCompositeSubscription == null) {
			this.mCompositeSubscription = new CompositeSubscription();
		}

		this.mCompositeSubscription.add(s);
	}

	public void unsubscribe() {
		if (this.mCompositeSubscription != null) {
			this.mCompositeSubscription.clear();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		that = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (StringUtils.isEmpty(AccountPreference.getInstance().getDomainUrl())) {
			AppConfig.getAppConfig();
		}

		mRoot = LayoutInflater.from(that).inflate(getContentLayout(),container, false);
		mRxManager=new RxManager();
		ButterKnife.bind(this, mRoot);
		init();
		getIntentData(savedInstanceState);
		getIntentData();
		initHeadView();
		initSpaceBarView();
		initContentView();
		initData();
		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
        if (mRxManager != null) {
            mRxManager.clear();
        }
		unsubscribe();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public boolean onBackPressed(){return false;}

	protected void initSpaceBarView() {
		View spaceView = mRoot.findViewById(R.id.view_space);
		LogUtils.w("newbiefly","initSpaceBarView:");
		if (spaceView == null) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int statusBarHeight = UIUtils.getStatusBarHeight();
			LogUtils.w("newbiefly","statusBarHeight:" + statusBarHeight);
			LogUtils.w("newbiefly","statusBarHeight2:" + UIUtils.px2dip(statusBarHeight));
			if(statusBarHeight>0){
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtils.px2dip(statusBarHeight));
				spaceView.setLayoutParams(layoutParams);
			}
		}else{
			spaceView.setVisibility(View.GONE);
		}
	}
	protected void init(){}
	protected void getIntentData(Bundle savedInstanceState) {}
	protected void initData() {}
	protected void initContentView() {}
	protected void initHeadView() {}
	protected abstract int getContentLayout();
	protected void getIntentData() {}


	protected void finish() {
		that.finish();
	}
	/**
	 * 登陆
	 */
	protected void jumpToLogin(String targetActivityName, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(that, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtras(bundle);
		intent.putExtra(GlobalConstant.IntentConstant.TARGETACTIVITY_NAME, targetActivityName);
		startActivity(intent);
		that.overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
	}


	public synchronized void showStopDialog(int resId) {
		showStopDialog(resId, false);
	}


	public synchronized void showStopDialog(int resId,boolean isCancel) {
		if (mProgressDialog == null) {
			LoadingDialog dialog = new LoadingDialog(that, R.style.stopdialogstyle);
			dialog.setCancelable(true);
			mProgressDialog = dialog;
		}
		mProgressDialog.setCanceledOnTouchOutside(isCancel);
		mProgressDialog.setMessage(that.getString(resId));
		mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {

				if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0) {
					return false;
				}
				return true;
			}
		});
		if (!mProgressDialog.isShowing()) {
			try {
				mProgressDialog.show();
			} catch (WindowManager.BadTokenException e) {
			}
		}
	}

	public void dismissStopDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	public boolean isProgressDialogShowing() {
		return mProgressDialog != null && mProgressDialog.isShowing();
	}
}
