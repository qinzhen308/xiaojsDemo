package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.AccountRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.HomeData;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class AccountDataManager {

    /**
     * 获取个人主页数据API
     * @param context
     * @param sessionID
     * @param callback
     */
    public static void requestHomeData(Context context,
                                       @NonNull String sessionID,
                                       @NonNull APIServiceCallback<HomeData> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the get home data request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the get home data request return failure");
            }

            String errorMessage = ErrorPrompts.getHomeDataPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.getHomeData(context,sessionID,callback);
    }

    /**
     * 声明教学能力API
     * @param context
     * @param sessionID
     * @param competencyParams
     * @param callback
     */
    public static void requestClaimCompetency(Context context,
                                              @NonNull String sessionID,
                                              @NonNull CompetencyParams competencyParams,
                                              @NonNull APIServiceCallback<ClaimCompetency> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the claim competency request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the claim competency request return failure");
            }

            String errorMessage = ErrorPrompts.claimCompetencyPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        if (competencyParams == null) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the params is null,so the claim competency request return failure");
            }

            String errorMessage = ErrorPrompts.claimCompetencyPrompt(Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }


        AccountRequest accountRequest = new AccountRequest();
        accountRequest.claimCompetency(context,sessionID,competencyParams,callback);
    }


    public static void requestEditProfile(Context context,
                            @NonNull String sessionID,
                            @NonNull Account.Basic basic,
                            @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.editProfilePrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.editProfile(context,sessionID,basic,callback);

    }

    public static void requestProfile(Context context,
                                  @NonNull String sessionID,
                                  @NonNull APIServiceCallback<Account> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.getProfilePrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.getProfile(context,sessionID,callback);

    }




    public static void requestAvatarUpToken(Context context,APIServiceCallback<String> callback) {
        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the claim competency request");
            }
            return;
        }

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.getAvatarUpToken(context,callback);
    }


}
