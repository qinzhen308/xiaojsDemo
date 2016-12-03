package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.LoginRequest;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginDataManager extends DataManager{

    /**
     * 调用登陆API，进行登陆
     *
     * @param params 登陆API中需要上传的参数
     */
    public static void requestLoginByAPI(@NonNull Context context,
                                         @NonNull LoginParams params,
                                         @NonNull APIServiceCallback<LoginInfo> callback) {

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the login request");
            }
            return;
        }


        LoginRequest loginRequest = new LoginRequest(context,callback);
        loginRequest.login(params);

    }

    /**
     * 调用退出登陆API，进行退出登陆
     */
    public static void requestLogoutByAPI(@NonNull Context context,
                                          @NonNull APIServiceCallback callback) {

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the logout request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session,callback)) {
            return;
        }


        LoginRequest loginRequest = new LoginRequest(context,callback);
        loginRequest.logout(session);
    }
}
