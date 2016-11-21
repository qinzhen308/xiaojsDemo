package com.benyuan.xiaojs.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.model.User;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/21.
 */

public class AccountPref {

    private static final String PREFIX_PREF_AUTH_TOKEN = "auth_token_";

    private static final String PREF_PHONE = "active_phone";
    private static final String PREF_USER = "active_user";





    private static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static String makeAccountSpecificKey(String phone, String prefix) {
        return prefix + phone;
    }


    public static void setPhone(final Context context, final String phone) {

        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(PREF_PHONE, phone).apply();

    }

    public static String getPhone(final Context context) {

        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(PREF_PHONE,"");
    }


    public static void setAuthToken(final Context context, final String phone, final String authToken) {

        if (XiaojsConfig.DEBUG){
            Logger.d("Auth token of length "
                    + (TextUtils.isEmpty(authToken) ? 0 : authToken.length()) + " for "
                    + phone);
        }

        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(makeAccountSpecificKey(phone, PREFIX_PREF_AUTH_TOKEN),
                authToken).apply();

        if (XiaojsConfig.DEBUG){
            Logger.d("Auth Token: " + authToken);
        }

    }

    public static String getAuthToken(final Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(makeAccountSpecificKey(getPhone(context), PREFIX_PREF_AUTH_TOKEN),"");
    }


}
