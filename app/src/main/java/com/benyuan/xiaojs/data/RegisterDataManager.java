package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.data.api.RegisterRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;
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
    public static void requestRegisterByAPI(@NonNull Context context,
                                            @NonNull RegisterInfo info,
                                            @NonNull APIServiceCallback callback){

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the register request");
            }
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(context,callback);
        registerRequest.register(info);

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
