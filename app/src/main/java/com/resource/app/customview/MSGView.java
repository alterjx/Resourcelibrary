package com.resource.app.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.resource.app.R;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.utils.StringUtils;

public class MSGView extends FrameLayout {

	private View mRootView;
	private LinearLayout mLoadingLayout;
	private LinearLayout mErrorLayout;
	private LinearLayout mEmptyLayout;
	private SpinKitView mSpinKitView;
	private TextView mEmptyTipTv;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public MSGView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MSGView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MSGView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mRootView = LayoutInflater.from(this.getContext()).inflate(R.layout.app_view_msg_layout, this);
		mEmptyTipTv = (TextView) mRootView.findViewById(R.id.tv_empty_tip);
		mLoadingLayout = (LinearLayout) mRootView.findViewById(R.id.ll_loading);
		mEmptyLayout = (LinearLayout) mRootView.findViewById(R.id.ll_empty);
		mErrorLayout = (LinearLayout) mRootView.findViewById(R.id.ll_error);
		mSpinKitView = (SpinKitView) mRootView.findViewById(R.id.spin_loading);
		if (null != mSpinKitView) {
			mSpinKitView.setColor(this.getContext().getResources().getColor(R.color.main_color));
			mSpinKitView.setIndeterminateDrawable(new Wave());
		}
	}

	public void setSpinKitViewType(Sprite sprite) {
		if (null != mSpinKitView && null != sprite) {
			mSpinKitView.setIndeterminateDrawable(sprite);
		}
	}
	
	public void dismiss(){
		this.setVisibility(View.INVISIBLE);
	}
	public void showLoading() {
		this.setVisibility(View.VISIBLE);
		mLoadingLayout.setVisibility(View.VISIBLE);
		mEmptyLayout.setVisibility(GONE);
		mErrorLayout.setVisibility(View.GONE);
	}
	public void showError() {
		this.setVisibility(View.VISIBLE);
		mLoadingLayout.setVisibility(View.GONE);
		mEmptyLayout.setVisibility(GONE);
		mErrorLayout.setVisibility(View.VISIBLE);
	}

	public void showEmpty() {
		this.setVisibility(View.VISIBLE);
		mLoadingLayout.setVisibility(View.GONE);
		mErrorLayout.setVisibility(View.GONE);
		mEmptyLayout.setVisibility(VISIBLE);
	}

	public void showEmpty(String tip) {
		if (!StringUtils.isNullOrEmpty(tip)) {
			mEmptyTipTv.setText(tip);
		}
		showEmpty();
	}

	public void showSearchEmpty() {
		showEmpty(UIUtils.getString(R.string.search_empty));

	}
	
	public void setErrorClick(OnClickListener listener){
		if(listener!=null){
			mErrorLayout.setOnClickListener(listener);
		}
	}
}
