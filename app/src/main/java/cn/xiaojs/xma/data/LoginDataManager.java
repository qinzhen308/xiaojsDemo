package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.LoginRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.security.AuthenticateStatus;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;

import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginDataManager {

    /**
     * 调用登陆API，进行登陆
     *
     * @param params 登陆API中需要上传的参数
     */
    public static void requestLoginByAPI(@NonNull final Context context,
                                         @NonNull final LoginParams params,
                                         @NonNull final APIServiceCallback<LoginInfo> callback) {

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the login request");
            }
            return;
        }

        String session = AccountPref.getAuthToken(context);
        String token = SecurityPref.getCSRFToken(context);
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(session)) {

            if(XiaojsConfig.DEBUG){
                Logger.d("the token or session is null, so check session first");
            }

            SecurityManager.checkSession(context, new APIServiceCallback<AuthenticateStatus>() {
                @Override
                public void onSuccess(AuthenticateStatus status) {
                    String csrf;

                    if (status == null || TextUtils.isEmpty(csrf = status.csrf)) {
                        callback.onFailure(Errors.NO_ERROR, "获取Token为NULL");
                        return;
                    }

                    //save token and session
                    SecurityManager.saveCSRFToken(context, csrf);
                    AccountDataManager.saveSessionID(context,status.sessionID);

                    //now to login
                    LoginRequest loginRequest = new LoginRequest(context,callback);
                    loginRequest.login(params);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {

                    if(XiaojsConfig.DEBUG){
                        Logger.d("reuest token failed,so login failed");
                    }

                    callback.onFailure(errorCode,errorMessage);
                }
            });

        }else{
            LoginRequest loginRequest = new LoginRequest(context,callback);
            loginRequest.login(params);
        }
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




        LoginRequest loginRequest = new LoginRequest(context,callback);
        loginRequest.logout();
    }
}
