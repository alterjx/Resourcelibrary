package com.resource.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hss01248.dialog.StyledDialog;
import com.resource.app.BaseActivity;
import com.resource.app.CustomHandler;
import com.resource.app.R;
import com.resource.app.api.UserLoginApi;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.MSGView;
import com.resource.app.model.UserInfo;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.DialogUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;


public class LoginActivity extends BaseActivity implements View.OnClickListener,Handler.Callback {

    private static final int MSG_AUTH_CANCEL = 1;
    private static final int MSG_AUTH_ERROR= 2;
    private static final int MSG_AUTH_COMPLETE = 3;
    private UserLoginApi mApi;
    private HttpListener<UserInfo> mListener;
    private String mTargetActivity;
    private Bundle mTargetBundle;
    private Platform[] mPlatformlist;
    private Handler mPlatformHandler = new PlatformHandler(this);
    private Handler handler = new Handler(Looper.getMainLooper(), this);
    private static class PlatformHandler extends CustomHandler<LoginActivity> {
        PlatformHandler(LoginActivity target) {
            super(target);
        }

        @Override
        public void handle(LoginActivity target, Message message) {
            target.initPlatformList();
        }
    }

    @Bind(R.id.ll_other_platform)
    LinearLayout mOtherPlatormLl;
    @Bind(R.id.tv_declare)
    TextView mTvDeclare;
    @Bind(R.id.msg_view)
    public MSGView mMsgView;


    @OnClick(R.id.rl_content)
    public void exitLogin() {
        finish();
        overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
    }

    @OnClick(R.id.tv_declare)
    public void showDeclare() {
        View view = LayoutInflater.from(that).inflate(R.layout.mine_fragment_declare_item,null);
        if (!StringUtils.isNullOrEmpty(AppConfig.getAppDeclare())) {
            TextView  declare = (TextView) view.findViewById(R.id.tv_declare);
            declare.setText(AppConfig.getAppDeclare());
        }
        StyledDialog.buildCustom(view, Gravity.CENTER).setCancelable(true,true).show();
    }




    @Override
    protected void getIntentData() {
        mTargetActivity = getIntent().getStringExtra(GlobalConstant.IntentConstant.TARGETACTIVITY_NAME);
        mTargetBundle = getIntent().getExtras();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.login_other_platform_activity;
    }

    @Override
    protected void init() {
        mListener = new HttpListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                if (userInfo == null ||  StringUtils.isNullOrEmpty(userInfo.id)) {
                    onError(null);
                }
                MobclickAgent.onProfileSignIn(userInfo.platform, userInfo.id);
                dismissProgressDialog();
                try {
                    String info = JsonUtils.encode(userInfo);
                    AccountPreference.getInstance().setUserInfo(info);
                    mRxManager.post(GlobalConstant.RxBus.LOGIN_SUCCESS, userInfo);
                } catch (Exception e) {
                    LogUtils.e("com.resource.app", e.getMessage());
                }
                goTargetActivity();
            }

