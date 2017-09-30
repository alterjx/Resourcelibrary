package com.resource.app.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.resource.app.BaseActivity;
import com.resource.app.BaseApplication;
import com.resource.app.R;
import com.resource.app.api.GetOrderInfoApi;
import com.resource.app.api.GetUserInfoApi;
import com.resource.app.api.PayConfigInfoApi;
import com.resource.app.api.model.PayConfigInfoOutput;
import com.resource.app.api.model.PayOrderInfoInput;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.DeclareView;
import com.resource.app.customview.MSGView;
import com.resource.app.customview.PayTypeView;
import com.resource.app.manager.rx.RxBus;
import com.resource.app.model.PayProductMode;
import com.resource.app.model.PayResult;
import com.resource.app.model.UserInfo;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.OrderInfoUtil;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.DialogUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import butterknife.Bind;
import butterknife.OnClick;


public class PayActivity extends BaseActivity {

    private static final int ALI_PAY_FLAG = 1; //支付宝
    private static final int WEICHAT_AUTH_FLAG = 2; //微信
    private boolean mIsNeedPayTypeView;
    private PayOrderInfoInput mInfoInput = new PayOrderInfoInput();
    private GetOrderInfoApi mApi;
    private String mPayDeclare;
    private String mPayQuestion;

    @Bind(R.id.tv_title)
    TextView mTitleTv;
    @Bind(R.id.rl_head)
    RelativeLayout mHeadRl;
    @Bind(R.id.ll_pay_product)
    LinearLayout mProductLl;
    @Bind(R.id.msg_view)
    public MSGView mMsgView;
    @Bind(R.id.animation_background)
    public FrameLayout mAnimalBg;
    @Bind(R.id.view_pay)
    public PayTypeView mPayTypeView;
    @Bind(R.id.view_space)
    public View mSpaceView;
    @Bind(R.id.tv_pay_declare)
    TextView mPayDeclareTv;
    @Bind(R.id.ll_declare)
    LinearLayout mPayDeclareLl;
    @Bind(R.id.tv_pay_question)
    TextView mPayQuestionTv;
    @Bind(R.id.ll_banner)
    LinearLayout mBannerLl;

    @OnClick(R.id.iv_back)
    public void back() {
        exit();
    }

    @OnClick(R.id.tv_pay_declare)
    public void showDeclare() {
        DeclareView declareView = new DeclareView(this);
        declareView.showAdvView();
        declareView.setData(getString(R.string.pay_declare),mPayDeclare);
        StyledDialog.buildCustom(declareView, Gravity.CENTER).setCancelable(true, true).show();
    }

