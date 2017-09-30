package com.resource.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.hss01248.dialog.DialogAssigner;
import com.hss01248.dialog.config.ConfigBean;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.resource.app.BaseActivity;
import com.resource.app.R;
import com.resource.app.adapter.PicDetailAdapter;
import com.resource.app.api.model.PicVipInfo;
import com.resource.app.api.PicDetailInfoApi;
import com.resource.app.api.model.PicVipInfoInput;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.MSGView;
import com.resource.app.model.PicCommonInfo;
import com.resource.app.model.UserInfo;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.mark_net.exception.CustomException;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import rx.functions.Action1;

/**
 */

public class PicDetaiActivity extends BaseActivity {

    private PicCommonInfo mPicInfo;
    private UserInfo mUserInfo;
    private PicDetailInfoApi mApi;
    private PicDetailAdapter mAdapter;
    private List<String> mImageUrls;
    private HashMap<String,String> mDomainMap;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.tv_pic_title)
    TextView mPicTitleTv;

    @Bind(R.id.msg_view)
    public MSGView mMsgView;
    public Dialog mDialog;


    @Override
    protected int getContentLayout() {
        return R.layout.pic_detail_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            exit();
            return;
        }
        mPicInfo = (PicCommonInfo)bundle.getSerializable(GlobalConstant.IntentConstant.PIC_DETAIL_INFO);
        if (mPicInfo == null) {
            exit();
            return;
        }

        mImageUrls = new ArrayList<>();
        mImageUrls.add(mPicInfo.firstUrl);

    }

    @Override
    protected void init() {
        super.init();
        mDomainMap = AppConfig.getDomianUrl();
        mAdapter = new PicDetailAdapter(mDomainMap.get(mPicInfo.domain_type));
        if (mImageUrls != null && !mImageUrls.isEmpty()) {
            mAdapter.addPics(mImageUrls);
            mPicTitleTv.setText(getString(R.string.number_part, 1, mPicInfo.amout));
        }

        mAdapter.setImageClickListener(new PicDetailAdapter.ImageOnClick() {
            @Override
            public void singleOnclick(int position) {
                exit();
            }
        });
        mUserInfo = AppConfig.getUserInfo();
        if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.id)) {
            exit();
            return;
        }
        HttpListener<List<PicVipInfo>> listener = new HttpListener<List<PicVipInfo>>() {
            @Override
            public void onSuccess(List<PicVipInfo> picVipInfos) {
                mMsgView.dismiss();
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                if (picVipInfos == null || picVipInfos.isEmpty() ) {
                    mMsgView.showEmpty();
                    return;
                }
                PicVipInfo vipInfo = picVipInfos.get(0);
                if (StringUtils.isNullOrEmpty(vipInfo.urls)) {
                    mMsgView.showEmpty();
                    return;
                }
                mImageUrls = java.util.Arrays.asList(vipInfo.urls.split(","));
                mAdapter.addPics(mImageUrls);
                mPicTitleTv.setText(getString(R.string.number_part, 1, mImageUrls.size()));
            }
            @Override
            public void onError(Throwable e) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                if (e != null && e instanceof CustomException) {
                    showErrorDialog(((CustomException) e).getResultCode());
                } else {
                    mMsgView.showError();
                }
            }

        };
        mApi = new PicDetailInfoApi(listener, this);
    }



    @Override
    protected void initContentView() {
        super.initContentView();
        if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.id)) {
            exit();
            return;
        }
        mMsgView.setSpinKitViewType(new CubeGrid());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mPicTitleTv.setText(getString(R.string.number_part, i + 1, mImageUrls.size()));
                if (i == 0) {
                    setIsGestureViewEnable(true);
                } else {
                    setIsGestureViewEnable(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setAdapter(mAdapter);
        mMsgView.setErrorClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMsgView.showLoading();
                loadData();
            }
        });
        mMsgView.dismiss();


        if (AppConfig.isVip() || mPicInfo.type == 1) {
            loadData();
        } else {
            mMsgView.dismiss();
            showErrorDialog(CustomException.NOT_VIP);
        }
        //监听菜单显示或隐藏
        mRxManager.on(GlobalConstant.RxBus.LOGIN_SUCCESS, new Action1<UserInfo>() {
            @Override
            public void call(UserInfo userInfo) {
                mUserInfo = userInfo;
                loadData();
            }
        });

        mRxManager.on(GlobalConstant.RxBus.UPGRADE_RESULT, new Action1<UserInfo>() {
            @Override
            public void call(UserInfo userInfo) {
                mUserInfo = AppConfig.getUserInfo();
                loadData();
            }
        });
    }


    @Override
    public boolean isUseGestureView() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDialog != null) {
            mDialog.show();
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0)
                    {
                        mDialog.dismiss();
                        exit();
                    }
                    return false;
                }
            });
        }
    }

    private void loadData() {
        mMsgView.showLoading();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        PicVipInfoInput infoInput = new PicVipInfoInput();
        infoInput.user_id = mUserInfo.id;
        infoInput.pic_id = mPicInfo.id;
        infoInput.type = mPicInfo.type;
        infoInput.loginTime = mUserInfo.loginTime;
        mApi.doHttp(infoInput);
    }


    private void showErrorDialog(final int errorCode) {
        String tip = "";
        String buttonTxt = that.getString(R.string.detail_dialog_vip_tip_second);

        switch (errorCode) {
            case CustomException.VIP_OUT_TIME:
                tip = getString(R.string.detail_dialog_time_out_vip_tip_content);
                mMsgView.dismiss();
                break;
            case CustomException.NOT_VIP:
                tip = getString(R.string.detail_dialog_not_vip_tip_content);
                mMsgView.dismiss();
                break;
            case CustomException.USER_LOGIN_EXCEPTION:
                tip = getString(R.string.detail_dialog_login_exception_tip_content);
                buttonTxt = that.getString(R.string.detail_dialog_login);
                mMsgView.dismiss();
                break;
            case CustomException.NO_USER:
                tip = getString(R.string.detail_dialog_login_exception_no_user);
                buttonTxt = that.getString(R.string.detail_dialog_login);
                mMsgView.dismiss();
                break;
            default:
                mMsgView.showError();
                break;
        }

        if (StringUtils.isNullOrEmpty(tip)) {
            return;
        }

        ConfigBean dialogConfigBean = DialogAssigner.getInstance().assignIosAlert(this,that.getString(R.string.detail_dialog_vip_tip_title), tip,  new MyDialogListener() {
            @Override
            public void onFirst() {
                exit();
            }

            @Override
            public void onSecond() {
            }

            @Override
            public void onThird() {
                if (errorCode == CustomException.USER_LOGIN_EXCEPTION || errorCode == CustomException.NO_USER)  {
                    Intent intent = new Intent();
                    intent.setClass(PicDetaiActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(PicDetaiActivity.this, PayActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);
                }
            }
        });
        mDialog = dialogConfigBean.setTitleColor(R.color.light_gray)
                .setMsgColor(R.color.google_red)
                .setBtnColor(R.color.gray_c5c5c5,0,R.color.main_color)
                .setBtnText(that.getString(R.string.detail_dialog_vip_tip_first),"",buttonTxt)
                .show();
        if (mDialog != null) {
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0)
                    {
                        mDialog.dismiss();
                        exit();
                    }
                    return false;
                }
            });
        }
    }
}
