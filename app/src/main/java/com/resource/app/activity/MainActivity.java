package com.resource.app.activity;


import java.io.File;
import java.util.ArrayList;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hss01248.dialog.DialogAssigner;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.resource.app.R;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.fragment.VideoMainFragment;
import com.resource.app.fragment.MineFragment;
import com.resource.app.fragment.PicMainFragment;
import com.resource.app.fragment.FilmMyListFragment;
import com.resource.app.fragment.SearchMainFragment;
import com.resource.app.manager.AppManager;
import com.resource.app.model.TabEntity;
import com.resource.app.model.UpgradeModel;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.AppPackageUtils;
import com.resource.app.utils.NumberUtil;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.common.DownInfo;
import com.resource.mark_net.http.HttpDownManager;
import com.resource.mark_net.listener.HttpDownOnNextListener;
import com.resource.app.BaseActivity;
import com.resource.mark_net.utils.AppUtil;
import com.resource.mark_net.utils.DialogUtil;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;

import butterknife.Bind;
import okhttp3.ResponseBody;
import rx.functions.Action1;


public class MainActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnKeyListener {
    private FilmMyListFragment mTempFragment;
    private PicMainFragment mPicMainFragment;
    private VideoMainFragment mFilMainFragment;
    private MineFragment mMineFragment;
    private SearchMainFragment mSearchMainFragment;
    private Bundle mBundleSavedInstanceState;
    private String[] mTitles;
    private int[] mIconUnselectIds = {R.mipmap.ic_girl_normal,R.mipmap.ic_video_normal,R.mipmap.ic_search_normal,R.mipmap.ic_mine_normal};
    private int[] mIconSelectIds = {R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected,R.mipmap.ic_search_selected,R.mipmap.ic_mine_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private static int tabLayoutHeight;
    private long mExitTime = 0;
    private UpgradeModel mUpgradle;
    private Dialog mProgressDialog = null;
    private Dialog mUpgradeDialog = null;
    private boolean mIsMustUp;


    @Bind(R.id.tab_layout)
    CommonTabLayout tabLayout;

    @Override
    protected int getContentLayout() {
        return R.layout.main_activity;
    }

    @Override
    protected void initSavedInstanceState(Bundle savedInstanceState) {
        super.initSavedInstanceState(savedInstanceState);
        mBundleSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void init() {
        mTitles = getResources().getStringArray(R.array.main_default_menus);
        //监听菜单显示或隐藏
        mRxManager.on(GlobalConstant.RxBus.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });
    }

    @Override
    protected void initHeadView() {
        super.initHeadView();
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        initTab();
        initFragment();
        checkoutNeedUpgrade();
    }

    @Override
    public boolean isUseGestureView() {
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        if (tabLayout != null) {
            outState.putInt(GlobalConstant.SavedInstanceStateConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            default:
                break;
        }
    }
    /**
     * 初始化tab
     */
    private void initTab() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }
            @Override
            public void onTabReselect(int position) {
            }
        });
        tabLayout.measure(0,0);
        tabLayoutHeight = tabLayout.getMeasuredHeight();
    }
    /**
     * 初始化碎片
     */
    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (mBundleSavedInstanceState != null) {
            mTempFragment = (FilmMyListFragment) getSupportFragmentManager().findFragmentByTag("mTempFragment");
            mPicMainFragment = (PicMainFragment) getSupportFragmentManager().findFragmentByTag("mPicMainFragment");
            mFilMainFragment = (VideoMainFragment) getSupportFragmentManager().findFragmentByTag("mFilMainFragment");
            mMineFragment = (MineFragment) getSupportFragmentManager().findFragmentByTag("mMineFragment");
            mSearchMainFragment = (SearchMainFragment)getSupportFragmentManager().findFragmentByTag("mSearchMainFragment");
            currentTabPosition = mBundleSavedInstanceState.getInt(GlobalConstant.SavedInstanceStateConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            mTempFragment = new FilmMyListFragment();
            mPicMainFragment = new PicMainFragment();
            mFilMainFragment = new VideoMainFragment();
            mMineFragment = new MineFragment();
            mSearchMainFragment = new SearchMainFragment();
            transaction.add(R.id.fl_body, mTempFragment, "mTempFragment");
            transaction.add(R.id.fl_body, mPicMainFragment, "mPicMainFragment");
            transaction.add(R.id.fl_body, mFilMainFragment, "mFilMainFragment");
            transaction.add(R.id.fl_body, mSearchMainFragment, "mSearchMainFragment");
            transaction.add(R.id.fl_body, mMineFragment, "mMineFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        tabLayout.setCurrentTab(currentTabPosition);
    }

    /**
     * 切换
     */
    private void SwitchTo(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mTempFragment);
        switch (position) {
            //美女
            case 0:
                transaction.hide(mMineFragment);
                transaction.hide(mFilMainFragment);
                transaction.hide(mSearchMainFragment);
                transaction.show(mPicMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //电影
            case 1:
                transaction.hide(mMineFragment);
                transaction.hide(mPicMainFragment);
                transaction.hide(mSearchMainFragment);
                transaction.show(mFilMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //搜索
            case 2:
                transaction.hide(mFilMainFragment);
                transaction.hide(mPicMainFragment);
                transaction.hide(mMineFragment);
                transaction.show(mSearchMainFragment);
                transaction.commitAllowingStateLoss();
                break;

            //我的
            case 3:
                transaction.hide(mFilMainFragment);
                transaction.hide(mPicMainFragment);
                transaction.hide(mSearchMainFragment);
                transaction.show(mMineFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单显示隐藏动画
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide){
        final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if(!showOrHide){
            valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 1, 0);
        }else{
            valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 0, 1);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();
                tabLayout.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator,alpha);
        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
        if (mUpgradle.forceUpdate != GlobalConstant.AppUpgrade.FORCE_UPDATE) {
            return false;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (mUpgradeDialog != null) {
                mUpgradeDialog.dismiss();
                mUpgradeDialog = null;
            }
            AppManager.getAppManager().AppExit(that,true);
        }
        return false;
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getApplicationContext(), UIUtils.getString(R.string.app_exit_tip),
                    Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (mUpgradeDialog != null) {
                mUpgradeDialog.dismiss();
                mUpgradeDialog = null;
            }
            AppManager.getAppManager().AppExit(that,true);
        }
    }

    public void checkoutNeedUpgrade() {
        mUpgradle = AppConfig.getUpgradeData();
        if (mUpgradle == null || StringUtils.isNullOrEmpty(mUpgradle.updateUrl) || NumberUtil.stringToDouble(AppUtil.getCurrentVersionName(that)) >= NumberUtil.stringToDouble(mUpgradle.versionCode)) {
            return;
        }

        mIsMustUp = mUpgradle.forceUpdate == GlobalConstant.AppUpgrade.FORCE_UPDATE;
        String sencond = getString(R.string.app_upgrade_update_cancle);

        mUpgradeDialog = DialogAssigner.getInstance().assignIosAlert(this,that.getString(R.string.app_upgrade_title), mUpgradle.updateDesc,  new MyDialogListener() {
            @Override
            public void onFirst() {
                if (mIsMustUp) {
                    AppManager.getAppManager().AppExit(that,true);
                }
            }

            @Override
            public void onSecond() {
            }

            @Override
            public void onThird() {
                if (StringUtils.isNullOrEmpty(AppUtil.getSDPath())) {
                    DialogUtil.showLongPromptToast(MainActivity.this,UIUtils.getString(R.string.app_no_more_space));
                    return;
                }
                downLoadNew(mUpgradle.updateUrl);
            }
        }).setBtnColor(R.color.gray_c5c5c5,0,R.color.main_color)
                .setBtnText(sencond,"",that.getString(R.string.app_upgrade_update))
                .show();
        mUpgradeDialog.setOnKeyListener(this);
    }
    private void downLoadNew(String url) {
        final DownInfo info = new DownInfo();
        info.setUrl(url);
        info.setReadLength(0);
        info.setIsNeedProgree(true);
        info.setNeedProgree(true);
        info.setConnectonTime(5);
        info.setSavePath(AppUtil.getSDPath() +File.separator+ url.substring(url.lastIndexOf("/") + 1));
        info.setListener(new HttpDownOnNextListener<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                DialogUtil.showShortPromptToast(MainActivity.this,UIUtils.getString(R.string.app_download_fail));
                if (mUpgradeDialog != null) {
                    mUpgradeDialog.show();
                } else {
                    checkoutNeedUpgrade();
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onComplete() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                if (mUpgradeDialog != null && mIsMustUp) {
                    mUpgradeDialog.show();
                }
                LogUtils.w("pighand", "apk savePath:" + info.getSavePath() );
                AppPackageUtils.installApkFile(MainActivity.this, info.getSavePath());
            }

            @Override
            public void onStart() {
                if (mProgressDialog == null) {
                    mProgressDialog = DialogAssigner.getInstance().assignProgress(MainActivity.this,UIUtils.getString(R.string.app_downloading),true).show();
                    mProgressDialog.setOnKeyListener(MainActivity.this);
                } else {
                    mProgressDialog.show();
                }
            }
            @Override
            public void saveFile(ResponseBody responseBody) {
                try {
                    AppUtil.writeCache(responseBody,info.getSavePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.w("pighand", "apk saveFile error:" + e.getMessage());
                    onError(e);
                }
            }
            @Override
            public void updateProgress(long readLength, long countLength) {
                if (mProgressDialog != null) {
                    StyledDialog.updateProgress(mProgressDialog,(int)(readLength * 100 / countLength),100,UIUtils.getString(R.string.app_downloading),true);
                }
            }
        });
        HttpDownManager.getInstance().startDown(info);
    }
}