    @OnClick(R.id.tv_pay_question)
    public void showQuestion() {
        DeclareView questView = new DeclareView(this);
        questView.showAdvView();
        questView.setData(getString(R.string.pay_question),mPayQuestion);
        StyledDialog.buildCustom(questView, Gravity.CENTER).setCancelable(true, true).show();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALI_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, GlobalConstant.PayResult.ALI_PAY_SUCCESS)) {
                        showProgressDialog(R.string.pay_user_upgrade, false);
                        mAnimalBg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUserInfo();
                            }
                        },3000);

                    } else if (TextUtils.equals(resultStatus, GlobalConstant.PayResult.ALI_PAY_CANCLE)) {
                        DialogUtil.showShortPromptToast(that, that.getString(R.string.pay_cancle));
                    } else if (TextUtils.equals(resultStatus, GlobalConstant.PayResult.ALI_PAY_NET_ERROR)) {
                        DialogUtil.showShortPromptToast(that, that.getString(R.string.app_download_fail));
                    } else if (TextUtils.equals(resultStatus, GlobalConstant.PayResult.ALI_PAY_NO_ALI_APP)) {
                        DialogUtil.showShortPromptToast(that, that.getString(R.string.pay_no_ali_app));
                    } else {
                        if (StringUtils.isNullOrEmpty(resultStatus)) {
                            DialogUtil.showShortPromptToast(that, that.getString(R.string.pay_error));
                        } else {
                            DialogUtil.showShortPromptToast(that, that.getString(R.string.pay_error2, resultStatus));
                        }

                        AccountPreference.getInstance().setPayInfo(payResult.toString());
                        MobclickAgent.reportError(BaseApplication.getInstance(), "ALI_PAY_FLAG error：" + payResult.toString());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.pay_activity;
    }

    @Override
    protected void init() {
        mApi = new GetOrderInfoApi(new HttpListener<TreeMap<String, String>>() {
            @Override
            public void onPre() {
                super.onPre();
                showProgressDialog(R.string.pay_get_order_info, false);
            }

            @Override
            public void onSuccess(TreeMap<String, String> info) {
                dismissProgressDialog();
                aliPay(info);
            }

            @Override
            public void onError(Throwable e) {
                dismissProgressDialog();
                DialogUtil.showShortPromptToast(that, that.getString(R.string.pay_error));
                MobclickAgent.reportError(BaseApplication.getInstance(), "GetOrderInfoApi error:" + e.getMessage());
            }
        }, this);


    }


    @Override
    protected void initHeadView() {
        super.initHeadView();
        mHeadRl.setVisibility(View.VISIBLE);
        mSpaceView.setVisibility(View.GONE);
        mTitleTv.setText(getString(R.string.pay_head_title));
    }

    @Override
    protected void initContentView() {
        mMsgView.setErrorClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayConfig();
            }
        });
        getPayConfig();
        mPayTypeView.setOnSelectPay(new PayTypeView.OnSelectPay() {
            @Override
            public void onSelect(int payType) {
                mPayTypeView.startBottomAnimation(mAnimalBg);
                if (payType < 0) {
                    return;
                }
                mInfoInput.payId = payType;
                getOrderInfo();
            }
        });

        UIUtils.setUnderLine(mPayDeclareTv, that.getString(R.string.pay_declare));
        UIUtils.setUnderLine(mPayQuestionTv, that.getString(R.string.pay_question));
    }

    private void initProduct(List<PayProductMode> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        for (final PayProductMode mode : products) {
            if (mode == null || mode.price <= 0) {
                continue;
            }
            View view = LayoutInflater.from(that).inflate(R.layout.pay_product_item_view, null, false);
            view.findViewById(R.id.tv_pay_click).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInfoInput.productId = mode.id;
                    if (mIsNeedPayTypeView) {
                        mPayTypeView.setPayTypeTitlData(mode.subject, mode.price);
                        mPayTypeView.startBottomAnimation(mAnimalBg);
                    } else {
                        getOrderInfo();
                    }
                }
            });
            TextView product = (TextView) view.findViewById(R.id.tv_product_name);
            product.setText(mode.subject + getString(R.string.app_bracket, mode.body));
            TextView price = (TextView) view.findViewById(R.id.tv_product_price);
            price.setText(getPrice(String.valueOf(mode.price)));
            if (mode.price != mode.originPrice) {
                TextView originPrice = (TextView) view.findViewById(R.id.tv_product_origin_price);
                originPrice.setText(getString(R.string.pay_product_origin_price, String.valueOf(mode.originPrice)));
            }
            mProductLl.addView(view);
        }

    }

    private void initPayType(LinkedHashMap<Integer, String> payTypeMap) {

        if (payTypeMap.size() <= 1) {
            mIsNeedPayTypeView = false;
            Iterator<Integer> treeKey = payTypeMap.keySet().iterator();
            while (treeKey.hasNext()) {
                mInfoInput.payId = treeKey.next();
            }
        } else {
            mIsNeedPayTypeView = true;
            mPayTypeView.setPayTypeData(payTypeMap);
        }
    }

    private void getOrderInfo() {
        mInfoInput.userId = AppConfig.getUserInfo().id;
        mApi.doHttp(mInfoInput);
    }


    private void aliPay(final Map<String, String> map) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
                String orderInfo = OrderInfoUtil.buildOrderParam(map);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = ALI_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void showUpgradeError() {
        StyledDialog.buildIosAlert(that.getString(R.string.pay_user_upgrade_error_title), that.getString(R.string.pay_user_upgrade_error), new MyDialogListener() {
            @Override
            public void onFirst() {
                ClipboardManager cm = (ClipboardManager) that.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(that.getString(R.string.app_contact_service_weixin));
                DialogUtil.showShortPromptToast(that,that.getString(R.string.app_contact_tip));
            }

            @Override
            public void onSecond() {
            }

            @Override
            public void onThird() {
                showProgressDialog(R.string.pay_user_upgrade, false);
                getUserInfo();
            }


        }).setTitleColor(R.color.light_gray)
                .setCancelable(false ,false)
                .setBtnColor(R.color.gray_c5c5c5, 0, R.color.main_color)
                .setBtnText(that.getString(R.string.pay_contact_service), "", that.getString(R.string.pay_contact_update))
                .show();
    }

    private void getUserInfo() {
        new GetUserInfoApi(new HttpListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                dismissProgressDialog();
                if (userInfo == null) {
                    return;
                }
                if (userInfo.vipType > 0) {
                    AppConfig.updateUserInfoExceptLoginTime(userInfo);
                    RxBus.getInstance().post(GlobalConstant.RxBus.UPGRADE_RESULT, userInfo);
                    DialogUtil.showShortPromptToast(that, that.getString(R.string.pay_user_upgrade_success));
                    exit();
                } else {
                    onError(null);
                }

            }
            @Override
            public void onError(Throwable e) {
                showUpgradeError();
                dismissProgressDialog();
                if (e == null) {
                    MobclickAgent.reportError(BaseApplication.getInstance(), "getUserInfo error:未知");
                } else {
                    MobclickAgent.reportError(BaseApplication.getInstance(), "getUserInfo error:" + e.getMessage());
                }

            }
        }, this).doHttp(AppConfig.getUserInfo());
    }

    private void getPayConfig() {
        mMsgView.showLoading();
        new PayConfigInfoApi(new HttpListener<PayConfigInfoOutput>() {
            @Override
            public void onSuccess(PayConfigInfoOutput infoInput) {
                mMsgView.dismiss();
                if (infoInput == null
                        || infoInput.payTypeMap == null
                        || infoInput.payTypeMap.isEmpty()
                        || infoInput.productInfos == null
                        || infoInput.productInfos.isEmpty()) {
                    mMsgView.showError();
                    return;
                }
                initProduct(infoInput.productInfos);
                initPayType(infoInput.payTypeMap);
                initPayDeclare(infoInput.payDeclare);
                initPayQuestion(infoInput.payQuestion);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mMsgView.showError();
                MobclickAgent.reportError(BaseApplication.getInstance(), "getPayConfig error:" + e.getMessage());

            }
        }, this).doHttp(null);
    }

    private void initPayDeclare(String payDeclare) {
        if (StringUtils.isNullOrEmpty(payDeclare)) {
            mPayDeclareLl.setVisibility(View.GONE);
        } else {
            mPayDeclare = payDeclare;
            mPayDeclareLl.setVisibility(View.VISIBLE);
        }
    }

    private void initPayQuestion(String payQuestion) {
        if (StringUtils.isNullOrEmpty(payQuestion)) {
            mPayQuestionTv.setVisibility(View.GONE);
        } else {
            mPayQuestion = payQuestion;
            mPayQuestionTv.setVisibility(View.VISIBLE);
        }
    }

    private SpannableString getPrice(String price) {
        String priceFormat = that.getString(R.string.pay_product_price, price);
        SpannableString spannableString = new SpannableString(priceFormat);
        spannableString.setSpan(new AbsoluteSizeSpan(15, true), price.length(), priceFormat.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, price.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    public void onBackPressed() {
        if (mPayTypeView.getVisibility() == View.VISIBLE) {
            mPayTypeView.startBottomAnimation(mAnimalBg);
            return;
        }
        exit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
