package com.resource.app.adapter;

import android.graphics.drawable.Animatable;
import android.net.Uri;

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
import com.resource.app.utils.UIUtils;

public class VideoDetailAdapter extends BaseQuickAdapter<String> {

    public VideoDetailAdapter() {
        super(R.layout.video_activity_adapter_item,null);
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final String url) {
        final ViewHolder  holder = new ViewHolder(baseViewHolder);
        ImageRequest request =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url.trim()))
                        .setResizeOptions(new ResizeOptions(UIUtils.getScreenW()/5,UIUtils.getScreenH()/6))
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

    }
    static class ViewHolder{
        public SimpleDraweeView pic;
        public ViewHolder(BaseViewHolder itemView) {
            pic = itemView.getView(R.id.iv_pic);
        }
    }
}
