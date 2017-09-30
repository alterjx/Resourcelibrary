package com.resource.mark_net.common;

import java.io.Serializable;

/**
 * 回调信息统一封装类
 */
public class BaseResultEntity implements Serializable {
    public boolean success;
    public int errorCode;
    public String msg;
    public Object data;
    public Object errorData;
    public boolean isFromCache = false;
    public BaseResultEntity() {
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("success:").append(this.success);
        buf.append("errorcode:").append(this.errorCode);
        buf.append("msg:").append(this.msg);
        buf.append("data:").append(this.data == null?"":this.data.toString());
        buf.append("errorData:").append(this.errorData == null?"":this.errorData.toString());
        return buf.toString();
    }
}
