/**
 * Copyright (C) 2006-2016 Tuniu All rights reserved
 */
package com.resource.mark_net.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.resource.mark_net.R;
import com.resource.mark_net.utils.log.LogUtils;

/**
 * 权限申请Mediator
 */
public class PermissionMediator {

    /**
     * 权限申请回调listener
     */
    public interface OnPermissionRequestListener {
        /**
         * 单条权限申请回调
         * 多条权限申请时也需要回调此方法，因为只申请非授权的权限
         */
        void onPermissionRequest(boolean granted, String permission);

        /**
         * 多条权限申请回调
         */
        void onPermissionRequest(boolean isAllGranted, @NonNull String[] permissions, int[] grantResults);
    }

    public static abstract class DefaultPermissionRequest implements PermissionMediator.OnPermissionRequestListener {
        @Override
        public void onPermissionRequest(boolean granted, String permission) {

        }

        @Override
        public void onPermissionRequest(boolean isAllGranted, @NonNull String[] permissions, int[] grantResults) {

        }
    }

    private static final String TAG = PermissionMediator.class.getSimpleName();

    // FragmentActivity要求request code为8位
    private static int sIncRequestCode = 0;

    private static Map<Integer, OnPermissionRequestListener> mListenerMap = new HashMap<>();

    public static void dispatchPermissionResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        for (int index = 0; index < permissions.length; index++) {
            LogUtils.i(TAG, "request {}, {}", permissions[index], (grantResults[index] == PackageManager.PERMISSION_GRANTED ? true : false));
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[index])) {
                    //用户之前拒绝，并勾选不再提示时，引导用户去设置页面开启权限
                    showGrantToast(activity.getApplicationContext(), permissions[index]);
                }
            }
        }
        OnPermissionRequestListener listener = mListenerMap.get(requestCode);
        if (listener != null) {
            if (permissions.length == 1) {
                listener.onPermissionRequest(grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0]);
            } else {
                boolean isAllGranted = true;
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false;
                        listener.onPermissionRequest(false, permissions, grantResults);
                        break;
                    }
                }
                if (isAllGranted) {
                    listener.onPermissionRequest(true, permissions, grantResults);
                }
            }
            mListenerMap.remove(requestCode);
        }
    }

    public static void checkPermission(Activity activity, String permission, OnPermissionRequestListener listener) {
        checkPermission(activity, new String[]{permission}, listener);
    }

    public static void checkPermission(Activity activity, String[] permissions, OnPermissionRequestListener listener) {
        if (activity == null || permissions == null || permissions.length <= 0) {
            return;
        }
        List<String> unauthorizedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (StringUtils.isNullOrEmpty(permission)) {
                continue;
            }
            try {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    LogUtils.i(TAG, "request {}", permission);
                    unauthorizedPermissions.add(permission);
                } else {
                    LogUtils.i(TAG, "already has {} permission", permission);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "check self permission failed. {}", e.toString());
                unauthorizedPermissions.add(permission);
            }
        }
        if (!unauthorizedPermissions.isEmpty()) {
            sIncRequestCode++;
            if (sIncRequestCode > 255) {
                sIncRequestCode = 0;
            }
            mListenerMap.put(sIncRequestCode, listener);
            ActivityCompat.requestPermissions(activity, unauthorizedPermissions.toArray(new String[unauthorizedPermissions.size()]), sIncRequestCode);
        } else {
            if (listener != null) {
                if (permissions.length == 1) {
                    listener.onPermissionRequest(true, permissions[0]);
                } else {
                    listener.onPermissionRequest(true, permissions, null);
                }
            }
        }
    }

    /**
     * 判断是否拒绝权限并勾选不再提示
     */
    public static boolean permissionIsRefuseAndHide(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void showGrantToast(Context context, String unauthorizedPermission) {
        if (StringUtils.isNullOrEmpty(unauthorizedPermission)) {
            return;
        }
        if (Manifest.permission.CALL_PHONE.equals(unauthorizedPermission)) {
            DialogUtil.showShortPromptToast(context, R.string.grant_permission_phone_call);
        } else if (Manifest.permission.CAMERA.equals(unauthorizedPermission)) {
            DialogUtil.showShortPromptToast(context, R.string.grant_permission_camera);
        } else if (Manifest.permission.RECORD_AUDIO.equals(unauthorizedPermission)) {
            DialogUtil.showShortPromptToast(context, R.string.grant_permission_audio);
        } else if (Manifest.permission.READ_CONTACTS.equals(unauthorizedPermission)) {
            DialogUtil.showShortPromptToast(context, R.string.grant_permission_read_contact);
        }
    }
}
