package com.resource.mark_net.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import com.resource.mark_net.common.DownInfo;
import com.resource.mark_net.utils.log.LogUtils;

import okhttp3.ResponseBody;

/**
 * 方法工具类
 * Created by newbiefly on 2017/05/02.
 */

public class AppUtil {
    /**
     * 描述：判断网络是否有效.
     *
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * 读取baseurl
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index);
        }
        return head + url;
    }

    /**
     * 读取baseurl
     * @param url
     * @return
     */
    public static String getRelUrl(String url) {
        int index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(index + 1, url.length());
        }
        return url;
    }


    /**
     * 写入文件
     * @throws IOException
     */
    public  static  void writeCache(ResponseBody responseBody, String savePath) throws Exception {
        File saveFile = new File(savePath);
        if (!saveFile.getParentFile().exists())
            saveFile.getParentFile().mkdirs();
        InputStream inputStream = responseBody.byteStream();
        FileOutputStream diff_file = new FileOutputStream(saveFile);
        byte[] fileReader = new byte[4096];
        while (true) {
            int read = inputStream.read(fileReader);
            if (read == -1) {
                break;
            }
            diff_file.write(fileReader, 0, read);
        }
        inputStream.close();
        diff_file.flush();
        responseBody.byteStream().close();
    }

    /**
     * 获取应用当前版名称
     */
    public static String getCurrentVersionName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("", "package info not get", e);
            return "";
        } catch (RuntimeException e) {
            LogUtils.e("com>resource.app", "package info not get", e);
            return "";
        }
    }

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}
