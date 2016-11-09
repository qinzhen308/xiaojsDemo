package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/3.
 */

public class AccountRequest extends ServiceRequest{


    public void getHomeData(Context context,
                            @NonNull String sessionID,
                            @NonNull final APIServiceCallback<HomeData> callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getHomeData(sessionID).enqueue(new Callback<HomeData>() {
            @Override
            public void onResponse(Call<HomeData> call, Response<HomeData> response) {
                int responseCode = response.code();

                if (responseCode == 200) {

                    HomeData homeData = response.body();
                    callback.onSuccess(homeData);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {

                        if(XiaojsConfig.DEBUG){
                            Logger.d("the get hmoe data response code=%s,but has not error code");
                        }

                        String errorMessage = ErrorPrompts.getHomeDataPrompt(Errors.NO_ERROR);
                        callback.onFailure(Errors.NO_ERROR,errorMessage);

                    } else {
                        String errorCode = ApiManager.parseErrorBody(errorBody);
                        String errorMessage = ErrorPrompts.getHomeDataPrompt(errorCode);
                        callback.onFailure(errorCode,errorMessage);

                    }

                }
            }

            @Override
            public void onFailure(Call<HomeData> call, Throwable t) {

                if(XiaojsConfig.DEBUG){
                    Logger.d("the login has occur exception");
                }

                String errorMessage = ErrorPrompts.getHomeDataPrompt(Errors.NO_ERROR);
                callback.onFailure(Errors.NO_ERROR,errorMessage);

            }
        });

    }


    public void claimCompetency(Context context,
                                String sessionID,
                                CompetencyParams competencyParams,
                                @NonNull final APIServiceCallback<ClaimCompetency> callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.claimCompetency(sessionID,competencyParams).enqueue(new Callback<ClaimCompetency>() {
            @Override
            public void onResponse(Call<ClaimCompetency> call, Response<ClaimCompetency> response) {
                int responseCode = response.code();

                if (responseCode == 200) {

                    ClaimCompetency claimCompetency = response.body();
                    callback.onSuccess(claimCompetency);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {

                        String errorMessage = ErrorPrompts.claimCompetencyPrompt(Errors.NO_ERROR);
                        callback.onFailure(Errors.NO_ERROR,errorMessage);

                    } else {

                        String errorCode = ApiManager.parseErrorBody(errorBody);
                        String errorMessage = ErrorPrompts.claimCompetencyPrompt(errorCode);
                        callback.onFailure(errorCode,errorMessage);

                    }

                }
            }

            @Override
            public void onFailure(Call<ClaimCompetency> call, Throwable t) {

                if(XiaojsConfig.DEBUG){
                    Logger.d("the login has occur exception");
                }

                String errorMessage = ErrorPrompts.claimCompetencyPrompt(Errors.NO_ERROR);
                callback.onFailure(Errors.NO_ERROR,errorMessage);
            }
        });

    }
}