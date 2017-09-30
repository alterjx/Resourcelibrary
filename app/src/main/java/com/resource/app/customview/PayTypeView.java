package com.resource.app.customview;

import java.util.LinkedHashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.resource.app.R;
import com.resource.app.constant.GlobalConstant;


public class PayTypeView extends BottomAnimationView implements View.OnClickListener {
    private static final int CANCLE = -1;
    private View mLayoutView;
    private TextView mTitleTv;
    private TextView mAmountTv;
    private TextView mAliPayTv;
    private TextView mWeiXinPayTv;
    private OnSelectPay mSelectPay;
    private LinearLayout mBannerLl;

    public void setOnSelectPay(OnSelectPay selectPay) {
        mSelectPay = selectPay;
    }
    public PayTypeView(Context context) {
        this(context,null);
    }

    public PayTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContent();
    }

    private void initContent() {
        mLayoutView = LayoutInflater.from(mContext).inflate(R.layout.pay_select_view,this);
        mLayoutView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSelectPay) {
                    mSelectPay.onSelect(CANCLE);
                }
            }
        });
        mTitleTv = (TextView) mLayoutView.findViewById(R.id.tv_pay_select_title);
        mAmountTv = (TextView) mLayoutView.findViewById(R.id.tv_pay_select_amount);
        mAliPayTv = (TextView) mLayoutView.findViewById(R.id.tv_pay_ali);
        mAliPayTv.setOnClickListener(this);
        mWeiXinPayTv = (TextView) mLayoutView.findViewById(R.id.tv_pay_weixin);
        mWeiXinPayTv.setOnClickListener(this);
        setVisibility(View.GONE);
        mBannerLl = (LinearLayout) mLayoutView.findViewById(R.id.ll_banner);
    }

    public void setPayTypeData(LinkedHashMap<Integer, String> payTypeMap) {
        mAliPayTv.setText(payTypeMap.get(GlobalConstant.PayType.ALI_PAY));
        mWeiXinPayTv.setText(payTypeMap.get(GlobalConstant.PayType.WEIXIN_PAY));
    }

    public void addAdvView(View view) {
        mBannerLl.addView(view);
        mBannerLl.setVisibility(GONE);
    }

    public void setPayTypeTitlData(String name, int amount) {
        mTitleTv.setText(mContext.getString(R.string.pay_select_title,name));
        mAmountTv.setText(mContext.getString(R.string.pay_select_amount,String.valueOf(amount)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pay_ali:
                if (mSelectPay != null) {
                    mSelectPay.onSelect(GlobalConstant.PayType.ALI_PAY);
                }
                break;
            case R.id.tv_pay_weixin:
                if (mSelectPay != null) {
                    mSelectPay.onSelect(GlobalConstant.PayType.WEIXIN_PAY);
                }
                break;
        }
    }

    public interface OnSelectPay{
        public void onSelect(int payType);
    }
}
