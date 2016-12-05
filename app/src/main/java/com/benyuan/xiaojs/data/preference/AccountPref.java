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

public class AccountPref{

    private static final String PREF_AUTH_TOKEN = "auth_token";

    private static final String PREF_PHONE = "active_phone";
    private static final String PREF_ID = "active_id";
    //private static final String PREF_NAME = "active_name";





//    private static String makeAccountSpecificKey(String phone, String prefix) {
//        return prefix + phone;
//    }

    public static void setAccountID(final Context context,String id) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_ID, id).apply();

    }

    public static String getAccountID(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_ID,"");
    }

    public static void setPhone(final Context context, final String phone) {

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_PHONE, phone).apply();

    }

    public static String getPhone(final Context context) {

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_PHONE,"");
    }


    public static void setAuthToken(final Context context, final String authToken) {

        if (XiaojsConfig.DEBUG){
            Logger.d("Auth token of length "
                    + (TextUtils.isEmpty(authToken) ? 0 : authToken.length()));
        }

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_AUTH_TOKEN, authToken).apply();

        if (XiaojsConfig.DEBUG){
            Logger.d("Auth Token: " + authToken);
        }

    }

    public static String getAuthToken(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_AUTH_TOKEN,"");
    }


}
