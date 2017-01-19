package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Xu;
import cn.xiaojs.xma.data.api.AccountRequest;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.HomeData;
import cn.xiaojs.xma.model.account.UpTokenParam;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.model.social.ContactGroup;
import okhttp3.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class AccountDataManager {

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

    public static void saveSessionID(Context context,String session) {
        AccountPref.setAuthToken(context,session);
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
        boolean login = AccountPref.getLoginStatus(context);

        if (login && !TextUtils.isEmpty(sessionID)) {
            return true;
        }

        return false;
    }

    public static void saveUserInfo(Context context,@NonNull User user) {

        if (user == null){

            if (XiaojsConfig.DEBUG) {
                Logger.d("The user is null,so save user failed and return");
            }

            return;
        }

        //String phone = user.getSessionID();
        //AccountPref.setAuthToken(context,phone);

        String id = user.getId();
        AccountPref.setAccountID(context,id);
        AccountPref.setLoginStatus(context,true);

        AccountPref.setUser(context,user);
    }

    public static User getUserInfo(Context context) {
        return AccountPref.getUser(context);
    }

    /**
     * clear the login user data and cache
     * @param context
     */
    public static void clearUserInfo(Context context) {

        AccountPref.setAuthToken(context,"");
        AccountPref.setAccountID(context,"");
        AccountPref.setLoginStatus(context,false);
        AccountPref.setUser(context,null);
        SecurityManager.saveCSRFToken(context,"");


        DataManager.clearCacheData(context);
    }


    /**
     * 获取个人主页数据API
     * @param context
     * @param callback
     */
    public static void getHomeData(Context context,
                                       @NonNull APIServiceCallback<ResponseBody> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the get home data request");
            }
            return;
        }
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getHomeData();
    }

    public static Map<Long, ContactGroup> getHomeData(Context context) throws Exception{

        AccountRequest accountRequest = new AccountRequest(context,null);
        ResponseBody responseBody = accountRequest.getHomeDataSync();
        if (responseBody != null) {
            return DataManager.parseGroupIntoMap(responseBody.string());
        }

        return null;
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

//        String session = getSessionID(context);
//
//        if (checkSession(session,callback)) {
//            return;
//        }

        if (competencyParams == null) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the params is null,so the claim competency request return failure");
            }

            String errorMessage = ErrorPrompts.getErrorMessage(-1, Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }


        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.claimCompetency(competencyParams);
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


        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.editProfile(basic);

    }

    public static void requestProfile(Context context,
                                      @NonNull APIServiceCallback<Account> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getProfile();

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

//        String session = getSessionID(context);
//
//        if (TextUtils.isEmpty(session)) {
//
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the sessionID is empty,so the request return failure");
//            }
//
//            qiniuService.uploadFailure();
//            return;
//        }

        UpTokenParam param = new UpTokenParam();
        param.type = Xu.TokenType.AVATAR;
        param.quantity = 1;


        QiniuRequest qiniuRequest = new QiniuRequest(context,filePath,qiniuService);
        qiniuRequest.getToken(param);


    }

    public static void requestCenterData(Context context,
                                  @NonNull APIServiceCallback<CenterData> callback) {
        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

//        String session = getSessionID(context);
//
//        if (checkSession(session,callback)) {
//            return;
//        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getCenterData();

    }




}
