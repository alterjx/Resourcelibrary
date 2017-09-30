package com.resource.app.customview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.resource.app.BaseActivity;
import com.resource.app.R;
import com.resource.app.manager.AppManager;
import com.resource.app.utils.ActivityUtils;
import com.resource.app.utils.UIUtils;

/**
 * Created by newbiefly on 2016/6/25.
 * 用于实现Activit滑动关闭的view
 */
public class GestureView extends LinearLayout {
    private Context ctx;
    float down_x = 0, down_y = 0, up_x = 0;
    private Scroller mScroller;
    private GestureViewChanged GestureViewChanged = null;
    private GestureViewScrollStateChanged GestureViewScrollStateChanged = null;

    private int screenWidth = 0, asGestureViewShowWidth = 0;
    private boolean isTouch = false;
    private boolean mScrolling = false;

    /**
     * 可以认为是滚动的最小距离
     */
    private int mTouchSlop;
    /**
     * 最后点击的点
     */
    private float mLastMotionX, mLastMotionY;
    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;

    /**
     * 全屏幕滑动所需时间
     */
    private final int FULL_SCREEN_SCROLL_TIME = 600;

    /**
     * 手势View是否有效
     */
    private boolean isGertureViewEnable = true;

    /**
     * 阴影view
     */
    private ImageView imageViewShadow;
    private int imageWidth = 0;

    private boolean isHashSlideShadowTopPadding = false;

