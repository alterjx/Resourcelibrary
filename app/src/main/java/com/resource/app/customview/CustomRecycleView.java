package com.resource.app.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by newbiefly on 2016/9/28.
 */
public class CustomRecycleView extends RecyclerView {

    public CustomRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public CustomRecycleView(Context context) {
      super(context,null);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
       Log.w("newbiefly","onInterceptTouchEvent:");
        return false;
    }
}
