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

        Call<HomeData> call = getService().getHomeData(sessionID);
        enqueueRequest(APIType.GET_HOME_DATA,call);

    }

    public void claimCompetency(String sessionID, CompetencyParams competencyParams) {

        Call<ClaimCompetency> call = getService().claimCompetency(sessionID, competencyParams);
        enqueueRequest(APIType.CLAIM_COMPETENCY,call);

    }

    public void editProfile(@NonNull String sessionID, @NonNull Account.Basic basic) {

        Call<ResponseBody> call = getService().editProfile(sessionID, basic);
        enqueueRequest(APIType.EDIT_PROFILE,call);

    }

    public void getProfile(@NonNull String sessionID) {

        Call<Account.Basic> call = getService().getProfile(sessionID);
        enqueueRequest(APIType.GET_PROFILE,call);

    }


    protected void getAvatarUpToken(@NonNull String sessionID) {

        Call<TokenResponse> call = getService().getAvatarUpToken(sessionID);
        enqueueRequest(APIType.GET_UPTOKEN,call);

    }


    protected void getCoverUpToken(@NonNull String sessionID, @NonNull String lesson) {

        Call<TokenResponse> call = getService().getCoverUpToken(sessionID,lesson);
        enqueueRequest(APIType.GET_LESSON_COVER_UPTOKEN,call);

    }

    public void getCenterData(@NonNull String sessionID) {

        Call<CenterData> call = getService().getCenterData(sessionID);
        enqueueRequest(APIType.GET_CENTER_DATA,call);

    }

}
