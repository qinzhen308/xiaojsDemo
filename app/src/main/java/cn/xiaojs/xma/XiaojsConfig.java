package cn.xiaojs.xma;

import cn.xiaojs.xma.model.account.User;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class XiaojsConfig {

    //是否为debug版本
    public static final boolean DEBUG= true;

    //日志tag
    public static final String LOG_TAG = "XiaoJS-Log";

    //BASE_URL
    public static String BASE_URL = "http://192.168.100.3";

    //HTTP缓存目录
    public static final String HTTP_CACHE_DIR = "hcache";
    //HTTP缓存目录最大SIZE
    public static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024;//10MiB

    //DB name
    public static final String DB_NAME = "xdb";
    //DB current version
    public static final int DB_VERSION = 1;


    //登录成功后用户信息, 退出后重置为null
    public static User mLoginUser;

    public static final String XIAOJS_PREFERENCE_NAME = "xiaojs_preference";
    public static final String KEY_LOGIN_USERNAME = "key-login-username";

    public static final int PORTRAIT_SIZE = 300;

    public static final int EXIT_DELAY = 1000;


    //wechat
    public static final String WX_APP_ID = "wxb0290782ab5b55b5";
    //QQ
    public static final String QQ_APP_ID = "1105919395";


    public static final String PING_WALLET = "con.xiaojs.xma.wallet";

    //默认渠道
    public static final String DEFAULT_CHANNEL = "N/A";

}
