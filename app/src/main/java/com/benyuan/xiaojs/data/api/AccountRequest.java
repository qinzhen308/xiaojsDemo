package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.HomeData;
import com.orhanobut.logger.Logger;


import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/3.
 */

public class AccountRequest extends ServiceRequest {


    public void getHomeData(Context context,
                            @NonNull String sessionID,
                            @NonNull APIServiceCallback<HomeData> callback) {


        final WeakReference<APIServiceCallback<HomeData>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getHomeData(sessionID).enqueue(new Callback<HomeData>() {
            @Override
            public void onResponse(Call<HomeData> call, Response<HomeData> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    HomeData homeData = response.body();

                    APIServiceCallback<HomeData> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(homeData);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getHomeDataPrompt(errorCode);

                    APIServiceCallback<HomeData> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<HomeData> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the login has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getHomeDataPrompt(errorCode);

                APIServiceCallback<HomeData> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(Errors.NO_ERROR, errorMessage);
                }

            }
        });

    }


    public void claimCompetency(Context context,
                                String sessionID,
                                CompetencyParams competencyParams,
                                @NonNull APIServiceCallback<ClaimCompetency> callback) {

        final WeakReference<APIServiceCallback<ClaimCompetency>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.claimCompetency(sessionID, competencyParams).enqueue(new Callback<ClaimCompetency>() {
            @Override
            public void onResponse(Call<ClaimCompetency> call, Response<ClaimCompetency> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    ClaimCompetency claimCompetency = response.body();

                    APIServiceCallback<ClaimCompetency> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(claimCompetency);
                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.claimCompetencyPrompt(errorCode);

                    APIServiceCallback<ClaimCompetency> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<ClaimCompetency> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the login has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.claimCompetencyPrompt(errorCode);

                APIServiceCallback<ClaimCompetency> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });

    }
}
