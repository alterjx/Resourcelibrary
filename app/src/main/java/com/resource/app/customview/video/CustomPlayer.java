package com.resource.app.customview.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.resource.app.R;
import com.resource.app.utils.UIUtils;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;




public class CustomPlayer extends ListGSYVideoPlayer {

    private TextView mTvScreenType;
    //记住切换数据源类型
    private int mType = 0;
    //数据源
    private int mSourcePosition = 0;

    private int mScreenType = 1; //0横 1竖屏
    private OnChangeScreenEvent mChangeEvent;
    private OnChangeNumberEvent mChangeNumberEvent;

    public void setOnChangeNumberEvent(OnChangeNumberEvent mChangeNumberEvent) {
        this.mChangeNumberEvent = mChangeNumberEvent;
    }

    public void setChangeEvent(OnChangeScreenEvent mChangeEvent) {
        this.mChangeEvent = mChangeEvent;
    }

    public TextView getmTvScreenType() {
        return mTvScreenType;
    }

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public CustomPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomPlayer(Context context) {
        super(context);
    }

    public CustomPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    private void initView() {
        mTvScreenType = (TextView) findViewById(R.id.tv_screen_type);
        mTvScreenType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHadPlay) {
                    return;
                }
                if (mScreenType == 0) {
                    mScreenType = 1;
                    mTvScreenType.setText(UIUtils.getString(R.string.film_detail_video_screen_ver));
                } else {
                    mScreenType = 0;
                    mTvScreenType.setText(UIUtils.getString(R.string.film_detail_video_screen_hor));
                }
                if (null != mChangeEvent) {
                    mChangeEvent.onClick(mScreenType);
                }

            }
        });
    }

    /**
     * 需要在尺寸发生变化的时候重新处理
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureSizeChanged(surface, width, height);
    }


    @Override
    public int getLayoutId() {
        return R.layout.custom_player_video_layout;
    }


    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        CustomPlayer customPlayer = (CustomPlayer) super.startWindowFullscreen(context, actionBar, statusBar);
        customPlayer.mSourcePosition = mSourcePosition;
        customPlayer.mType = mType;
        customPlayer.setChangeEvent(mChangeEvent);
        customPlayer.setOnChangeNumberEvent(mChangeNumberEvent);
        //sampleVideo.resolveTransform();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return customPlayer;
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            CustomPlayer sampleVideo = (CustomPlayer) gsyVideoPlayer;
            mSourcePosition = sampleVideo.mSourcePosition;
            mType = sampleVideo.mType;
        }
    }

    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        resolveRotateUI();
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        if (mChangeNumberEvent != null) {
            mChangeNumberEvent.onChange(this.mPlayPosition);
        }

    }

    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    public interface OnChangeScreenEvent{
        public void onClick(int type);
    }

    public interface OnChangeNumberEvent{
        public void onChange(int index);
    }
}
