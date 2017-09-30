package com.resource.mark_net.utils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import android.widget.Toast;

import com.resource.mark_net.exception.CustomException;
import com.resource.mark_net.exception.HttpException;
import com.resource.mark_net.utils.log.LogUtils;

/**
 * TODO: description
 * Date: 2017-07-13
 *
 * @author wanglei20
 */

public class ErrorUtil {
    /*错误统一处理*/
    public static void errorLog(Throwable e) {
        LogUtils.e("com.resource.app_errorLog",e == null || e.getMessage() == null ? "Throwable is NUll" : e.getMessage());
    }

    public static void errorDo(Throwable e) {
        if (RxRetrofitApp.getApplication() == null || e == null) {
            return;
        }

        if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            if (HttpNetUtil.INSTANCE.isConnected()) {
                Toast.makeText(RxRetrofitApp.getApplication(), "系统升级中，请稍后再试！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RxRetrofitApp.getApplication(), "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
            }
        } else if (e instanceof HttpException) {
            Toast.makeText(RxRetrofitApp.getApplication(), "系统升级中，请稍后再试！", Toast.LENGTH_SHORT).show();
        }else if (e instanceof CustomException) {
            //Toast.makeText(RxRetrofitApp.getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(RxRetrofitApp.getApplication(), "系统升级中，请稍后再试！", Toast.LENGTH_SHORT).show();
        }
    }
}
