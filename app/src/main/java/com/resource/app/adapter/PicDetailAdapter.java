package com.resource.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.resource.app.R;
import com.resource.app.customview.frescozoomable.DoubleTapGestureListener;
import com.resource.app.customview.frescozoomable.ZoomableDraweeView;
import com.resource.app.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class PicDetailAdapter extends PagerAdapter {

    private List<String> mImageUrls;
    private ImageOnClick mImageClickListener;
    ZoomableDraweeView[] mImageViewList;
    private String mUrlDomain;

    public void setImageClickListener(ImageOnClick listener) {
        mImageClickListener = listener;
    }

    public PicDetailAdapter(String urlDomain) {
        mImageUrls = new ArrayList<>();
        mUrlDomain = urlDomain;
    }

    public void addPics(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        mImageUrls.clear();
        mImageUrls.addAll(urls);
        mImageViewList = new ZoomableDraweeView[urls.size()];
        notifyDataSetChanged();
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public int getCount() {
        return mImageUrls != null ? mImageUrls.size() : 0;
    }

    public String getItem(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        return mImageUrls.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ZoomableDraweeView zoomableDraweeView = null;
        if (mImageViewList != null && mImageViewList.length > position && mImageViewList[position] != null) {
            zoomableDraweeView = mImageViewList[position];
        } else {
            zoomableDraweeView = new ZoomableDraweeView(container.getContext());
            zoomableDraweeView.setAllowTouchInterceptionWhileZoomed(true);
            zoomableDraweeView.setIsLongpressEnabled(false);
            zoomableDraweeView.setTapListener(new DoubleTapGestureListener(zoomableDraweeView) {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (mImageClickListener != null) {
                        mImageClickListener.singleOnclick(position);
                    }
                    return super.onSingleTapConfirmed(e);
                }
            });
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setTapToRetryEnabled(true)
                    .setUri(mUrlDomain + getItem(position).trim()).build();
            zoomableDraweeView.setController(controller);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(container.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setFailureImage(R.mipmap.icon_remarkloading_fail, ScalingUtils.ScaleType.CENTER)
                    //.setRetryImage(R.mipmap.pic_load_retry, ScalingUtils.ScaleType.CENTER_INSIDE)
                    .setProgressBarImage(new AutoRotateDrawable(UIUtils.getDrawable(R.mipmap.pic_loading_progress), 3000))
                    .build();
            zoomableDraweeView.setHierarchy(hierarchy);

            mImageViewList[position] = zoomableDraweeView;
        }
        container.addView(zoomableDraweeView);

        return zoomableDraweeView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mImageViewList != null && mImageViewList.length > position && mImageViewList[position] != null) {
            ZoomableDraweeView imageView = mImageViewList[position];
            container.removeView(imageView);
        }
    }

    public interface ImageOnClick {
        void singleOnclick(int position);
    }

}
