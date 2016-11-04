package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class RegisterRequest extends ServiceRequest{


    public void register(@NonNull Context appContext,
                         @NonNull RegisterInfo registerInfo,
                         @NonNull final APIServiceCallback callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();

        xiaojsService.accountRegister(registerInfo).enqueue(new Callback<Empty>() {
            @Override
            public void onResponse(Call<Empty> call, Response<Empty> response) {

                int responseCode = response.code();
                if (responseCode == 200) {

                    callback.onSuccess(null);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {
                        callback.onFailure(Errors.NO_ERROR);


                    } else {

                        callback.onFailure(ApiManager.parseErrorBody(errorBody));

                    }

                }

            }

            @Override
            public void onFailure(Call<Empty> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the register request has occur exception");
                }

                String errorMsg = t.getMessage();
                // FIXME: 2016/11/1
                if(errorMsg.contains("No content to map due to end-of-input")){
                    callback.onSuccess(null);
                }else{
                    callback.onFailure(Errors.NO_ERROR);
                }

            }
        });

    }


    public void validateCode(@NonNull Context appContext,
                             int method,
                             long mobile,
                             int verifycode,
                             @NonNull final APIServiceCallback callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();
        xiaojsService.validateCode(method, mobile, verifycode).enqueue(new Callback<APIEntity>() {
            @Override
            public void onResponse(Call<APIEntity> call, Response<APIEntity> response) {

                int responseCode = response.code();
                if (responseCode == 200) {

                    APIEntity apiEntity = response.body();
                    if (apiEntity != null) {

                        boolean match = apiEntity.isMatch();
                        if (match) {
                            callback.onSuccess(null);
                        } else {
                            callback.onFailure(Errors.NO_ERROR);
                        }

                    } else {
                        callback.onFailure(Errors.NO_ERROR);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {
                        callback.onFailure(Errors.NO_ERROR);


                    } else {

                        callback.onFailure(ApiManager.parseErrorBody(errorBody));

                    }

                }
            }

            @Override
            public void onFailure(Call<APIEntity> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the validate code request has occur exception");
                }

                callback.onFailure(Errors.NO_ERROR);

            }
        });

    }


    public void sendVerifyCode(@NonNull Context appContext,int method,
                 long mobile,
                 @NonNull final APIServiceCallback<VerifyCode> callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();
        xiaojsService.sendVerifyCode(method,mobile).enqueue(new Callback<VerifyCode>() {
            @Override
            public void onResponse(Call<VerifyCode> call, Response<VerifyCode> response) {
                int responseCode = response.code();

                if (responseCode == 200) {

                    VerifyCode verifyCode = response.body();
                    callback.onSuccess(verifyCode);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {
                        callback.onFailure(Errors.NO_ERROR);


                    } else {

                        callback.onFailure(ApiManager.parseErrorBody(errorBody));

                    }

                }
            }

            @Override
            public void onFailure(Call<VerifyCode> call, Throwable t) {

                if(XiaojsConfig.DEBUG){
                    Logger.d("the send code has occur exception");
                }

                callback.onFailure(Errors.NO_ERROR);
            }
        });
    }

}
