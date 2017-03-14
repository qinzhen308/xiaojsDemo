package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.orhanobut.logger.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.im.ChatActivity;
import cn.xiaojs.xma.model.AliasTags;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.account.User;

/**
 * Created by maxiaobao on 2017/2/24.
 */

public class JpushUtil {

    public static final int STATUS_OK = 0;

    /**
     * 进入聊天界面
     * @param context
     * @param accountID
     */
    public static void launchChat(Context context, String accountID, String name) {

        if (needLogin()){
            APPUtils.exitAndLogin(context, R.string.relogin);
            return;
        }

        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ChatActivity.TARGET_ID, accountID);
        intent.putExtra(ChatActivity.NICKNAME, name);
        intent.putExtra(ChatActivity.TARGET_APP_KEY, XiaojsConfig.JPUSH_APP_KEY);
        context.startActivity(intent);
    }

    /**
     * 是否需要登陆
     * @return
     */
    public static boolean needLogin() {

        UserInfo myInfo = JMessageClient.getMyInfo();
        return myInfo == null? true : false;
    }

    public static void register(final User user) {

        if (user == null) return;

        final String aid = user.getId();
        JMessageClient.register(aid, aid, new BasicCallback() {
            @Override
            public void gotResult(int status, String s) {
                if (status == STATUS_OK) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Jpush注册成功:" + status);
                    }

                    //登陆
                    JMessageClient.login(aid, aid, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String s) {
                            if (status == STATUS_OK) {
                                if (XiaojsConfig.DEBUG) {
                                    Logger.d("Jpush注册后--登录成功" + status);
                                }

                                //更新用户昵称
                                updateNickName(user.getName());

                            } else {
                                if (XiaojsConfig.DEBUG) {
                                    Logger.d("Jpush注册后--登录失败" + status);
                                }
                            }
                        }
                    });

                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Jpush注册失败:" + status);
                    }

                    //注册失败后，重试
                    if (isRetry(status)) {
                        register(user);
                    }
                }
            }
        });
    }


    /**
     * 登陆JPUSH
     */
    public static void loginJpush(final String userName, final String password) {
        JMessageClient.login(userName, password, new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == STATUS_OK) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Jpush登录成功" + status);
                    }

                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Jpush登录失败" + status);
                    }

                    //登陆失败，重试登陆
                    if (isRetry(status)) {
                        loginJpush(userName, password);
                    }

                }
            }
        });
    }

    /**
     * 注销Jpush
     */
    public static void logoutJpush() {
        JMessageClient.logout();
    }


    /**
     * 设置别名和标签
     * @param context
     */
    public static void setAliasAndTags(final Context context, AliasTags aliasTags, TagAliasCallback callback) {

        if (aliasTags == null) return;

        List<String> ptags = aliasTags.getTags();
        Set<String> tags = new HashSet<>();
        if (ptags != null) {
            tags.addAll(ptags);
        }

        JPushInterface.setAliasAndTags(context, aliasTags.getAlias(), tags, callback);
    }

    /**
     * 更新用户信息
     * @param basic
     */
    public static void updateUserInfo(Account.Basic basic) {

        if (basic == null) return;

        JpushUtil.updateNickName(basic.getName());
        JpushUtil.updateSignature(basic.getTitle());
        JpushUtil.updateGender(basic.getSex());
    }

    /**
     * 更新用户昵称
     * @param nickName
     */
    public static void updateNickName(String nickName) {

        UserInfo userInfo = JMessageClient.getMyInfo();
        if (TextUtils.isEmpty(nickName) || userInfo == null) return;

        String oldNick = userInfo.getNickname();
        if (TextUtils.isEmpty(oldNick) || !oldNick.equals(nickName)){
            userInfo.setNickname(nickName);

            updateUserInfo(UserInfo.Field.nickname, userInfo);
        }

    }

    /**
     * 更新用户签名
     * @param signature
     */
    public static void updateSignature(String signature) {

        UserInfo userInfo = JMessageClient.getMyInfo();
        if (TextUtils.isEmpty(signature) || userInfo == null) return;

        String oldsign = userInfo.getSignature();
        if (TextUtils.isEmpty(oldsign) || !oldsign.equals(signature)){
            userInfo.setSignature(signature);

            updateUserInfo(UserInfo.Field.signature, userInfo);
        }

    }

    /**
     * 更新用户性别
     * @param sex
     */
    public static void updateGender(String sex) {

        UserInfo userInfo = JMessageClient.getMyInfo();
        if (userInfo == null) return;

        UserInfo.Gender gender = TextUtils.isEmpty(sex) ? UserInfo.Gender.unknown :
                ("true".equals(sex) ? UserInfo.Gender.male : UserInfo.Gender.female);


        UserInfo.Gender oldGender = userInfo.getGender();
        if (oldGender==null || !oldGender.equals(gender)){
            userInfo.setGender(gender);

            updateUserInfo(UserInfo.Field.gender, userInfo);
        }

    }

    private static void updateUserInfo(UserInfo.Field field, UserInfo info) {

        JMessageClient.updateMyInfo(field, info, new BasicCallback() {
            @Override
            public void gotResult(int status, String s) {
                if (status == STATUS_OK) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Jpush 用户信息更新成功："+status);
                    }

                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Jpush 用户信息更新失败："+status);
                    }
                }
            }
        });
    }

    public static String getGenderTitle(UserInfo.Gender gender){
        if (gender == null || gender == UserInfo.Gender.unknown)
            return "Ta";
        if (gender == UserInfo.Gender.male){
            return "他";
        }else if (gender == UserInfo.Gender.female){
            return "她";
        }else {
            return "Ta";
        }
    }

    /**
     * 根据状态码，是否决定重试操作
     * @param status
     * @return
     */
    public static boolean isRetry(int status) {

        switch(status){
            case 898030:
            case 898000:
            case 899000:
            case 899030:
            case 800009:
            case 800012:
            case 800013:
            case 871504:
            case 871300:
            case 871102:
            case 871201://响应超时
            case 871103:
            case 871104:
                return true;
        }

        return false;
    }
}
