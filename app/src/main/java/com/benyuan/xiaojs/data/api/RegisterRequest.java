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

    public RegisterRequest(Context context,APIServiceCallback callback) {

        super(context,callback);

    }

    public void register(@NonNull RegisterInfo registerInfo) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();

        xiaojsService.accountRegister(registerInfo).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.REGISTER,response);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.REGISTER,t);

            }

        });

    }


    public void validateCode(int method, long mobile, int verifycode) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.validateCode(method, mobile, verifycode).enqueue(new Callback<APIEntity>() {
            @Override
            public void onResponse(Call<APIEntity> call, Response<APIEntity> response) {

                onRespones(APIType.VALIDATE_CODE,response);
            }

            @Override
            public void onFailure(Call<APIEntity> call, Throwable t) {

                onFailures(APIType.VALIDATE_CODE,t);

            }
        });

    }


    public void sendVerifyCode(int method, long mobile) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.sendVerifyCode(method, mobile).enqueue(new Callback<VerifyCode>() {
            @Override
            public void onResponse(Call<VerifyCode> call, Response<VerifyCode> response) {

                onRespones(APIType.VERIFY_MOBILE,response);
            }

            @Override
            public void onFailure(Call<VerifyCode> call, Throwable t) {

                onFailures(APIType.VERIFY_MOBILE,t);

            }
        });
    }

}
