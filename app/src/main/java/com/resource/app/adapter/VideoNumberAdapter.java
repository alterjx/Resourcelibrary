package com.resource.app.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.resource.app.R;
import com.resource.app.utils.UIUtils;


public class VideoNumberAdapter extends BaseQuickAdapter<String> {
    private OnItemClickListener onItemClickListener;
    private int mSelectNumber = -1;

    public int getSelectNumber() {
        return mSelectNumber;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectNumber(int position) {
        mSelectNumber = position;
        notifyDataSetChanged();
    }

    public VideoNumberAdapter() {
        super(R.layout.video_activity_number_adapter_item,null);
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final String url) {
        final ViewHolder  holder = new ViewHolder(baseViewHolder);
        final int position = baseViewHolder.getAdapterPosition();
        holder.numberTv.setText((position + 1) + "");
        if (mSelectNumber == position) {
            holder.numberTv.setTextColor(UIUtils.getColor(R.color.main_color));
            holder.numberTv.setBackground(UIUtils.getResource().getDrawable(R.drawable.bg_main_color_circle));
        } else {
            holder.numberTv.setTextColor(UIUtils.getColor(R.color.color_80p_black));
            holder.numberTv.setBackground(UIUtils.getResource().getDrawable(R.drawable.bg_gray_circle));
        }
        holder.numberTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectNumber == position) {
                    return;
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(position, url);

                }
            }
        });

    }
    static class ViewHolder{
        public TextView numberTv;
        public ViewHolder(BaseViewHolder itemView) {
            numberTv = itemView.getView(R.id.tv_video_number);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, String url);
    }
}
