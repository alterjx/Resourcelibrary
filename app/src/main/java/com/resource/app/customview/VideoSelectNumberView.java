package com.resource.app.customview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.resource.app.R;
import com.resource.app.model.VideoInfoModel;


public class VideoSelectNumberView extends BottomAnimationView implements View.OnClickListener {
    private View mLayoutView;
    private OnClickResource mOnClickResource;
    private RecyclerView mNumberRecyclerView;

    public void setOnClickResource(OnClickResource mOnClickResource) {
        this.mOnClickResource = mOnClickResource;
    }
    public VideoSelectNumberView(Context context) {
        this(context,null);
    }

    public VideoSelectNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContent();
    }

    private void initContent() {
        mLayoutView = LayoutInflater.from(mContext).inflate(R.layout.video_select_number_view,this);
        mNumberRecyclerView = (RecyclerView) mLayoutView.findViewById(R.id.rlv_index_list);
        mNumberRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 6));
        mLayoutView.findViewById(R.id.iv_close).setOnClickListener(this);
        setVisibility(View.GONE);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter == null) {
            return;
        }
        mNumberRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                if (null != mOnClickResource) {
                    mOnClickResource.onClick(view.getId());
                }
                break;
        }
    }

    public interface OnClickResource{
        public void onClick(int id);
    }
}
