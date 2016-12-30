package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.Account;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.HomeData;
import cn.xiaojs.xma.model.TokenResponse;


import okhttp3.ResponseBody;
import retrofit2.Call;

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

        Account account = new Account();
        account.setBasic(basic);

        Call<ResponseBody> call = getService().editProfile(sessionID, account);
        enqueueRequest(APIType.EDIT_PROFILE,call);

    }

    public void getProfile(@NonNull String sessionID) {

        Call<Account> call = getService().getProfile(sessionID);
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
