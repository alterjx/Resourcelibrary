package com.resource.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.Wave;
import com.resource.app.R;


/**
 * 加载动画
 */
public class LoadingDialog extends Dialog implements Dialog.OnDismissListener,Dialog.OnShowListener {

    private SpinKitView mSpinKitView;
    private TextView mMessageView;
    private View mContent;

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        mContent = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.app_loading_dialog, null);
        setContentView(mContent);
        mSpinKitView = (SpinKitView) mContent.findViewById(R.id.spin_loading);
        if (null != mSpinKitView) {
            mSpinKitView.setColor(this.getContext().getResources().getColor(R.color.main_color));
            mSpinKitView.setIndeterminateDrawable(new FadingCircle());
        }
        mMessageView = (TextView) mContent.findViewById(R.id.tv_loading);
        setOnDismissListener(this);
        setOnShowListener(this);
    }

    public void hideContent() {
        mContent.setVisibility(View.GONE);
    }

    public void setMessageId(int resId) {
        mMessageView.setText(resId);
    }

    public void setMessage(String res){
        mMessageView.setText(res);
    }

    public void setSpinKitViewType(Sprite sprite) {
        if (null != mSpinKitView && null != sprite) {
            mSpinKitView.setIndeterminateDrawable(sprite);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mSpinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        if (mContent.getVisibility() == View.GONE) {
            return;
        }
        mSpinKitView.setVisibility(View.VISIBLE);
    }


    public interface IProgressDialog {
        void showProgressDialog(int var1);

        void dismissProgressDialog();
    }
}
