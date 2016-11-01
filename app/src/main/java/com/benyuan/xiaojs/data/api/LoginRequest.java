package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginRequest {


    public void login(Context appContext,
                      LoginParams params,
                      @NonNull final APIServiceCallback<LoginInfo> callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();

        xiaojsService.login(params).enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {

                int responseCode = response.code();

                if (responseCode == 200) {

                    LoginInfo info = response.body();
                    callback.onSuccess(info);

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
            public void onFailure(Call<LoginInfo> call, Throwable t) {

                if(XiaojsConfig.DEBUG){
                    Logger.d("the login has occur exception");
                }

                callback.onFailure(Errors.NO_ERROR);
            }
        });

    }

    public void logout(Context appContext,String sessionID,@NonNull final APIServiceCallback callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();
        xiaojsService.logout(sessionID).enqueue(new Callback<Empty>() {
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

                if(XiaojsConfig.DEBUG){
                    Logger.d("the logout has occur exception");
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


//    public void logout(Context appContext,String sessionID,@NonNull final APIServiceCallback callback) {
//
//        XiaojsService xiaojsService = ApiManager.getAPIManager(appContext).getXiaojsService();
//        xiaojsService.logout(sessionID).enqueue(new Callback<APIEntity>() {
//            @Override
//            public void onResponse(Call<APIEntity> call, Response<APIEntity> response) {
//
//                int responseCode = response.code();
//                if (responseCode == 200) {
//
//                    callback.onSuccess(null);
//
//                } else {
//
//
//                    String errorBody = null;
//                    try {
//                        errorBody = response.errorBody().string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (TextUtils.isEmpty(errorBody)) {
//                        callback.onFailure(Errors.NO_ERROR);
//
//
//                    } else {
//
//                        callback.onFailure(ApiManager.parseErrorBody(errorBody));
//
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<APIEntity> call, Throwable t) {
//
//                if(XiaojsConfig.DEBUG){
//                    Logger.d("the logout has occur exception");
//                }
//
//                callback.onFailure(Errors.NO_ERROR);
//            }
//        });
//
//    }

}