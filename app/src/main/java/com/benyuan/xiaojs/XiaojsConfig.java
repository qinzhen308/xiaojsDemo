package com.benyuan.xiaojs;

import com.benyuan.xiaojs.model.User;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class XiaojsConfig {

    //是否为debug版本
    public static final boolean DEBUG= true;

    //日志tag
    public static final String LOG_TAG = "XiaoJS-Log";

    //登录成功后用户信息, 退出后重置为null
    public static User mLoginUser;

    public static final String XIAOJS_PREFERENCE_NAME = "xiaojs_preference";
    public static final String KEY_LOGIN_USERNAME = "key-login-username";
    public static final String KEY_LOGIN_PASSWORD = "key-login-password";
}
