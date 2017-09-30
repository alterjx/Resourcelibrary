package com.resource.mark_net.exception;

import com.resource.mark_net.utils.log.LogUtils;

/**
 * 自定义错误信息，统一处理返回处理
 */
public class HttpException extends RuntimeException {

    public static final int NO_DATA = 0x1;
    public static final int  CONVERT_ERROR = 0x2; //数据转换异常
    public static final int  BODY_EMPTY = 0x3; //body为null
    public static final int SERVER_ERROR = 100002;
    public static final int DB_ERROR = 200002;
    public static final int PARAM_ERROR = 300002;

    public HttpException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public HttpException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 转换错误数据
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        String message = "";
        switch (code) {
            case NO_DATA:
                message = "无数据";
                break;
            case BODY_EMPTY:
                message = "body为null";
                break;
            case CONVERT_ERROR:
                message = "数据转换";
                break;
            case SERVER_ERROR:
                message = "服务器异常";
                break;
            case DB_ERROR:
                message = "数据库异常";
                break;
            case PARAM_ERROR:
                message = "入参异常";
                break;
            default:
                message = "error";
                break;

        }
        LogUtils.e("com.resource.app",message);
        return message;
    }
}

