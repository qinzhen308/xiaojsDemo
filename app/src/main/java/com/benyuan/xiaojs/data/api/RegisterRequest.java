package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class RegisterRequest extends ServiceRequest {

    public RegisterRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void register(@NonNull RegisterInfo registerInfo) {

        Call<ResponseBody> call = getService().accountRegister(registerInfo);
        enqueueRequest(APIType.REGISTER, call);
    }


    public void validateCode(int method, long mobile, int verifycode) {

        Call<APIEntity> call = getService().validateCode(method, mobile, verifycode);
        enqueueRequest(APIType.VALIDATE_CODE, call);

    }


    public void sendVerifyCode(int method, long mobile) {

        Call<VerifyCode> call = getService().sendVerifyCode(method, mobile);
        enqueueRequest(APIType.VERIFY_MOBILE, call);

    }

}
