package com.resource.app.utils;

import android.app.Activity;
import java.util.Stack;

/**
 * Created by chenlong on 15-1-7.
 */
public class ActivityUtils {


    private Stack<Activity> activityStack;
    private volatile static ActivityUtils instance;

    private ActivityUtils() {
        activityStack = new Stack<>();
    }

    /**
     * 单一实例
     */
    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if(activityStack.size()>0){
            Activity activity = activityStack.lastElement();
            return activity;
        }else{
            return null;
        }
    }


    /**
     * 获取当前Activity之前的那个Activity
     * @return
     */
    public Activity currentPreviousActivity(){
        int size=activityStack.size();
        int pos=size-2;
        if(pos>=0){
            Activity activity =activityStack.get(pos);
            return activity;
        }else{
            return null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

}
