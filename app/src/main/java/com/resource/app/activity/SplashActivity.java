package com.resource.app.activity;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.resource.app.BaseActivity;
import com.resource.app.R;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.PatchManager;
import com.resource.app.utils.PermissionHelper;
import com.resource.mark_net.common.DownInfo;
import com.resource.mark_net.listener.HttpDownOnNextListener;
import butterknife.Bind;
import cn.carbs.android.autozoominimageview.library.AutoZoomInImageView;
import okhttp3.ResponseBody;

public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getName();
    private Handler mHandlerLaunch;
    private PermissionHelper mPermissionHelper;
    private long mBegin;
    private boolean mIsBack;
    private AnimatorSet mAnimatorSet;
    private boolean mAnimalIsEnd;

    @Bind(R.id.iv_log)
    public SimpleDraweeView mHeadIv;
    @Bind(R.id.iv_zoom)
    public AutoZoomInImageView mZoomView;

    @Override
    protected void onCreate(Bundle arg0) {
        mBegin = System.currentTimeMillis();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(arg0);
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                runApp();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            runApp();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                runApp();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                mPermissionHelper.applyPermissions();
            }
        }
    }

    private void runApp() {
        mHandlerLaunch = new Handler();
        mHandlerLaunch.post(new Runnable() {
            @Override
            public void run() {
                AppConfig.getAppConfig();
                updateInfo();
            }
        });
    }

    @Override
    public boolean isUseGestureView() {
        return false;
    }


    @Override
    protected void init() {
    }

    @Override
    protected int getContentLayout() {
        return R.layout.launch_splashactivity;
    }

    @Override
    protected void initContentView() {
        mHeadIv.setActualImageResource(R.mipmap.ic_launcher);
        mHeadIv.setVisibility(View.GONE);
       /* PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.1f, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.1f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.1f, 1f);
        ObjectAnimator objectAnimators = ObjectAnimator.ofPropertyValuesHolder(mHeadIv, alpha, scaleX, scaleY);
        final ObjectAnimator move = ObjectAnimator.ofFloat(mHeadIv, "translationY", mHeadIv.getTranslationY(), (UIUtils.getScreenH()/2 - 95));
        move.setDuration(650);
        move.setInterpolator(new AccelerateInterpolator());
        move.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mAnimalIsEnd = true;
                goAdv();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play( objectAnimators);
        mAnimatorSet.setInterpolator(new AccelerateInterpolator());
        mAnimatorSet.setDuration(2300);
        mAnimatorSet.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) {

            }

            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                move.start();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animator) {

            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }
    private void updateInfo() {
        DownInfo info = new DownInfo();
        info.setListener(new HttpDownOnNextListener<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                AccountPreference.getInstance().setPatchVersion("");
                onComplete();
            }

            @Override
            public void onComplete() {
                long passedMillis = System.currentTimeMillis() - mBegin;
                long delayedMillis = 2200 - passedMillis;
                delayedMillis = delayedMillis < 0 ? 0 : delayedMillis;
                mHandlerLaunch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goAdv();
                    }
                }, delayedMillis);

            }

            @Override
            public void saveFile(ResponseBody responseBody) {
                PatchManager.savePatch(responseBody);
            }
        });
        PatchManager.loadPatchApk(info);
        mZoomView.post(new Runnable() {//iv即AutoZoomInImageView

            @Override
            public void run() {
                //简单方式启动放大动画
//                iv.init()
//                  .startZoomInByScaleDeltaAndDuration(0.3f, 1000, 1000);//放大增量是0.3，放大时间是1000毫秒，放大开始时间是1000毫秒以后
                //使用较为具体的方式启动放大动画
                mZoomView.init()
                        .setScaleDelta(0.25f)//放大的系数是原来的（1 + 0.2）倍
                        .setDurationMillis(2800)//动画的执行时间为1500毫秒
                        .setOnZoomListener(new AutoZoomInImageView.OnZoomListener(){
                            @Override
                            public void onStart(View view) {
                                /*//放大动画开始时的回调
                                mHeadIv.setVisibility(View.VISIBLE);
                                mAnimatorSet.start();*/
                            }
                            @Override
                            public void onUpdate(View view, float progress) {
                                //放大动画进行过程中的回调 progress取值范围是[0,1]
                            }
                            @Override
                            public void onEnd(View view) {
                                //放大动画结束时的回调
                                mAnimalIsEnd = true;
                                goAdv();
                            }
                        })
                        .start(500);//延迟1000毫秒启动
            }
        });
    }


    private void goAdv() {
        if (mIsBack || !mAnimalIsEnd) {
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        mIsBack = true;
        finish();
        return;
    }

}
