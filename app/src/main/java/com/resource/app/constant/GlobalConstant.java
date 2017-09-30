package com.resource.app.constant;

public class GlobalConstant {
    public interface SavedInstanceStateConstant{
        public static final String HOME_CURRENT_TAB_POSITION="HOME_CURRENT_TAB_POSITION";
        public static final String MENU_SHOW_HIDE="MENU_SHOW_HIDE";
    }

    public interface PlatoformType {
        public static String QQ = "QQ";
        public static String Wechat = "Wechat";
        public static String SinaWeibo = "SinaWeibo";
    }

    public interface RxBus{
        public static final String MENU_SHOW_HIDE="MENU_SHOW_HIDE";
        public static String NEWS_LIST_TO_TOP = "NEWS_LIST_TO_TOP";//列表返回顶部
        public static final String LOGIN_SUCCESS="LOGIN_SUCCESS";
        public static final String TOP_BUTTON_SHOW_HIDE="TOP_BUTTON_SHOW_HIDE";
        public static final String TOP_SHOW_HIDE="TOP_SHOW_HIDE";
        public static final String PIC_TOP_BUTTON_CLICK ="PIC_TOP_BUTTON_CLICK";
        public static final String SEARCH_TOP_BUTTON_CLICK ="SEARCH_TOP_BUTTON_CLICK";
        public static final String SERACH_HISTORY_CLICK="SERACH_HISTORY_CLICK";
        public static final String SEARCH_TOP_SHOW_HIDE="search_top_show_hide";
        public static final String SERACH_MAIN_HEAD_EXPLAND="serach_main_head_expland";
        public static final String UPGRADE_RESULT="upgrade_result";
        public static final String FILM_TOP_BUTTON_CLICK ="film_top_button_click";

    }

    public interface IntentConstant {
        public static final String FILM_PAGE_TYPE = "film_page_type";
        public static final String PIC_TYPE = "pic_type";
        public static final String DETAIL_TYPE = "detail_type";
        public static final String PIC_DETAIL_INFO = "pic_detail_info";
        public static final String TARGETACTIVITY_NAME = "targetactivity_name";
        public static final String PIC_SEARCH_RESULT = "pic_search_result";
        public static final String PIC_SEARCH_C0NTENT = "pic_search_content";
        public static final String VIDEO_SEARCH_RESULT = "video_search_result";
        public static final String VIDEO_SEARCH_C0NTENT = "video_search_content";
        public static final String FILM_TYPE = "film_type";
        public static final String VIDEO_DETAIL_INFO = "VIDEO_DETAIL_INFO";
    }

    public interface IntentPreference {
        public static final String USER_INFO = "user_info";
        public static final String PIC_MENU = "pic_menu";
        public static final String FILM_MENU = "film_menu";
        public static final String DOMAIN_URL = "domain_url";
        public static final String APP_NOTICE = "app_notice";
        public static final String SEARCH_HISTORY = "search_history";
        public static final String SEARCH_TAB = "search_tab";
        public static final String UPGRADE_DATA = "upgrade_data";
        public static final String APP_DECLARE = "app_declare";
        public static final String PAY_INFO = "pay_info";
        public static final String SHOW_ADV = "show_adv";
    }

    public interface DeatilType {
        public static final int PIC = 1;
        public static final int FILM = 2;
    }

    public interface VipType {
        public static final int COMMON = 0;
        public static final int TIME_VIP = 88;
        public static final int VIP = 99;
    }

    public interface SearchResultTitle {
        public static final String SEARCH_PIC_TITLE = "美女";
        public static final String SEARCH_FILM_TITLE = "视频";
    }



    public interface AppTextConfig {
        public static final String APP_NORMAL_URL = "app_normal_url";
        public static final String MINE_NOTICE = "minefragment_notice";
        public static final String APP_DECLARE = "app_declare";
        public static final String SEARCH_PIC_TABS = "search_pic_tabs";
    }

    public interface AppUpgrade {
        public static final int NORMAL_UPDATE = 2;
        public static final int FORCE_UPDATE = 1;
    }

    public interface PayType {
        public static final int ALI_PAY = 1; //支付宝
        public static final int WEIXIN_PAY = 2; //微信
    }

    public interface PayResult {
        public static final String ALI_PAY_SUCCESS = "9000"; //支付宝支付成功
        public static final String ALI_PAY_CANCLE= "6001"; //支付宝支付取消
        public static final String ALI_PAY_NET_ERROR= "6002"; //支付宝支付网络出错
        public static final String ALI_PAY_NO_ALI_APP= "4000"; //没有安装支付宝客户端
    }

}
