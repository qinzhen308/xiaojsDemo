package cn.xiaojs.xma.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.model.account.User;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class DataPref {

    //private static final String PREF_CONTACT_GROUP = "self_groups";

    private static final String PREF_VCODE = "vcode";
    private static final String PREF_ALLOW_DOWNLOAD = "download_allow";
    private static final String PREF_UPGRADE = "upgrade";
    private static final String PREF_SERVER_IP = "sip";
    private static final String PREF_XAS_PORT = "xas_port";
    private static final String PREF_XLS_PORT = "xls_port";

    protected static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setXLSPort(final Context context,String port) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_XLS_PORT, port).apply();

    }

    public static String getXLSPort(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_XLS_PORT, XiaojsConfig.LIVE_PORT);
    }


    public static void setXASPort(final Context context,String port) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_XAS_PORT, port).apply();

    }

    public static String getXASPort(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_XAS_PORT, XiaojsConfig.SERVICE_PORT);
    }

    public static void setServerIP(final Context context,String ip) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_SERVER_IP, ip).apply();

    }

    public static String getServerIP(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_SERVER_IP, XiaojsConfig.BASE_URL);
    }


    public static void setVersionCode(final Context context,int vcode) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putInt(PREF_VCODE, vcode).apply();

    }

    public static int getVersionCode(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getInt(PREF_VCODE,-1);
    }


    public static void setAllowDownload(final Context context,boolean forbidden) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putBoolean(PREF_ALLOW_DOWNLOAD, forbidden).apply();

    }

    public static boolean allowDownload(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getBoolean(PREF_ALLOW_DOWNLOAD,true);
    }

    public static void setUpgrade(final Context context, Upgrade upgrade) {

        String userJson = "";
        try {
            userJson =  new ObjectMapper().writeValueAsString(upgrade);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_UPGRADE, userJson).apply();

    }

    public static Upgrade getUpgrade(final Context context) {

        Upgrade upgrade = null;

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        String userJson = sp.getString(PREF_UPGRADE,"");
        if (!TextUtils.isEmpty(userJson)){
            try {
                upgrade = new ObjectMapper().readValue(userJson,Upgrade.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return upgrade;
    }

//    public static void setContactGroup(final Context context,String group) {
//
//        Set<String> groups = getContactGroup(context);
//        if (groups ==null) {
//            groups = new HashSet<>();
//        }
//
//        groups.add(group);
//
//        SharedPreferences sp = DataPref.getSharedPreferences(context);
//        sp.edit().putStringSet(PREF_CONTACT_GROUP,groups).apply();
//    }
//
//    public static Set<String> getContactGroup(final Context context) {
//        SharedPreferences sp = DataPref.getSharedPreferences(context);
//        return sp.getStringSet(PREF_CONTACT_GROUP,null);
//    }

}
