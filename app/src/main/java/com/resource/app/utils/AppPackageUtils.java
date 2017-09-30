package com.resource.app.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.resource.mark_net.utils.log.LogUtils;

/**
 * TODO: description
 * Date: 2017-07-04
 *
 * @author wanglei20
 */

public class AppPackageUtils {
    private static final String LOG_TAG = AppPackageUtils.class.getSimpleName();

    /**
     * 获取应用当前版本号
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(LOG_TAG, "package info not get", e);
            return 0;
        }
    }

    /**
     * 获取应用当前版名称
     */
    public static String getCurrentVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(LOG_TAG, "package info not get", e);
            return "";
        } catch (RuntimeException e) {
            LogUtils.e(LOG_TAG, "package info not get", e);
            return "";
        }
    }

    public static void installApkFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.resource.app.fileProvider", new File(filePath));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

}
