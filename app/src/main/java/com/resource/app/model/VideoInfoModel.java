package com.resource.app.model;

import java.io.Serializable;

/**
 * TODO: description
 * Date: 2017-08-21
 */

public class VideoInfoModel implements Serializable{
    public String id;
    public String videoId; //视频编号
    public String f_id; //分类
    public String name;
    public int type;
    public String firstUrl; //
    public String picUrls;//app处理时应注意，如果链接中包含了http就不用拼接
    public String videoUrls; //app处理时应注意，如果链接中包含了http就不用拼接
    public Long time;
    public String baiduUrl;
    public String pwd;
    public String videoTime; //视频时长
    public int likeAmount;//赞
    public int commentAmout;
    public int videoAmout;
    public String video_domain_type;
    public String pic_domain_type;
    public int isFree; //0免费 1付费
    public String keyValue;
    public int count_id;
    public int isDownLoad;
}
