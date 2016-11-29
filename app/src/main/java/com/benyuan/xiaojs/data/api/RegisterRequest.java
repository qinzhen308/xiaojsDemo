package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class RegisterRequest extends ServiceRequest {


    public void register(@NonNull Context appContext,
                         @NonNull RegisterInfo registerInfo,
                         @NonNull final APIServiceCallback callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();

        xiaojsService.accountRegister(registerInfo).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    if (callback != null) {
                        callback.onSuccess(null);
                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.registerPrompt(errorCode);

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.registerPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
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
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    APIEntity apiEntity = response.body();
                    if (apiEntity != null) {

                        boolean match = apiEntity.isMatch();
                        if (match) {

                            if (callback != null) {
                                callback.onSuccess(null);
                            }

                            return;
                        }

                    }

                    String errorCode = getDefaultErrorCode();
                    String errorMessage = ErrorPrompts.validateCodePrompt(errorCode);

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.validateCodePrompt(errorCode);

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<APIEntity> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.validateCodePrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });

    }


    public void sendVerifyCode(@NonNull Context appContext, int method,
                               long mobile,
                               @NonNull final APIServiceCallback<VerifyCode> callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();
        xiaojsService.sendVerifyCode(method, mobile).enqueue(new Callback<VerifyCode>() {
            @Override
            public void onResponse(Call<VerifyCode> call, Response<VerifyCode> response) {

                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    VerifyCode verifyCode = response.body();

                    if (callback != null) {
                        callback.onSuccess(verifyCode);
                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.sendCodePrompt(errorCode);

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<VerifyCode> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.sendCodePrompt(errorCode);
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });
    }

}
