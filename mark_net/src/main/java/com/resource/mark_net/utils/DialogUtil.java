package com.resource.mark_net.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.resource.mark_net.utils.helper.IProgressDialog;

public class DialogUtil {

    public static void showProgressDialog(Context context, int resId) {
        if (context != null && context instanceof IProgressDialog) {
            IProgressDialog progressDialog = (IProgressDialog) context;
            progressDialog.showProgressDialog(resId);
        }
    }

    public static void dismissProgressDialog(Context context) {
        if (context != null && context instanceof IProgressDialog) {
            IProgressDialog progressDialog = (IProgressDialog) context;
            progressDialog.dismissProgressDialog();
        }
    }

    public static void showShortPromptToast(Context context, int resid) {
        try {
            showShortPromptToast(context, context.getString(resid));
        } catch (Exception e) {
        }
    }

    public static void showShortPromptToast(Context context, String res) {
        if (StringUtils.isNullOrEmpty(res)) {
            res = "";
        }
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }

    public static void showLongPromptToast(Context context, int resid) {
        try {
            showLongPromptToast(context, context.getString(resid));
        } catch (Exception e) {
        }
    }

    public static void showLongPromptToast(Context context, String res) {
        if (StringUtils.isNullOrEmpty(res)) {
            res = "";
        }
        Toast.makeText(context, res, Toast.LENGTH_LONG).show();
    }

    public static Dialog createAlertDialog(Context context, String title, String message, String positiveButton, String negativeButton, final DialogInterface.OnClickListener positiveButtonListener, final DialogInterface.OnClickListener negativeButtonListener) {
        Dialog dialog = null;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(positiveButton, positiveButtonListener)
                .setNegativeButton(negativeButton, negativeButtonListener);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (negativeButtonListener != null) {
                    negativeButtonListener.onClick(dialog, 0);
                }
            }
        });
        return dialog;
    }

    /**
     * 自定义AlertDialog（包含确认，中立button）
     */
    public static Dialog createAlertDialog(Context context, View view, String positiveButton, String neutralButton, final DialogInterface.OnClickListener positiveButtonListener, final DialogInterface.OnClickListener neutralButtonListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(positiveButton, positiveButtonListener)
                .setNeutralButton(neutralButton, neutralButtonListener);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 自定义AlertDialog（只有确定按钮）
     */
    public static Dialog createAlertDialog(Context context, View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }
}