            @Override
            public void onError(Throwable e) {
                DialogUtil.showShortPromptToast(that,that.getString(R.string.login_fail));
                dismissProgressDialog();
            }
        };
        mApi = new UserLoginApi(mListener,this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    private void goTargetActivity() {
        if (!StringUtils.isNullOrEmpty(mTargetActivity)) {
            Intent intent = new Intent();
            if (mTargetBundle != null) {
                intent.putExtras(mTargetBundle);
            }
            intent.setClassName(this, mTargetActivity);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);

        }
        finish();
    }

    @Override
    protected void initContentView() {
        UIUtils.setUnderLine(mTvDeclare, that.getString(R.string.mine_app_declare));
        getPlatformList();
        mMsgView.setErrorClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPlatformList();
            }
        });
    }



    @Override
    public void onClick(View view) {
        showProgressDialog(R.string.app_loading_empy,false,true);
        Object tag = view.getTag();
        if (tag != null) {
            Platform platform = (Platform) tag;
            String name = platform.getName();
            //登陆逻辑的调用
            login(name);
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_AUTH_CANCEL:
                Toast.makeText(this, getString(R.string.login_cancle), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                break;
            case MSG_AUTH_ERROR:
                // 失败
                Throwable t = (Throwable) msg.obj;
                Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                t.printStackTrace();
                break;
            case MSG_AUTH_COMPLETE:
                Object[] objs = (Object[]) msg.obj;
                String plat = (String) objs[0];
                loginSuccess(plat);
                break;
            default:
                Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                break;
        }
        return false;
    }

    /* 获取平台列表*/
    private void getPlatformList() {
        mMsgView.showLoading();
        mMsgView.setVisibility(View.VISIBLE);
        mOtherPlatormLl.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlatformlist = ShareSDK.getPlatformList();
                mPlatformHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /* 显示平台按钮*/
    private void initPlatformList() {
        if (mOtherPlatormLl == null) {
            return;
        }
        if (mPlatformlist != null) {
            Platform qq  = ShareSDK.getPlatform(GlobalConstant.PlatoformType.QQ);
            addPlatformView(qq);
            Platform sinaWeibo  = ShareSDK.getPlatform(GlobalConstant.PlatoformType.SinaWeibo);
            addPlatformView(sinaWeibo);
            Platform wechat  = ShareSDK.getPlatform(GlobalConstant.PlatoformType.Wechat);
            addPlatformView(wechat);
            mMsgView.dismiss();
            mOtherPlatormLl.setVisibility(View.VISIBLE);
            mMsgView.setVisibility(View.GONE);
        } else {
            mMsgView.showError();
            mMsgView.setVisibility(View.VISIBLE);
            mOtherPlatormLl.setVisibility(View.GONE);
        }
    }

    private void addPlatformView(Platform platform) {
        if (mOtherPlatormLl == null) {
            return;
        }
        View platormView = LayoutInflater.from(that).inflate(R.layout.login_activity_platform_item,null,false);
        LinearLayout platormLayout = (LinearLayout) platormView.findViewById(R.id.ll_platform);
        TextView nameTv = (TextView) platormView.findViewById(R.id.tv_platform_name);
        ImageView iconIv = (ImageView) platormView.findViewById(R.id.iv_platform_icon);
        platormLayout.setTag(platform);
        String name = platform.getName();
        if (name.equals(GlobalConstant.PlatoformType.QQ)) {
            nameTv.setText(getString(R.string.login_other_platform_qq));
            iconIv.setImageDrawable(UIUtils.getDrawable(R.mipmap.qq));
            platormLayout.setOnClickListener(this);
            mOtherPlatormLl.addView(platormView);
        } else if (name.equals(GlobalConstant.PlatoformType.Wechat)) {
            nameTv.setText(getString(R.string.login_other_platform_weixin));
            iconIv.setImageDrawable(UIUtils.getDrawable(R.mipmap.weixin));
            platormLayout.setOnClickListener(this);
            mOtherPlatormLl.addView(platormView);

        }else if (name.equals(GlobalConstant.PlatoformType.SinaWeibo)) {
            nameTv.setText(getString(R.string.login_other_platform_sina));
            iconIv.setImageDrawable(UIUtils.getDrawable(R.mipmap.sina));
            platormLayout.setOnClickListener(this);
            mOtherPlatormLl.addView(platormView);
        }
    }

    /*
    * 演示执行第三方登录/注册的方法
    * <p>
    * 这不是一个完整的示例代码，需要根据您项目的业务需求，改写登录/注册回调函数
    *
    * @param platformName 执行登录/注册的平台名称，如：SinaWeibo.NAME
    */
    private void login(final String platformName) {
        Platform plat = ShareSDK.getPlatform(platformName);
        if (plat == null) {
            dismissProgressDialog();
            return;
        }

        if (plat.isAuthValid()) {
            plat.removeAccount(true);
        }

        //使用SSO授权，通过客户单授权
        plat.SSOSetting(false);
        plat.setPlatformActionListener(new PlatformActionListener() {
            public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.what = MSG_AUTH_COMPLETE;
                    msg.arg2 = action;
                    msg.obj =  new Object[] {plat.getName(), res};
                    handler.sendMessage(msg);
                } else {
                    DialogUtil.showLongPromptToast(that,getString(R.string.login_fail_unknow, platformName));
                    dismissProgressDialog();
                }
            }

            public void onError(Platform plat, int action, Throwable t) {
                LogUtils.e(TAG,action + ":" + t.getMessage());
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.what = MSG_AUTH_ERROR;
                    msg.arg2 = action;
                    msg.obj = t;
                    handler.sendMessage(msg);
                } else {
                    DialogUtil.showLongPromptToast(that,getString(R.string.login_fail_unknow, getPlatformName(platformName)));
                    dismissProgressDialog();
                }
                t.printStackTrace();
            }

            public void onCancel(Platform plat, int action) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.what = MSG_AUTH_CANCEL;
                    msg.arg2 = action;
                    msg.obj = plat;
                    handler.sendMessage(msg);
                } else {
                    dismissProgressDialog();
                }
            }
        });
        plat.showUser(null);
    }


    private void loginSuccess(String platformName) {
        Platform platform = ShareSDK.getPlatform(platformName);
        if(platform != null){
            UserInfo userInfo = new UserInfo();
            String gender = platform.getDb().getUserGender();
            if("m".equals(gender)){
                userInfo.gender = 0;
            } else {
                userInfo.gender = 1;
            }
            userInfo.nickname = platform.getDb().getUserName();
            userInfo.headIcon = platform.getDb().getUserIcon();
            userInfo.id = platform.getDb().getUserId();
            userInfo.platform = platformName;
            userInfo.loginTime = new Date().getTime();
            mApi.doHttp(userInfo);
        } else {
            dismissProgressDialog();
        }
    }

    private String getPlatformName(String name) {
        String platformName = "";
        if (name.equals(GlobalConstant.PlatoformType.QQ)) {
            platformName = getString(R.string.login_other_platform_qq);
        } else if (name.equals(GlobalConstant.PlatoformType.Wechat)) {
            platformName = getString(R.string.login_other_platform_weixin);

        }else if (name.equals(GlobalConstant.PlatoformType.SinaWeibo)) {
            platformName = getString(R.string.login_other_platform_sina);
        }
        return platformName;

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
    }
}
