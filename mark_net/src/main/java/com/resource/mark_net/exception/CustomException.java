package com.resource.mark_net.exception;

import com.resource.mark_net.utils.log.LogUtils;


/**
 * 自定义错误信息，统一处理返回处理
 */
public class CustomException extends RuntimeException {
    private int resultCode;
    public static final int SERVER_ERROR = 100002;
    public static final int DB_ERROR = 200002;
    public static final int PARAM_ERROR = 300002;
    public static final int NO_USER = 400001; //用户不存在
    public static final int VIP_OUT_TIME = 400002; //Vip过期
    public static final int NOT_VIP = 400003; //不是Vip
    public static final int USER_LOGIN_EXCEPTION = 400004; //登录异常

    public CustomException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public CustomException(String detailMessage) {
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
            case USER_LOGIN_EXCEPTION:
                message = "用户登录异常";
                break;
            case NO_USER:
                message = "用户不存在";
                break;
            case NOT_VIP:
                message = "不是Vip";
                break;
            case VIP_OUT_TIME:
                message = "Vip过期";
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

