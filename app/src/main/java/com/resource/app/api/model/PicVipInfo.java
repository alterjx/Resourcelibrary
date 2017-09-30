package com.resource.app.api.model;

import java.io.Serializable;

/**
 * TODO: description
 * Date: 2017-07-10
 *
 */

public class PicVipInfo implements Serializable{
    public String id;
    public String urls; //分号分割
    public String baiduUrl;
    public String pwd;
    public String domain_type;
    public int type;
    public String viewNumber;
    public int isFree; //0免费 1付费
}
