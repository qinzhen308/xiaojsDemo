package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.AccountRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
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

            String errorMessage = ErrorPrompts.getHomeDataPrompt(Errors.UNAUTHORIZED);
            callback.onFailure(Errors.UNAUTHORIZED,errorMessage);
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

            String errorMessage = ErrorPrompts.claimCompetencyPrompt(Errors.UNAUTHORIZED);
            callback.onFailure(Errors.UNAUTHORIZED,errorMessage);
            return;
        }

        if (competencyParams == null) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the params is null,so the claim competency request return failure");
            }

            String errorMessage = ErrorPrompts.claimCompetencyPrompt(Errors.NO_ERROR);
            callback.onFailure(Errors.NO_ERROR,errorMessage);
            return;
        }


        AccountRequest accountRequest = new AccountRequest();
        accountRequest.claimCompetency(context,sessionID,competencyParams,callback);
    }
}
