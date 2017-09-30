package com.resource.app.customview;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * TODO: description
 * Date: 2017-08-07
 */

public class BottomAnimationView extends LinearLayout {
    private static final int COLOR_ALPHA = 0x00000000;
    private static final int COLOR_BLACK = 0x4D000000;
    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final int BOTTOM_ANIMATION_DURATION = 400;
    private static final int BOTTOM_OUT_ANIMATION_DURATION = 300;
    private TranslateAnimation mInFromBottomAnimation;
    private TranslateAnimation mOuToBottomAnimation;
    private ValueAnimator mAlphaToBalck;
    private ValueAnimator mBalckToAlpha;
    public Context mContext;

    public BottomAnimationView(Context context) {
        this(context,null);
    }
    public BottomAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAnimation();
    }

    private void initAnimation() {
        mInFromBottomAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mInFromBottomAnimation.setDuration(BOTTOM_ANIMATION_DURATION);
        mOuToBottomAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mOuToBottomAnimation.setDuration(BOTTOM_OUT_ANIMATION_DURATION);
    }

    public void startBottomAnimation( View bgFrameLayout) {
        if (getVisibility() == View.VISIBLE) {
            mBalckToAlpha = ObjectAnimator.ofInt(bgFrameLayout, BACKGROUND_COLOR, COLOR_BLACK, COLOR_ALPHA);
            mBalckToAlpha.setDuration(BOTTOM_ANIMATION_DURATION);
            mBalckToAlpha.setEvaluator(new ArgbEvaluator());
            startAnimation(mOuToBottomAnimation);
            mOuToBottomAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mBalckToAlpha.start();
                    setClickable(false);

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            setVisibility(View.GONE);
        } else {
            mAlphaToBalck = ObjectAnimator.ofInt(bgFrameLayout, BACKGROUND_COLOR, COLOR_ALPHA, COLOR_BLACK);
            mAlphaToBalck.setDuration(BOTTOM_ANIMATION_DURATION);
            mAlphaToBalck.setEvaluator(new ArgbEvaluator());
            startAnimation(mInFromBottomAnimation);
            mInFromBottomAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mAlphaToBalck.start();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            setVisibility(View.VISIBLE);
        }
    }


}
