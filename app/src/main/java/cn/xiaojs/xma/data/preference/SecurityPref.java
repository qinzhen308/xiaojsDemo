package cn.xiaojs.xma.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class SecurityPref {

    private static final String PREF_PERMISSION_PREFIX = "permission_";
    private static final String PREF_CSRF_TOKEN = "csrf_token";
    private static final String PREF_CSRF_COOKIE = "csrf_cookie";



    private static String permissionKey(int permission) {

        StringBuilder key = new StringBuilder(PREF_PERMISSION_PREFIX);
        key.append(permission);

        return key.toString();

    }

    public static void setPermission(final Context context, int permission,boolean granted) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putBoolean(permissionKey(permission), granted).apply();

    }

    public static boolean getPermission(final Context context,int permission) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getBoolean(permissionKey(permission),false);
    }

    public static void setCSRFToken(final Context context,String csrf_token) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_CSRF_TOKEN,csrf_token).apply();
    }

    public static String getCSRFToken(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_CSRF_TOKEN,"");
    }


    public static void setCSRFCookie(final Context context,String csrf_cookie) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_CSRF_COOKIE,csrf_cookie).apply();
    }

    public static String getCSRFCookie(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_CSRF_COOKIE,"");
    }





}
