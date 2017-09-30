package com.resource.app.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.resource.app.BaseFragment;
import com.resource.app.R;
import com.resource.app.activity.LoginActivity;
import com.resource.app.activity.MainActivity;
import com.resource.app.activity.PayActivity;
import com.resource.app.api.GetUserInfoApi;
import com.resource.app.api.UserExitApi;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.DeclareView;
import com.resource.app.customview.MarqueeTextView;
import com.resource.app.customview.WaveView;
import com.resource.app.model.UpgradeModel;
import com.resource.app.model.UserInfo;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.NumberUtil;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.AppUtil;
import com.resource.mark_net.utils.DialogUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import rx.functions.Action1;


public class MineFragment extends BaseFragment implements OnRefreshListener {

    private UserInfo mUserInfo;
    private GetUserInfoApi mApi;
    private HttpListener<UserInfo> mListener;
    private boolean mIsFirst = true;
    private String mNotice;
    private String mAppUrl;
    private UserExitApi mExitApi;
    @Bind(R.id.swipeToLoadLayout)
    public SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.rl_head)
    public RelativeLayout mHeadRl;
    @Bind(R.id.iv_head)
    public SimpleDraweeView mHeadIv;
    @Bind(R.id.wave_view)
    WaveView mWaveView;
    @Bind(R.id.bt_exit)
    Button mExitBt;
    @Bind(R.id.tv_login_or_money)
    TextView mLoginOrMoney;
    @Bind(R.id.tv_nick_name)
    public TextView mNickNameTv;
    @Bind(R.id.tv_vip_time)
    public TextView mVipTimeTv;
    @Bind(R.id.rl_notice)
    public RelativeLayout mNoticeRl;
    @Bind(R.id.tv_notice)
    public MarqueeTextView mNoticeTv;
    @Bind(R.id.tv_upgrade)
    public TextView mAppTv;
    private UpgradeModel mUpgradle;
    @Bind(R.id.rl_login_or_money)
    public RelativeLayout nLoginRl;
    @Bind(R.id.adv_line)
    public View mAdvLine;
    @Bind(R.id.rl_adult)
    public RelativeLayout nAdvRl;
    @Bind(R.id.iv_vip_type)
    public ImageView mVipIv;

    @OnClick(R.id.rl_notice)
    public void showNotice() {
        if (!StringUtils.isNullOrEmpty(mNotice)) {
            DeclareView noticeView = new DeclareView(that);
            noticeView.showAdvView();
            noticeView.setData(that.getString(R.string.mine_notice_title), mNotice);
            StyledDialog.buildCustom(noticeView, Gravity.CENTER).setCancelable(true, true).show();
        }

    }

    public void showDeclare() {
        if (!StringUtils.isNullOrEmpty(AppConfig.getAppDeclare())) {
            DeclareView declareView = new DeclareView(that);
            declareView.showAdvView();
            declareView.setContent(AppConfig.getAppDeclare());
            StyledDialog.buildCustom(declareView, Gravity.CENTER).setCancelable(true, true).show();
        }

    }

    @OnClick(R.id.rl_about_us)
    public void showAboutUs() {
        View view = LayoutInflater.from(that).inflate(R.layout.mine_fragment_about_us_item, null);
        TextView tvDeclare = (TextView) view.findViewById(R.id.tv_declare);
        tvDeclare.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvDeclare.getPaint().setAntiAlias(true);//抗锯齿
        tvDeclare.setText(Html.fromHtml(that.getString(R.string.mine_app_declare)));
        tvDeclare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeclare();
            }
        });
        TextView tvAppUrl = (TextView) view.findViewById(R.id.tv_app_url);
        tvAppUrl.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvAppUrl.getPaint().setAntiAlias(true);//抗锯齿
        tvAppUrl.setText(Html.fromHtml(that.getString(R.string.app_url)));
        tvAppUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isNullOrEmpty(mAppUrl)) {
                    DialogUtil.showLongPromptToast(that,that.getString(R.string.mine_app_url_empty_tip));
                }
            }
        });

        TextView tvAppVersion = (TextView) view.findViewById(R.id.tv_version);
        tvAppVersion.setText(that.getString(R.string.app_version, AppUtil.getCurrentVersionName(that)));

        SimpleDraweeView head = (SimpleDraweeView) view.findViewById(R.id.iv_pic);
        head.setActualImageResource(R.mipmap.ic_launcher);
        StyledDialog.buildCustom(view, Gravity.CENTER).setCancelable(true, true).show();
    }

    @OnClick(R.id.rl_contact_service)
    public void contactService() {
        ClipboardManager cm = (ClipboardManager) that.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(that.getString(R.string.app_contact_service_weixin));
        DialogUtil.showShortPromptToast(that, that.getString(R.string.app_contact_tip));
    }

    @OnClick(R.id.rl_get_code)
    public void getAppCode() {
        ClipboardManager cm = (ClipboardManager) that.getSystemService(Context.CLIPBOARD_SERVICE);
        if (mUserInfo == null) {
            goLogin();
            return;
        }
        if (StringUtils.isNullOrEmpty(mUserInfo.codeUrl)) {
            DialogUtil.showLongPromptToast(that,R.string.mine_not_vip_tip_content);
            return;
        }
        cm.setText(mUserInfo.codeUrl);
        DialogUtil.showShortPromptToast(that, that.getString(R.string.app_get_code_tip));
    }

    @OnClick(R.id.rl_get_id)
    public void getUserIdInfo() {
        ClipboardManager cm = (ClipboardManager) that.getSystemService(Context.CLIPBOARD_SERVICE);
        StringBuilder info = new StringBuilder();
        if (mUserInfo != null) {
            info.append(mUserInfo.id).append(",").append(mUserInfo.platform).append(",").append(mUserInfo.nickname).append("+支付信息：").append(AccountPreference.getInstance().getPayInfo());
        }
        cm.setText(info.toString());
        DialogUtil.showShortPromptToast(that, that.getString(R.string.app_get_info_tip));
    }

    @OnClick(R.id.iv_instruction)
    public void showIdInstruction() {
        View view = LayoutInflater.from(that).inflate(R.layout.mine_fragment_id_instrouction_item, null);
        StyledDialog.buildCustom(view, Gravity.CENTER).setCancelable(true, true).show();
    }

    public void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_cancle));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.newbiefly.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.codewechat.com");
        // 启动分享GUI
        oks.show(that);
    }

    @OnClick(R.id.bt_exit)
    public void appExit() {
        StyledDialog.buildIosAlert("\n" + that.getString(R.string.login_exit_tip) + "\n", "", new MyDialogListener() {
            @Override
            public void onFirst() {
                AppConfig.clearLoginInfo();
                MobclickAgent.onProfileSignOff();
                mExitApi.doHttp(mUserInfo);
                exitStateUI();
                mUserInfo = null;
            }

            @Override
            public void onSecond() {
            }

            @Override
            public void onThird() {
            }


        }).setTitleColor(R.color.light_gray)
                .setBtnColor(R.color.gray_c5c5c5, 0, R.color.main_color)
                .setBtnText(that.getString(R.string.login_exit_button_tip), "", that.getString(R.string.app_cancle))
                .show();
    }


    @OnClick(R.id.rl_checkout_app)
    public void checkoutNeedUpgrade() {
        if (mUpgradle != null && NumberUtil.stringToDouble(AppUtil.getCurrentVersionName(that)) < NumberUtil.stringToDouble(mUpgradle.versionCode)) {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.checkoutNeedUpgrade();
            }

        } else {
            DialogUtil.showShortPromptToast(that, UIUtils.getString(R.string.mine_current_version_is_new));
        }

    }


    @Override
    protected int getContentLayout() {
        return R.layout.mine_fragment;
    }

    @Override
    protected void init() {
        mUserInfo = AppConfig.getUserInfo();
        mNotice = AppConfig.getTextMap().get(GlobalConstant.AppTextConfig.MINE_NOTICE);
        mAppUrl = AppConfig.getTextMap().get(GlobalConstant.AppTextConfig.APP_NORMAL_URL);

        mUpgradle = AppConfig.getUpgradeData();


        mRxManager.on(GlobalConstant.RxBus.LOGIN_SUCCESS, new Action1<UserInfo>() {

            @Override
            public void call(UserInfo userInfo) {
                if (userInfo == null || StringUtils.isNullOrEmpty(userInfo.id)) {
                    return;
                }
                mUserInfo = userInfo;
                loginStateUI();
            }
        });

        mRxManager.on(GlobalConstant.RxBus.UPGRADE_RESULT, new Action1<UserInfo>() {
            @Override
            public void call(UserInfo userInfo) {
                mUserInfo = AppConfig.getUserInfo();
                loginStateUI();
            }
        });
        mListener = new HttpListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                mSwipeToLoadLayout.setRefreshing(false);
                if (userInfo == null || StringUtils.isNullOrEmpty(userInfo.id)) {
                    return;
                }
                AppConfig.updateUserInfoExceptLoginTime(userInfo);
                mUserInfo = AppConfig.getUserInfo();
                loginStateUI();
            }

            @Override
            public void onError(Throwable e) {
                mSwipeToLoadLayout.setRefreshing(false);
            }
        };
        mApi = new GetUserInfoApi(mListener, this);
        mExitApi = new UserExitApi(new HttpListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {

            }

            @Override
            public void onError(Throwable e) {

            }
        },this);
    }

    @Override
    protected void initHeadView() {
        super.initHeadView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mIsFirst && !hidden && null != mUserInfo) {
            mIsFirst = false;
            mApi.doHttp(mUserInfo);
        }
        if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.id)) {
            exitStateUI();
            AccountPreference.getInstance().setUserInfo("");
        }

        if (mUpgradle != null && NumberUtil.stringToDouble(AppUtil.getCurrentVersionName(that)) < NumberUtil.stringToDouble(mUpgradle.versionCode)) {
            mAppTv.setText(UIUtils.getString(R.string.mine_current_new_version));
            mAppTv.setTextColor(UIUtils.getColor(R.color.google_red));
        } else {
            mAppTv.setText(UIUtils.getString(R.string.mine_current_version, AppUtil.getCurrentVersionName(that)));
        }
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        exitStateUI();
        loginStateUI();
        if (StringUtils.isNullOrEmpty(mNotice)) {
            mNoticeRl.setVisibility(View.INVISIBLE);
        } else {
            mNoticeTv.setText(mNotice);
            mNoticeRl.setVisibility(View.VISIBLE);
        }

        mSwipeToLoadLayout.setOnRefreshListener(this);
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mHeadRl.getLayoutParams();
        mWaveView.setOnWaveAnimationListener(new WaveView.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0, 0, 0, (int) y + 2);
                mHeadRl.setLayoutParams(lp);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeToLoadLayout.isRefreshing()) {
            mSwipeToLoadLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (null != mUserInfo) {
            mApi.doHttp(mUserInfo);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.rl_login_or_money)
    public void goLogin() {
        if (mLoginOrMoney.getText().equals(getString(R.string.mine_login))) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
        } else if (mLoginOrMoney.getText().equals(getString(R.string.mine_vip))) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), PayActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);
        }

    }


    private void loginStateUI() {
        if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.id)) {
            return;
        }
        if (mHeadIv != null) {
            mHeadIv.setImageURI(mUserInfo.headIcon);
            mExitBt.setVisibility(View.VISIBLE);
        }
        mNickNameTv.setVisibility(View.VISIBLE);
        if (mSwipeToLoadLayout != null) {
            mSwipeToLoadLayout.setRefreshEnabled(true);
        }
        mNickNameTv.setText(mUserInfo.nickname);
        switch (mUserInfo.vipType) {
            case GlobalConstant.VipType.COMMON:
                mVipTimeTv.setVisibility(View.VISIBLE);
                mVipTimeTv.setText(that.getString(R.string.mine_user_common));
                mVipIv.setVisibility(View.GONE);
                break;
            case GlobalConstant.VipType.TIME_VIP:
                mVipTimeTv.setVisibility(View.VISIBLE);
                long time = mUserInfo.vipEndTime -  System.currentTimeMillis();
                if (time > 0) {
                    mVipTimeTv.setText(that.getString(R.string.app_pic_time_vip_day,getDay(time)));
                    mVipIv.setVisibility(View.VISIBLE);
                    mVipIv.setImageResource(R.mipmap.icon_vip_silver);
                } else {
                    mVipIv.setVisibility(View.GONE);
                    mVipTimeTv.setText(that.getString(R.string.app_pic_time_out_day));
                }

                break;
            case GlobalConstant.VipType.VIP:
                mVipTimeTv.setVisibility(View.VISIBLE);
                mVipTimeTv.setText(that.getString(R.string.mine_user_vip));
                mVipIv.setVisibility(View.VISIBLE);
                mVipIv.setImageResource(R.mipmap.icon_vip_gold);
                break;
        }
        mLoginOrMoney.setText(getString(R.string.mine_vip));
    }

    private void exitStateUI() {
        mHeadIv.setImageResource(R.mipmap.icon_default_head);
        mExitBt.setVisibility(View.GONE);
        mNickNameTv.setVisibility(View.GONE);
        mSwipeToLoadLayout.setRefreshEnabled(false);
        mLoginOrMoney.setText(getString(R.string.mine_login));
        mVipIv.setVisibility(View.GONE);
        mVipTimeTv.setVisibility(View.GONE);
    }

    private String getDay(long time) {
        return String.valueOf((time/(1000*60*60*24) + 1));
    }

}
