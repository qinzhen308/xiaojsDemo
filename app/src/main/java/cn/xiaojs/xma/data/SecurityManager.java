package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.SecurityRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.Privilege;
import cn.xiaojs.xma.model.security.AuthenticateStatus;
import cn.xiaojs.xma.model.security.ResetPwdParam;

import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class SecurityManager {


    public static boolean checkPermission(Context context,int permission) {

        return SecurityPref.getPermission(context,permission);

    }

    public static void updatePermission(Context context,int permission,boolean granted){
        SecurityPref.setPermission(context,permission,granted);
    }

    public static void savePermission(Context context,Privilege[] privileges) {

        if (privileges == null || privileges.length==0){
            return;
        }

        for (Privilege p: privileges) {
            boolean granted = p.getAuth().isGranted();
            SecurityPref.setPermission(context,p.getPermission(),granted);

        }
    }

    /**
     * 是否需要 check session
     * @param context
     * @return
     */
    public static boolean needCheckSession(Context context) {

        String session = AccountPref.getAuthToken(context);
        String token = getCSRFToken(context);
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(session)) {
            return true;
        }

        return false;

    }

    /**
     * save x-csrf-token
     * @param context
     * @param csrf_token
     */
    public static void saveCSRFToken(Context context, String csrf_token) {
        SecurityPref.setCSRFToken(context,csrf_token);
    }

    /**
     * get x-csrf-token
     * @param context
     * @return
     */
    public static String getCSRFToken(Context context) {
        return SecurityPref.getCSRFToken(context);
    }

    public static void requestHavePrivilege(@NonNull Context context,
                                            @NonNull APIServiceCallback<Privilege[]> callback,
                                            int... permissions) {

        int pcount = permissions.length;
        if (pcount <= 0) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("permissions length is 0,so cancel the request");
            }
            return;
        }

        Privilege[] privileges = new Privilege[pcount];
        for (int i=0;i<pcount ; i++) {

            Privilege p = new Privilege();
            p.setPermission(permissions[i]);

            privileges[i] = p;
        }

        requestHavePrivilege(context,privileges,callback);

    }


    public static Privilege[] havePrivilegeSync(@NonNull Context context,
                                                int... permissions) throws Exception{

        int pcount = permissions.length;
        if (pcount <= 0) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("permissions length is 0,so cancel the request");
            }
            return null;
        }

        Privilege[] privileges = new Privilege[pcount];
        for (int i=0;i<pcount ; i++) {

            Privilege p = new Privilege();
            p.setPermission(permissions[i]);

            privileges[i] = p;
        }

        return havePrivilegeSync(context,privileges);

    }

    public static Privilege[] havePrivilegeSync(@NonNull Context context,
                                            @NonNull Privilege[] privileges) throws Exception{


        SecurityRequest securityRequest = new SecurityRequest(context,null);
        return securityRequest.havePrivilegesSync(privileges);

    }


    public static void requestHavePrivilege(@NonNull Context context,
                                            @NonNull Privilege[] privileges,
                                            @NonNull APIServiceCallback<Privilege[]> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }


        SecurityRequest securityRequest = new SecurityRequest(context,callback);
        securityRequest.havePrivileges(privileges);

    }

    /**
     * Returns authentication status for the current session.
     * This API can be used to determine whether the current session is authenticated or not.
     * @param context
     * @param callback
     */
    public static void checkSession(@NonNull Context context,
                                    APIServiceCallback<AuthenticateStatus> callback) {

        SecurityRequest securityRequest = new SecurityRequest(context,callback);
        securityRequest.checkSession();
    }

    /**
     * Provides a means to reset password for the specific account.
     * @param context
     * @param mobile
     * @param password
     * @param code
     * @param callback
     */
    public static void resetPassword(Context context,
                                     long mobile,
                                     String password,
                                     int code,
                                     APIServiceCallback callback) {

        ResetPwdParam param = new ResetPwdParam();
        param.mobile = mobile;
        param.password = password;
        param.code = code;

        SecurityRequest securityRequest = new SecurityRequest(context,callback);
        securityRequest.resetPassword(param);

    }

}
