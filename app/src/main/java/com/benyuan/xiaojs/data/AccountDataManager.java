package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.AccountRequest;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.QiniuRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.QiniuService;
import com.benyuan.xiaojs.data.preference.AccountPref;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.CenterData;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.HomeData;
import com.benyuan.xiaojs.model.User;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class AccountDataManager extends DataManager{

    //保存已声明的能力
    public static void saveSubject(Context context,String subject) {
        AccountPref.setSubject(context,subject);
    }

    //获取已声明的教学能力subject
    public static String getSubject(Context context) {
        return AccountPref.getSubject(context);
    }

    /**
     * 获取session
     * @param context
     * @return
     */
    public static String getSessionID(Context context) {
        return AccountPref.getAuthToken(context);
    }

    /**
     * 获取账户ID
     * @param context
     * @return
     */
    public static String getAccountID(Context context) {
        return AccountPref.getAccountID(context);
    }

    /**
     * 是否登陆
     * @param context
     * @return true 已登陆 否则false
     */
    public static boolean isLogin(Context context) {

        String sessionID = AccountPref.getAuthToken(context);

        return TextUtils.isEmpty(sessionID) ? false : true ;

    }

    public static void saveUserInfo(Context context,@NonNull User user) {

        if (user == null){

            if (XiaojsConfig.DEBUG) {
                Logger.d("The user is null,so save user failed and return");
            }

            return;
        }

        String phone = user.getSessionID();
        AccountPref.setAuthToken(context,phone);

        String id = user.getId();
        AccountPref.setAccountID(context,id);


    }

    public static void clearUserInfo(Context context) {

        AccountPref.setAuthToken(context,"");
        AccountPref.setAccountID(context,"");

    }


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

        String session = getSessionID(context);
        if (checkSession(session,callback)) {
            return;
        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getHomeData(session);
    }

    /**
     * 声明教学能力API
     * @param context
     * @param
     * @param competencyParams
     * @param callback
     */
    public static void requestClaimCompetency(Context context,
                                              @NonNull CompetencyParams competencyParams,
                                              @NonNull APIServiceCallback<ClaimCompetency> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the claim competency request");
            }
            return;
        }

        String session = getSessionID(context);

        if (checkSession(session,callback)) {
            return;
        }

        if (competencyParams == null) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the params is null,so the claim competency request return failure");
            }

            String errorMessage = ErrorPrompts.getErrorMessage(-1,Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }


        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.claimCompetency(session,competencyParams);
    }


    public static void requestEditProfile(Context context,
                                          @NonNull Account.Basic basic,
                                          @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

        String session = getSessionID(context);

        if (checkSession(session,callback)) {
            return;
        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.editProfile(session,basic);

    }

    public static void requestProfile(Context context,
                                      @NonNull APIServiceCallback<Account.Basic> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }
        String session = getSessionID(context);

        if (checkSession(session,callback)) {
            return;
        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getProfile(session);

    }




//    public static void requestAvatarUpToken(Context context,APIServiceCallback<String> callback) {
//        if (callback == null) {

//    public static void requestAvatorUpToken(Context context,@NonNull String sessionID, @NonNull APIServiceCallback<String> callback) {
//        if (callback == null) {
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the api service callback is null,so cancel the claim competency request");
//            }
//            return;
//        }
//
//        if (TextUtils.isEmpty(sessionID)) {
//
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the sessionID is empty,so the request return failure");
//            }
//
//            String errorMessage = ErrorPrompts.getAvatarUpTokenPrompt(Errors.BAD_SESSION);
//            callback.onFailure(Errors.BAD_SESSION,errorMessage);
//            return;
//        }
//
//        AccountRequest accountRequest = new AccountRequest();
//        accountRequest.getAvatarUpToken(context,sessionID,callback);
//    }


    public static void requestUploadAvatar(Context context,
                                           @NonNull final String filePath,
                                           @NonNull QiniuService qiniuService) {


        if (qiniuService == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            qiniuService.uploadFailure();
            return;
        }

        String session = getSessionID(context);

        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            qiniuService.uploadFailure();
            return;
        }


        QiniuRequest qiniuRequest = new QiniuRequest(context,filePath,qiniuService);
        qiniuRequest.uploadAvatar(session);


    }

    public static void requestCenterData(Context context,
                                  @NonNull APIServiceCallback<CenterData> callback) {
        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

        String session = getSessionID(context);

        if (checkSession(session,callback)) {
            return;
        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getCenterData(session);

    }




}
