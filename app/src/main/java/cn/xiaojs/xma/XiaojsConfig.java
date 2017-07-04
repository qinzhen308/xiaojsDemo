package cn.xiaojs.xma;

import cn.xiaojs.xma.model.account.User;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class XiaojsConfig {

    //是否为debug版本
    public static final boolean DEBUG = BuildConfig.DEBUG;

    //是否是演示版本
    public static final boolean SHOW_DEMO = true;
    //日志TAG
    public static final String LOG_TAG = "XiaoJS-Log";
    //客户端版本号位数
    public static final int VERSION_BITS = 4;


    //生产环境小教室账号ID
    public static final String XAIOJS_ACCOUNT_ID = "58d255856734e6fe413589e0";
    //测试环境小教室账号ID
    public static final String TEST_XAIOJS_ACCOUNT_ID = "58d0aaac8b8d4bf95c396c1d";



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //channel
    //渠道好号，被Application初始化
    public static String CHANNEL;

    //预发布环境渠道,此名称要对应channel文件
    public static final String CHANNEL_ENV_PRE = "env-pre";
    //开发测试环境渠道,此名称要对应channel文件
    public static final String CHANNEL_ENV_DEVTEST = "env-dev&test";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //url and port
    ///////////////外网生产环境域名/////////////
    //XAS
    public static final String XAS_BASE_URL = "http://xas.xiaojs.cn";
    //XLS
    public static final String XLS_BASE_URL = "http://xls.xiaojs.cn";
    //分享URL
    public static final String SHARE_BASE_URL = "http://www.xiaojs.cn";
    //分享课（公开课和班课）
//    public static final String SHARE_LESSON_BASE_URL = "http://wxtest.xiaojs.cn";
    public static final String SHARE_LESSON_BASE_URL = "http://wx.xiaojs.cn";

    //七牛存储空间
    // 华东:存储空间列表(图片和文档存储)Bucket (files) CDN 加速域名
    public static final String FILES_BUCKET_URL = "http://xcfs.xiaojs.cn";
    //华北:存储空间列表(视频存储)Bucket (live) CDN 加速域名
    public static final String LIVE_BUCKET_URL = "http://live.xiaojs.cn";

    ///////////////外网预发布环境域名/////////////
    //XAS
    public static String XAS_PRE_BASE_URL = "http://xas.xiaojs.edu";
    //XLS
    public static String XLS_PRE_BASE_URL = "http://xls.xiaojs.edu";

    //七牛存储空间
    //华东:存储空间列表(图片和文档存储)Bucket (xiaojs-test) 测试域名:
    public static final String PRE_FILES_BUCKET_URL = "http://omjsoavuo.bkt.clouddn.com";
    //华北:存储空间列表(视频存储)Bucket (xiaojs)
    public static final String PRE_LIVE_BUCKET_URL = "http://oi29a0dpo.bkt.clouddn.com";



    ///////////////测试&开发环境////////////////
    public static String TEST_BASE_URL = "http://192.168.100.3";
    //Xiaojs rest api 中接口公共URL
    public static String SERVICE_PORT = "3000";
    //Live port
    public static String LIVE_PORT = "3004";

    //七牛存储空间
    //华东华北跟预发布的相同


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Article Content ID
    public static final String ARTICLE_ID_ABOUT = "aboutus_android";
    public static final String ARTICLE_ID_AGREEMENT = "userAgreement";
    public static final String ARTICLE_ID_HELP = "Classroom_use_guide";




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //API
    //HTTP缓存目录
    public static final String HTTP_CACHE_DIR = "hcache";
    //HTTP缓存目录最大SIZE
    public static final int HTTP_CACHE_SIZE = 10 * 1024 * 1024;//10MiB



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //DB name
    public static final String DB_NAME = "xdb";
    //DB current version
    public static final int DB_VERSION = 2;





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //第三方SDK相关
    //wechat
    public static final String WX_APP_ID = "wxecffc72c21a94f5a";
    public static final String WX_APP_KEY = "11f406e3caf4ea45a3dab85e19937226";
    //QQ
    public static final String QQ_APP_ID = "1105994736";
    public static final String QQ_APP_KEY = "TGIJEvzkjNRLT9Zb";

    //bugly
    //bugly------------生产环境使用----------
    public static final String BUGLY_APP_ID = "900060174";
    //bugly------------开发和预发布环境使用----------
    public static final String BUGLY_APP_ID_DEV = "d4347d7369";

    //Jpush app key
    public static final String JPUSH_APP_KEY = "e87cffb332432eec3c0807ba";
    //ping++
    public static final String PING_WALLET = "con.xiaojs.xma.wallet";

    //umeng
    public static final String UMENG_APPKEY = "592f92426e27a41dc2001d26";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //其他
    //file provider
    public static final String FILE_PROVIDER= "cn.xiaojs.xma.fileprovider";

    //登录成功后用户信息, 退出后重置为null
    public static User mLoginUser;

    public static final String XIAOJS_PREFERENCE_NAME = "xiaojs_preference";
    public static final String KEY_LOGIN_USERNAME = "key-login-username";

    public static final int PORTRAIT_SIZE = 300;

    public static final int EXIT_DELAY = 1000;

    public static String AVATOR_TIME;

    public static boolean CURRENT_PAGE_IN_MESSAGE = false;

    public static final String APP_QRCODE_URL = "http://www.xiaojs.cn/static/img/phone-xma.png";
    //分享二维码获取图片和图片域名
//    public static final String APP_QRCODE_IMG_BASE_URL = "http://myxjs.tunnel.2bdata.com";
    public static final String APP_QRCODE_IMG_BASE_URL = "http://wxtest.xiaojs.cn";


}
