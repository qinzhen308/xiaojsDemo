package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.data.api.LoginRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginDataManager {

    /**
     * 调用登陆API，进行登陆
     *
     * @param params 登陆API中需要上传的参数
     */
    public static void requestLoginByAPI(@NonNull Context appContext,
                                         @NonNull LoginParams params,
                                         @NonNull APIServiceCallback<LoginInfo> callback) {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.login(appContext, params, callback);

    }

    /**
     * 调用退出登陆API，进行退出登陆
     */
    public static void requestLogoutByAPI(@NonNull Context appContext,
                                          String sessionID,
                                          @NonNull APIServiceCallback<LoginInfo> callback) {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.logout(appContext, sessionID, callback);
    }
}
