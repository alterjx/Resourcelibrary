package com.resource.app;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/**
 * TODO: description
 */

public abstract class CustomHandler<T> extends Handler {
    private WeakReference<T> mTargets;

    public CustomHandler(T target) {
        this.mTargets = new WeakReference(target);
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T target = this.mTargets.get();
        if (target != null) {
            this.handle(target, msg);
        }

    }
    public abstract void handle(T var1, Message var2);
}
