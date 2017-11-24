package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.jpush.android.api.TagAliasCallback;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.AccountRequest;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.AliasTags;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.account.AssociationStaus;
import cn.xiaojs.xma.model.account.CompetencySubject;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.account.Location;
import cn.xiaojs.xma.model.account.OrgTeacher;
import cn.xiaojs.xma.model.account.PrivateHome;
import cn.xiaojs.xma.model.account.PublicHome;
import cn.xiaojs.xma.model.account.SocialRegisterInfo;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.model.account.PwdParam;
import cn.xiaojs.xma.model.account.VerifyParam;
import cn.xiaojs.xma.model.account.VerifyStatus;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.util.JpushUtil;
import okhttp3.ResponseBody;

import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class AccountDataManager {


    public static User getUser(Context context) {
        if (XiaojsConfig.mLoginUser == null) {
            XiaojsConfig.mLoginUser = getUserInfo(context);
        }
        return  XiaojsConfig.mLoginUser;
    }

    public static Account getAccont(Context context) {
        if (XiaojsConfig.mLoginUser == null) {
            XiaojsConfig.mLoginUser = getUser(context);
        }

        if (XiaojsConfig.mLoginUser == null) {
            return null;
        }

        return XiaojsConfig.mLoginUser.getAccount();
    }

    public static boolean hasClass(Context context) {
        return true;
        //return getUser(context).hasClass;
    }

    public static void setHasClass(Context context,boolean has) {
        getUser(context).hasClass = has;
        setUserInfo(context, XiaojsConfig.mLoginUser);
    }

    /**
     *
     * @param context
     * @param accountId
     * @return
     */
    public static boolean unFollowable(Context context, String accountId) {

        //FIXME 临时做法
        if (TextUtils.isEmpty(accountId) ||
                accountId.equals(XiaojsConfig.XAIOJS_ACCOUNT_ID) //线上环境
                || accountId.equals(XiaojsConfig.TEST_XAIOJS_ACCOUNT_ID)) {//测试环境
            return true;
        }

        return false;


//        Set<String> ids = AccountPref.getUnfollowables(context);
//        if (ids != null && ids.contains(accountId)) {
//            return true;
//        }
//
//        return false;
    }


    public static boolean isXiaojsAccount(String accountId) {

        if (!TextUtils.isEmpty(accountId)
                && (accountId.equals(XiaojsConfig.XAIOJS_ACCOUNT_ID)
                || accountId.equals(XiaojsConfig.TEST_XAIOJS_ACCOUNT_ID))) {
            return true;
        }

        return false;
    }

    /**
     * 是否已实名认证
     * @param context
     * @return
     */
    public static boolean isVerified(Context context) {

        Account account = getAccont(context);
        return account == null? false : account.isVerified;
    }

    public static void setVerified(Context context,boolean ver) {
        Account account = getAccont(context);

        if (account == null)
            return;


        account.isVerified = ver;
        setUserInfo(context, XiaojsConfig.mLoginUser);
    }

    /**
     * 用户是不是老师
     * @param context
     * @return
     */
    public static boolean isTeacher(Context context) {

        Account account = getAccont(context);


        return account == null? false : account.isTeacher;
    }

    public static void setTeacher(Context context, boolean teacher) {

        Account account = getAccont(context);
        if (account ==null)
            return;

        if (account.isTeacher != teacher) {
            account.isTeacher = teacher;
            setUserInfo(context, XiaojsConfig.mLoginUser);
        }

    }

    public static void setPhone(final Context context, final String phone) {
        AccountPref.setPhone(context, phone);
    }

    public static String getPhone(final Context context) {
        return AccountPref.getPhone(context);
    }

    public static Location getLocation(Context context){
        return AccountPref.getLocation(context);
    }

    public static void setLocation(Context context,Location location){
        AccountPref.setLocation(context, location);
    }

    public static String getAbilities(Context context) {
        return AccountPref.getAbilities(context);
    }

    public static void addAbility(Context context, String ability) {
        AccountPref.addAbility(context,ability,false);
    }

    public static void clearAbilities(Context context) {
        AccountPref.addAbility(context,null,true);
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
        String sfm = SecurityPref.getSFM(context);
        boolean login = AccountPref.getLoginStatus(context);

        if (login && !TextUtils.isEmpty(sessionID) && !TextUtils.isEmpty(sfm)) {
            return true;
        }

        return false;
    }

    public static void saveAliaTags(Context context, AliasTags aliasTags){
        AccountPref.setPrefAliasTags(context,aliasTags);
    }

    public static AliasTags getAliasTags(Context context) {
        return AccountPref.getPrefAliasTags(context);
    }

    public static void setATagsSuccess(Context context, boolean success) {
        AccountPref.setAtagsSuccess(context,success);
    }

    public static boolean isATagsSuccess(Context context) {
        return AccountPref.getAtagsSuccess(context);
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
        saveAliaTags(context,user.getAliasAndTags());

        Account account = user.getAccount();
        if (!TextUtils.isEmpty(user.getName()) && account !=null && account.getBasic() !=null) {
            account.getBasic().setName(user.getName());
            account.name = user.getName();
        }

        setUserInfo(context,user);
    }

    public static void setUserInfo(Context context, User user) {
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
        AccountPref.setThirdPartFlag(context,false);
        AccountPref.setUser(context,null);
        AccountPref.setLocation(context,null);
        AccountDataManager.clearAbilities(context);
        saveAliaTags(context, null);
        AccountPref.setAtagsSuccess(context,false);
        SecurityManager.saveCSRFToken(context,"");


        DataManager.clearAllData(context);
    }


    public static void setAliaTagsWithCheck(Context context) {

        if (isATagsSuccess(context)){
            return;
        }

        submitAliaTags(context);
    }

    public static void submitAliaTags(final Context context) {
        AliasTags aliasTags = AccountDataManager.getAliasTags(context);
        if (aliasTags != null) {
            JpushUtil.setAliasAndTags(context, aliasTags, new TagAliasCallback() {
                @Override
                public void gotResult(int responseCode, String s, Set<String> set) {

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("setAliasAndTags code:" + responseCode);
                    }

                    if (responseCode == 0) {

                        AccountDataManager.setATagsSuccess(context.getApplicationContext(),true);
                        /*if (XiaojsConfig.DEBUG) {
                            Toast.makeText(context, "注册别名成功", Toast.LENGTH_SHORT).show();
                        }*/

                    } else {
                        AccountDataManager.setATagsSuccess(context.getApplicationContext(),false);
                        /*if (XiaojsConfig.DEBUG) {
                            Toast.makeText(context, "注册别名失败", Toast.LENGTH_SHORT).show();
                        }*/
                    }

                }
            });
        }
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
                                              @NonNull APIServiceCallback<CompetencySubject> callback) {

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

    public static void requestEditProfile(Context context,
                                          @NonNull Account account,
                                          @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the  request");
            }
            return;
        }

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.editProfile(account);

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


    /**
     * 上传头像
     * @param context
     * @param filePath
     * @param qiniuService
     */
    public static void requestUploadAvatar(Context context,
                                           @NonNull final String filePath,
                                           @NonNull QiniuService qiniuService) {


        if (qiniuService == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            qiniuService.uploadFailure(false);
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

        QiniuRequest qiniuRequest = new QiniuRequest(context,filePath,qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.AVATAR,1);

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


    /**
     * Returns home data used for populating one's external homepage.
     * @param context
     * @param callback
     */
    public static void getPrivateHome(Context context,
                                      @NonNull APIServiceCallback<PrivateHome> callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getPrivateHome();
    }

    /**
     * Returns home data used for populating one's external homepage.
     * @param context
     * @param account
     * @param callback
     */
    public static void getPublicHome(Context context,
                                     String account,
                                     @NonNull APIServiceCallback<PublicHome> callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getPublicHome(account);
    }

    /**
     * 获取已声明的能力.
     * @param context
     * @param callback
     */
    public static void getCompetencies(Context context, APIServiceCallback<ClaimCompetency> callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getCompetencies();
    }

    public static ClaimCompetency getCompetencies(Context context) throws Exception {
        AccountRequest accountRequest = new AccountRequest(context,null);
        return accountRequest.getCompetenciesSync();

    }

    /**
     * Changes password for the session account.
     * @param context
     * @param password
     * @param callback
     */
    public static void changePassword(Context context, String password, APIServiceCallback callback) {

        PwdParam pwdParam = new PwdParam();
        pwdParam.password = password;

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.changePassword(pwdParam);
    }


    /**
     * Requests to verify identify for an unverified teachers or organization.
     * @param context
     * @param name
     * @param no
     * @param hold
     * @param callback
     */
    public static void requestVerification(Context context,
                                           String name,
                                           String no,
                                           String hold,
                                           APIServiceCallback callback) {

        VerifyParam param = new VerifyParam();
        param.name = name;
        param.handhold = hold;
        param.no = no;

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.requestVerification(param);

    }

    /**
     * 上传认证图片
     * @param context
     * @param filePath
     * @param qiniuService
     */
    public static void uploadHandhold(Context context,
                                      @NonNull final String filePath,
                                      @NonNull QiniuService qiniuService){

        QiniuRequest qiniuRequest = new QiniuRequest(context,filePath,qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.HAND_HOLD,1);
    }


    /**
     * Returns verification status for the session user.
     * @param context
     * @param callback
     */
    public static void getVerificationStatus(Context context, APIServiceCallback<VerifyStatus> callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getVerificationStatus();
    }


    public static void acknowledgeInvitation(Context context, String orgId, DealAck ack, APIServiceCallback callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.acknowledgeInvitation(orgId, ack);
    }

    /**
     * Returns teachers added by an organization.
     * @param context
     * @param account
     * @param recommended
     * @param pagination
     * @param callback
     */
    public static void getOrgTeachers(Context context,
                                      String account,
                                      boolean recommended,
                                      Pagination pagination,
                                      APIServiceCallback<CollectionPage<OrgTeacher>> callback) {

        Criteria criteria = new Criteria();
        criteria.recommended = String.valueOf(recommended);

        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.getOrgTeachers(account,criteria, pagination);

    }

    /**
     *Determines whether the specified external application OpenId is associated with an Xiaojs account.
     *This API can be used to determine whether a 3rd-party account is registered or not.
     * @param context
     * @param ea
     * @param openid
     * @param callback
     */
    public static void checkAssociation(Context context,
                                 String ea,
                                 String openid,
                                 APIServiceCallback<AssociationStaus> callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.checkAssociation(ea, openid);
    }

    /**
     * Associates an account on the Xiaojs platform with the specified external application OpenId.
     * @param context
     * @param ea
     * @param openid
     * @param info
     * @param callback
     */
    public static void socialAssociate(Context context,
                                String ea,
                                String openid,
                                SocialRegisterInfo info,
                                APIServiceCallback callback) {
        AccountRequest accountRequest = new AccountRequest(context,callback);
        accountRequest.socialAssociate(ea, openid,info);
    }

}
