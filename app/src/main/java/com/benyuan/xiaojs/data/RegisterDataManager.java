package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.data.api.RegisterRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class RegisterDataManager {

    /**
     * 调用注册API，进行注册
     * @param appContext
     * @param info  注册API中需要上传的参数
     * @param callback
     */
    public static void requestRegisterByAPI(@NonNull Context appContext,
                                            @NonNull RegisterInfo info,
                                            @NonNull APIServiceCallback callback){

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.register(appContext,info,callback);

    }

    /**
     * 验证验证码
     * @param appContext
     * @param mobile
     * @param callback
     */
    public static void requestValidateCode(@NonNull Context appContext,
                                         long mobile,
                                         int verifycode,
                                         @NonNull final APIServiceCallback callback) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.validateCode(appContext,
                Security.VerifyMethod.SMS_4_REGISTRATION,
                mobile,
                verifycode,
                callback);
    }

    /**
     * 发送验证码
     * @param appContext
     * @param mobile
     * @param callback
     */
    public static void requestSendVerifyCode(@NonNull Context appContext,
                                           long mobile,
                                           @NonNull final APIServiceCallback<VerifyCode> callback) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.sendVerifyCode(appContext,
                Security.VerifyMethod.SMS_4_REGISTRATION,
                mobile,
                callback);
    }

}
