package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.schemas.Security;
import cn.xiaojs.xma.data.api.RegisterRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.account.RegisterInfo;
import cn.xiaojs.xma.model.VerifyCode;
import cn.xiaojs.xma.model.security.AuthenticateStatus;

import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class RegisterDataManager {

    /**
     * 调用注册API，进行注册
     * @param
     * @param info  注册API中需要上传的参数
     * @param callback
     */
    public static void requestRegisterByAPI(@NonNull final Context context,
                                            @NonNull final RegisterInfo info,
                                            @NonNull final APIServiceCallback callback){

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the register request");
            }
            return;
        }

        if (SecurityManager.needCheckSession(context)) {

            if (XiaojsConfig.DEBUG) {
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
                    AccountDataManager.saveSessionID(context, status.sessionID);

                    //now to register
                    RegisterRequest registerRequest = new RegisterRequest(context, callback);
                    registerRequest.register(info);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("reuest token failed,so register failed");
                    }

                    callback.onFailure(errorCode, errorMessage);
                }
            });

        }else {
            RegisterRequest registerRequest = new RegisterRequest(context,callback);
            registerRequest.register(info);
        }

    }

    /**
     * 验证验证码
     * @param
     * @param mobile
     * @param callback
     */
    public static void requestValidateCode(@NonNull Context context,
                                         long mobile,
                                         int verifycode,
                                         @NonNull final APIServiceCallback callback) {

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the validate Code request");
            }
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(context,callback);
        registerRequest.validateCode(Security.VerifyMethod.SMS_4_REGISTRATION, mobile, verifycode);
    }

    /**
     * 发送验证码
     * @param
     * @param mobile
     * @param callback
     */
    public static void requestSendVerifyCode(@NonNull Context context,
                                           long mobile,
                                           @NonNull final APIServiceCallback<VerifyCode> callback) {

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the send verify code request");
            }
            return;
        }


        RegisterRequest registerRequest = new RegisterRequest(context,callback);
        registerRequest.sendVerifyCode(Security.VerifyMethod.SMS_4_REGISTRATION, mobile);
    }

}
