package com.resource.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.resource.app.activity.LoginActivity;
import com.resource.app.activity.MainActivity;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.dialog.LoadingDialog;
import com.resource.app.manager.AppManager;
import com.resource.app.manager.rx.RxManager;
import com.resource.app.customview.GestureView;
import com.resource.app.utils.UIUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 */
public abstract class BaseActivity extends RxAppCompatActivity implements
        LoadingDialog.IProgressDialog{
    public static final String TAG = BaseActivity.class.getName();
    protected GestureView mGestureView;
    protected ViewGroup mContentView;
    protected boolean mIsActivityRun = true;
    private boolean isConfigChange=false;
    private boolean mIsNeedSetRootViewProperty = false;
    private volatile LoadingDialog mProgressDialog;
    protected Activity that;
    public RxManager mRxManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.setDebugMode(true);
        mIsActivityRun = true;
        isConfigChange=false;
        that = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
        setContentView(getContentLayout());
        mRxManager = new RxManager();
        ButterKnife.bind(this);
        initSavedInstanceState(savedInstanceState);
        getIntentData(savedInstanceState);
        getIntentData();
        init();
        initHeadView();
        initSpaceBarView();
        initContentView();
        initData();

    }
    protected  void initSavedInstanceState(Bundle savedInstanceState){}
    protected  void initSpaceBarView(){}
    protected void init(){}
    protected void getIntentData(Bundle savedInstanceState) {}
    protected void getIntentData() {}
    protected void initData() {}
    protected void initContentView() {}
    protected void initHeadView() {}
    protected abstract int getContentLayout();

    @Override
    public void setContentView(int layoutResID) {
        mContentView = (ViewGroup) LayoutInflater.from(BaseActivity.this).inflate(layoutResID, null);
        ViewGroup realContentView;
        if (mIsNeedSetRootViewProperty) {
            //如果需要头部颜色的话 生成一个独立的viewGroup来设置颜色 避免因为设置rootview颜色导致的xml里面设置背景色无效的问题
            LinearLayout linearLayout = new LinearLayout(BaseActivity.this);
            linearLayout.setTag("realContentView");
            linearLayout.addView(mContentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            realContentView = linearLayout;
        } else {
            realContentView = mContentView;
        }
        if (isUseGestureView()) {
            mGestureView = new GestureView(this, false);
            mGestureView.setGestureViewChanged(new GestureView.GestureViewChanged() {
                @Override
                public void onClosed() {//View关闭完成则结束Activity
                    if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                        UIUtils.hideSoftInput(getCurrentFocus().getWindowToken());
                    }
                    BaseActivity.this.close();
                }
            });
            mGestureView.addOneView(realContentView);
            mGestureView.setAsGestureViewScale(getAsGestureViewScale());
            mGestureView.setBackgroundColor(getResources().getColor(R.color.gesture_background_color));
            setContentView(mGestureView);

        } else {
            super.setContentView(realContentView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 在当前Activity调用finish的时候还原前一个Activity的位置
     */
    @Override
    public void finish() {
        if (mGestureView != null) {
            mGestureView.restorePreviousActivity();
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isConfigChange=true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActivityRun = false;
        if(mRxManager!=null) {
            mRxManager.clear();
        }
        if(!isConfigChange){
            AppManager.getAppManager().finishActivity(this);
        }
        ButterKnife.unbind(this);
    }

    /**
     * 批量清除handler
     *
     * @param handlers
     */
    public void cleanAllHandler(Handler... handlers) {
        for (Handler handler : handlers) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
        }
    }



    @Override
    public synchronized void showProgressDialog(int resId) {
        showProgressDialog(resId, true);
    }


    public synchronized void showProgressDialog(int resId, boolean isCancel,final boolean backIsCancel) {
        if (mProgressDialog == null) {
            LoadingDialog dialog = new LoadingDialog(that, R.style.loadingdialogstyle);
            dialog.setCancelable(true);
            mProgressDialog = dialog;
        }
        mProgressDialog.setCanceledOnTouchOutside(isCancel);
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return !backIsCancel;
                } else {
                    return false; // 默认返回 false
                }
            }
        });
        mProgressDialog.setMessage(that.getString(resId));
        if (!mProgressDialog.isShowing()) {
            try {
                mProgressDialog.show();
            } catch (WindowManager.BadTokenException e) {
            }
        }
    }

    public synchronized void showProgressDialog(int resId, boolean isCancel) {
        showProgressDialog(resId,isCancel,false);
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean isProgressDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }



    /**
     * 准备关闭当前activity，在调用finish()方法之前调用，用于某些类检测是或否需要setResult(RESULT_OK)等操作
     */
    protected void prepareDestroy() {

    }


    /**
     * 关闭当前activity,子类关闭自身时调用该类
     */
    protected void close() {
        BaseActivity.this.prepareDestroy();
        BaseActivity.this.finish();
    }


    public GestureView getmGestureView() {
        return mGestureView;
    }

    /**
     * ★★★★注意★★★★★ 只有在特殊时候使用，如果确定了Activity不需要使用手势滚动，必须直接复写isUseGestureView()return false
     * 而不是调用该方法
     * 设置手势View是否可用，用于在activity中在某些特殊时候禁用手势View
     */
    public void setIsGestureViewEnable(boolean isEnable) {
        if (mGestureView != null) {
            mGestureView.setGestureViewEnable(isEnable);
        }
    }


    /**
     * 是否使用手势View,如果子类不使用则必须重写该方法并返回false
     */
    public boolean isUseGestureView() {
        return true;
    }

    /**
     * 在屏幕左侧1/result宽度向右滑动时触发手势View
     * 如果子类中有向右滑动事件则必须重写该方法，否则事件会被覆盖
     */
    public int getAsGestureViewScale() {
        return 1;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void goMainActivity(boolean overridePendingTransition) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    /**
     * 登陆
     */
    private void jumpToLogin(String targetActivityName, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtras(bundle);
        intent.putExtra(GlobalConstant.IntentConstant.TARGETACTIVITY_NAME, targetActivityName);
        this.overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
        startActivity(intent);
    }
    public void exit() {
        this.finish();
        overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);
    }
}
