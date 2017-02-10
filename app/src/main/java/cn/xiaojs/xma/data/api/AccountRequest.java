package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CompetencyParams;


import cn.xiaojs.xma.model.account.PrivateHome;
import cn.xiaojs.xma.model.account.PublicHome;
import cn.xiaojs.xma.model.account.UpToken;
import cn.xiaojs.xma.model.account.UpTokenParam;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static cn.xiaojs.xma.data.api.service.APIType.GET_UPTOKEN;

/**
 * Created by maxiaobao on 2016/11/3.
 */

public class AccountRequest extends ServiceRequest {


    public AccountRequest(Context context,APIServiceCallback callback) {

        super(context,callback);

    }

    public void getHomeData() {

        Call<ResponseBody> call = getService().getHomeData();
        enqueueRequest(APIType.GET_HOME_DATA,call);

    }

    public ResponseBody getHomeDataSync() throws IOException{

        Response<ResponseBody> response = getService().getHomeData().execute();
        if (response != null) {
            return response.body();
        }
        return null;
    }

    public void claimCompetency(CompetencyParams competencyParams) {

        Call<ClaimCompetency> call = getService().claimCompetency(competencyParams);
        enqueueRequest(APIType.CLAIM_COMPETENCY,call);

    }

    public void editProfile(@NonNull Account.Basic basic) {

        Account account = new Account();
        account.setBasic(basic);

        Call<ResponseBody> call = getService().editProfile(account);
        enqueueRequest(APIType.EDIT_PROFILE,call);

    }

    public void getProfile() {

        Call<Account> call = getService().getProfile();
        enqueueRequest(APIType.GET_PROFILE,call);

    }


//    protected void getAvatarUpToken(@NonNull String sessionID) {
//
//        Call<TokenResponse> call = getService().getAvatarUpToken(sessionID);
//        enqueueRequest(APIType.GET_UPTOKEN,call);
//
//    }
//
//
//    protected void getCoverUpToken(@NonNull String sessionID, @NonNull String lesson) {
//
//        Call<TokenResponse> call = getService().getCoverUpToken(sessionID,lesson);
//        enqueueRequest(APIType.GET_LESSON_COVER_UPTOKEN,call);
//
//    }

//    protected void getUpToken(UpTokenParam... upToken) {
//        Call<UpToken[]> call = getService().getUpToken(upToken);
//        enqueueRequest(GET_UPTOKEN,call);
//    }

    public void getCenterData() {

        Call<CenterData> call = getService().getCenterData();
        enqueueRequest(APIType.GET_CENTER_DATA,call);

    }

    public void getPrivateHome() {
        Call<PrivateHome> call = getService().getPrivateHome();
        enqueueRequest(APIType.GET_PRIVATE_HOME,call);
    }

    public void getPublicHome(String account) {
        Call<PublicHome> call = getService().getPublicHome(account);
        enqueueRequest(APIType.GET_PUBLIC_HOME,call);
    }

}