    private final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            return -(t) * (t - 2);
        }
    };

    public GestureView(Context context, boolean isHashSlideShadowTopPadding) {
        super(context);
        this.ctx = context;
        this.isHashSlideShadowTopPadding = isHashSlideShadowTopPadding;
        Init();
    }


    private void Init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        final ViewConfiguration configuration = ViewConfiguration.get(ctx);
        // 获得可以认为是滚动的距离
        mTouchSlop = 2 * configuration.getScaledTouchSlop();
        mScroller = new Scroller(getContext(), sInterpolator);
    }

    /**
     * 设置监听事件
     */
    public void setGestureViewChanged(GestureViewChanged changed) {
        this.GestureViewChanged = changed;
    }

    /**
     * 设置监听事件
     */
    public void setGestureViewScrollStateChanged(GestureViewScrollStateChanged changed) {
        this.GestureViewScrollStateChanged = changed;
    }

    /**
     * 向group中添加一个View
     */
    public void addOneView(View view) {
        /** 添加imageView */
        imageViewShadow = new ImageView(ctx);
        imageWidth = UIUtils.dip2px(10);
        imageViewShadow.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, LayoutParams.MATCH_PARENT));
        imageViewShadow.getMeasuredWidth();
        imageViewShadow.setBackgroundResource(R.mipmap.activity_slidebar_shadow);
        this.addView(imageViewShadow);
        this.addView(view);
        scrollTo(imageWidth, 0);
    }


    public int getStatusBarHeight() {
        try{
            int result = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }catch (Exception e){
            return 0;
        }
    }


    /**
     * 设置可以认为是展开侧滑栏的屏幕宽度比例 即用户在屏幕左侧screenwith/scale 区域向右滑动时认为是展开侧滑
     */
    public void setAsGestureViewScale(int scale) {
        asGestureViewShowWidth = screenWidth / scale;
    }

    /**
     * 设置手势View是否可用，用于在activity中在某些特殊时候禁用手势View
     */
    public void setGestureViewEnable(boolean isEnable) {
        this.isGertureViewEnable = isEnable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 仅当ViewGroup为fill_parent才处于EXACTLY模式
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be EXACTLY mode.");
        }
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Workspace can only be EXACTLY mode.");
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            int mywidth = widthMeasureSpec;
            if (getChildAt(i) == imageViewShadow) {
                mywidth = MeasureSpec.makeMeasureSpec(imageWidth, widthMode);
            }
            if (i < getChildCount()) {
                getChildAt(i).measure(mywidth, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childLeft = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int childWidth = child.getMeasuredWidth();
                if(child==imageViewShadow&&isHashSlideShadowTopPadding){
                    child.layout(childLeft, 0+getStatusBarHeight(), childLeft + childWidth, child.getMeasuredHeight());
                }else{
                    child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                }
                childLeft += childWidth;
            }
        }
    }

    private boolean isIntercept = true;

    public void setIsIntercept(boolean Intercept) {
        this.isIntercept = Intercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isGertureViewEnable) {
            return false;
        }
        if (isIntercept) {
            int action = ev.getAction();
            float x = ev.getX();
            float y = ev.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                    break;
                case MotionEvent.ACTION_MOVE:// 向右滑动切横向距离大于纵向滑动距离
                    int xDiff = (int) (x - mLastMotionX);
                    int yDiff = (int) Math.abs(y - mLastMotionY);
                    boolean xMoved = (mLastMotionX < asGestureViewShowWidth) && (xDiff > mTouchSlop) && (yDiff < mTouchSlop);
                    // 判断是否是移动
                    if (xMoved) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                        down_x = x;
                        isTouch = true;
                        setScrollingCacheEnabled(true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mTouchState = TOUCH_STATE_REST;
                    break;
            }
            boolean result = mTouchState != TOUCH_STATE_REST;
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isGertureViewEnable) {
            return false;
        }
        if (mScrolling) {
            return false;
        }
        boolean result = true;
        int action = event.getAction();
        float now_x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                down_x = now_x;
                down_y = event.getY();
                setScrollingCacheEnabled(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTouch) {
                    int xDiff = (int) (now_x - down_x);
                    int yDiff = (int) Math.abs(event.getY() - down_y);
                    boolean xMoved = (down_x < asGestureViewShowWidth) && (xDiff > mTouchSlop) && (yDiff < mTouchSlop);
                    if (xMoved) {
                        isTouch = true;
                        down_x = now_x;
                    }
                }
                if (isTouch) {
                    int tox = -(int) (now_x - down_x + 0);
                    if (tox > imageWidth) {
                        tox = imageWidth;
                    }
                    // 回调开始滑动
                    if (GestureViewScrollStateChanged != null) {
                        GestureViewScrollStateChanged.onScrollStateChanged(true);
                    }
                    scrollTo(tox, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTouch) {
                    isTouch = false;
                    if (now_x - down_x > screenWidth / 4) {
                        smoothScrollTo(-screenWidth, 0, 0);
                    } else {
                        smoothScrollTo(imageWidth, 0, 0);
                    }
                }
                // 回调结束滑动
                if (GestureViewScrollStateChanged != null) {
                    GestureViewScrollStateChanged.onScrollStateChanged(false);
                }
                break;
        }
        return result;
    }

    /**
     * 关闭当前view
     */
    public void close() {
        smoothScrollTo(-screenWidth, 0, 0);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if(isTouch||mScrolling){
            scrollPreviousActivity(x);
        }
    }

    /**
     * 滚动前面一个Activity 形成视差动画效果
     * by chenlong
     * @param x
     */
    private void scrollPreviousActivity(int x){
        Activity activityCurrent= AppManager.getAppManager().currentActivity();
        if(activityCurrent==null){
            return;
        }
        if(activityCurrent==ctx){
            Activity activityPrevious=AppManager.getAppManager().currentPreviousActivity();
            if(activityPrevious instanceof BaseActivity){
                BaseActivity EFA= (BaseActivity)activityPrevious;
                if(EFA.getmGestureView()!=null){
                    EFA.getmGestureView().scrollTo(imageWidth+ctx.getResources().getDisplayMetrics().widthPixels/4+x/4,0);
                }
            }
        }
    }

    /**
     * 还原前面一个Activity
     * by chenlong
     */
    public void restorePreviousActivity(){
        Activity activityCurrent= ActivityUtils.getInstance().currentActivity();
        if(activityCurrent==null){
            return;
        }
        if(activityCurrent==ctx){
            Activity activityPrevious=ActivityUtils.getInstance().currentPreviousActivity();
            if(activityPrevious instanceof BaseActivity){
                BaseActivity EFA= (BaseActivity)activityPrevious;
                if(EFA.getmGestureView()!=null){
                    EFA.getmGestureView().scrollTo(imageWidth,0);
                }
            }
        }
    }


    //////////////////////////////////////
    @Override
    public void computeScroll() {
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                int oldX = getScrollX();
                int oldY = getScrollY();
                int x = mScroller.getCurrX();
                int y = mScroller.getCurrY();
                if (oldX != x || oldY != y) {
                    scrollTo(x, y);
                }
                if (x > -screenWidth && x < imageWidth) {//未滑出视野
                    // Keep on drawing until the animation has finished.
                    invalidate();
                    return;
                }
                //view已经滑出视野，则终止
            }
        }
        // Done with scroll, clean up state.
        completeScroll();
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(enabled);
            }
        }
    }

    /**
     * 滑动结束
     */
    private void completeScroll() {
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            if (mScroller.getCurrX() < -screenWidth + 10) {// View隐藏完成
                Message msg = new Message();
                msg.what = 100;
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }
        mScrolling = false;
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    void smoothScrollTo(int x, int y, int velocity) {
        if (getChildCount() == 0) {
            // Nothing to do.
            setScrollingCacheEnabled(false);
            return;
        }
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            completeScroll();
            if (mScroller.getCurrX() < -screenWidth + 10) {// View隐藏完成
                Message msg = new Message();
                msg.what = 100;
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
            return;
        }
        setScrollingCacheEnabled(true);
        mScrolling = true;
        //计算行程所需时间
        float d = Math.abs(dx);
        float w = screenWidth;
        float dus = d / w;
        float duration = dus * FULL_SCREEN_SCROLL_TIME + 0.5f;

        mScroller.startScroll(sx, sy, dx, dy, (int) duration);
        invalidate();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:// 刷新显示图片
                    break;
                case 100:// 回调动作
                    switch (msg.arg1) {
                        case 1:// view隐藏完毕
                            if (GestureViewChanged != null) {
                                GestureViewChanged.onClosed();
                            }
                            break;
                    }
                    break;
            }// end switch
        }
    };

    public interface GestureViewChanged {
        /**
         * View隐藏完成
         */
        public void onClosed();
    }

    public interface GestureViewScrollStateChanged {
        public void onScrollStateChanged(boolean isScrolling);
    }
}
