package com.resource.mark_net.utils;
import java.io.File;

import android.content.Context;

import okhttp3.Cache;

/**
 * Created by newbiefly on 2016/6/25.
 */
public class CacheUtil {

    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

    private static File getCacheDir(Context context) {
        //设置缓存路径
        final File baseDir = context.getCacheDir();
        final File cacheDir = new File(baseDir, "FileMarkCache");
        return cacheDir;
    }

    public static Cache getCache(Context context) {
        if (context == null) {
            return null;
        }
        return new Cache(getCacheDir(context), HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }
}
