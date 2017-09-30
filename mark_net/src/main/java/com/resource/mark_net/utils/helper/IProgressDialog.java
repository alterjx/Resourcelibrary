/**
 * Copyright (C) 2006-2014 Tuniu All rights reserved
 */
package com.resource.mark_net.utils.helper;

/**
 * 加载对话框接口，确保只有Activity才实现这个接口
 */
public interface IProgressDialog {

    /**
     * 显示加载对话框，使用resId对应的字符串资源
     *
     * @param resId string的资源id
     */
    void showProgressDialog(int resId);

    /**
     * 关闭对话框
     */
    void dismissProgressDialog();
}
