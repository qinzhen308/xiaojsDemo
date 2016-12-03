package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.CenterData;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.HomeData;
import com.benyuan.xiaojs.model.TokenResponse;
import com.orhanobut.logger.Logger;


import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/3.
 */

public class AccountRequest extends ServiceRequest {


    public AccountRequest(Context context,APIServiceCallback callback) {

        super(context,callback);

    }

    public void getHomeData(@NonNull String sessionID) {

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getHomeData(sessionID).enqueue(new Callback<HomeData>() {
            @Override
            public void onResponse(Call<HomeData> call, Response<HomeData> response) {
                onRespones(APIType.GET_HOME_DATA,response);
            }

            @Override
            public void onFailure(Call<HomeData> call, Throwable t) {
               onFailures(APIType.GET_HOME_DATA,t);
            }
        });

    }


    public void claimCompetency(String sessionID, CompetencyParams competencyParams) {

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.claimCompetency(sessionID, competencyParams).enqueue(
                new Callback<ClaimCompetency>() {
            @Override
            public void onResponse(Call<ClaimCompetency> call, Response<ClaimCompetency> response) {

                onRespones(APIType.CLAIM_COMPETENCY,response);
            }

            @Override
            public void onFailure(Call<ClaimCompetency> call, Throwable t) {

                onFailures(APIType.CLAIM_COMPETENCY,t);

            }
        });

    }

    public void editProfile(@NonNull String sessionID, @NonNull Account.Basic basic) {



        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.editProfile(sessionID, basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.EDIT_PROFILE,response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.EDIT_PROFILE,t);

            }
        });


    }

    public void getProfile(@NonNull String sessionID) {

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getProfile(sessionID).enqueue(new Callback<Account.Basic>() {
            @Override
            public void onResponse(Call<Account.Basic> call, Response<Account.Basic> response) {

                onRespones(APIType.GET_PROFILE,response);
            }

            @Override
            public void onFailure(Call<Account.Basic> call, Throwable t) {

                onFailures(APIType.GET_PROFILE,t);
            }
        });


    }


    protected void getAvatarUpToken(@NonNull String sessionID) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getAvatarUpToken(sessionID).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {

                onRespones(APIType.GET_UPTOKEN,response);
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

               onFailures(APIType.GET_UPTOKEN,t);
            }
        });
    }


    protected void getCoverUpToken(@NonNull String sessionID, @NonNull String lesson) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getCoverUpToken(sessionID,lesson).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {

                onRespones(APIType.GET_LESSON_COVER_UPTOKEN,response);

            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

                onFailures(APIType.GET_LESSON_COVER_UPTOKEN,t);
            }
        });
    }

    public void getCenterData(@NonNull String sessionID) {

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getCenterData(sessionID).enqueue(new Callback<CenterData>() {
            @Override
            public void onResponse(Call<CenterData> call, Response<CenterData> response) {

                onRespones(APIType.GET_CENTER_DATA,response);

            }

            @Override
            public void onFailure(Call<CenterData> call, Throwable t) {

                onFailures(APIType.GET_CENTER_DATA,t);
            }
        });


    }

}
