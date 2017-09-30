/**
 * Copyright (C) 2006-2017 Tuniu All rights reserved
 */
package com.resource.mark_net.utils;

import android.app.Application;

/**
 * TODO: description
 * Date: 2017-05-02
 *
 * @author newbiefly
 */
public class RxRetrofitApp {
    private static Application mApplication;
    private static boolean mDebug = true;


    public static void init(Application app){
        setApplication(app);
        setDebug(true);
    }

    public static void init(Application app,boolean debug){
        setApplication(app);
        setDebug(debug);
    }

    public static Application getApplication() {
        return mApplication;
    }

    private static void setApplication(Application application) {
        RxRetrofitApp.mApplication = application;
    }

    public static boolean isDebug() {
        return mDebug;
    }

    public static void setDebug(boolean debug) {
        RxRetrofitApp.mDebug = debug;
    }
}
