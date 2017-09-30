package com.resource.app.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.resource.app.R;
import com.resource.mark_net.utils.StringUtils;

public class DeclareView extends FrameLayout {

	private Context mContext;
	private View mRootView;
	private LinearLayout mBannerLayout;
	private TextView mTitle;
	private TextView mContent;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public DeclareView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DeclareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DeclareView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mRootView = LayoutInflater.from(this.getContext()).inflate(R.layout.mine_fragment_declare_item, this);
		mBannerLayout = (LinearLayout) mRootView.findViewById(R.id.ll_banner);
		mBannerLayout.setVisibility(GONE);
		mTitle = (TextView) mRootView.findViewById(R.id.tv_declare_title);
		mContent = (TextView) mRootView.findViewById(R.id.tv_declare);
	}

	public void setData(String title, String content) {
		if (!StringUtils.isNullOrEmpty(title)) {
			mTitle.setText(title);
		}
		if (!StringUtils.isNullOrEmpty(content)) {
			mContent.setText(content);
		}
	}

	public void setContent(String content) {
		setData("",content);
	}

	public void showAdvView() {
	}

}
