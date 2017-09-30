package com.resource.app.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.resource.app.R;

public class SearchHistoryAdapter extends BaseQuickAdapter<String> {

    OnItemClickListener onItemClickListener;

    public SearchHistoryAdapter() {
        super(R.layout.search_history_fragment_adapter_item,null);
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final String content) {
        final ViewHolder  holder = new ViewHolder(baseViewHolder);
        holder.content.setText(content);
        holder.history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClickListener(baseViewHolder.getAdapterPosition(),content);
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemClickListener) {
                    onItemClickListener.onDeleteListener(baseViewHolder.getAdapterPosition());
                }
            }
        });
    }
    static class ViewHolder{

        public ImageView delete;
        public RelativeLayout history;
        public TextView content;


        public ViewHolder(BaseViewHolder itemView) {
            delete = itemView.getView(R.id.iv_delete);
            history = itemView.getView(R.id.rl_history_content);
            content = itemView.getView(R.id.tv_history);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, String content);
        void onDeleteListener(int position);
    }



}
