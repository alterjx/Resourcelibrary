package com.resource.app.adapter;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.resource.app.R;
import com.resource.app.model.VideoInfoModel;
import com.resource.app.model.UndefinedModel;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.utils.StringUtils;

public class VideoFragmentAdapter extends BaseQuickAdapter<VideoInfoModel> {

    OnItemClickListener onItemClickListener;
    public VideoFragmentAdapter() {
        super(R.layout.film_fragment_adapter_item,null);
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final VideoInfoModel info) {
        final ViewHolder  holder = new ViewHolder(baseViewHolder);
        final UndefinedModel mUndefined = AppConfig.getManager();
        ImageRequest request =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(info.firstUrl.trim()))
                        .setResizeOptions(new ResizeOptions(UIUtils.getScreenW()/2,UIUtils.getScreenH()/2))
                        //缩放,在解码前修改内存中的图片大小, 配合Downsampling可以处理所有图片,否则只能处理jpg,
                        // 开启Downsampling:在初始化时设置.setDownsampleEnabled(true)
                        .setProgressiveRenderingEnabled(true)//支持图片渐进式加载
                        .setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                        .build();
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                float width = imageInfo.getWidth();
                float height = imageInfo.getHeight();
                holder.pic.setAspectRatio(width / height);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                holder.pic.setClickable(false);
            }
        };

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setOldController(holder.pic.getController())
                .setImageRequest(request)
               // .setUri(Uri.parse("http://pic.zhifuok.com/qq32593992"+ pic.firstUrl))
                .build();
        holder.pic.setController(controller);
        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClickListener(baseViewHolder.getAdapterPosition(), info,holder.pic);
                }
            }
        });

        StringBuffer title = new StringBuffer();
        title.append(UIUtils.getString(R.string.video_time, String.valueOf(info.videoTime))).append(info.name);
        holder.title.setText(title.toString());

        if (mUndefined == null || !mUndefined.isCanOperate  || StringUtils.isNullOrEmpty(mUndefined.dUrl)) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != onItemClickListener) {
                        onItemClickListener.onDeleteClickListener(baseViewHolder.getAdapterPosition(), info, mUndefined.dUrl);
                    }
                }
            });
        }
    }
    static class ViewHolder{

        public SimpleDraweeView pic;
        public TextView title;
        public ImageView delete;

        public ViewHolder(BaseViewHolder itemView) {
            pic = itemView.getView(R.id.iv_pic);
            title = itemView.getView(R.id.tv_title);
            delete = itemView.getView(R.id.iv_delete);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, VideoInfoModel info, View view);
        void onDeleteClickListener(int position, VideoInfoModel info, String url);
    }

}
