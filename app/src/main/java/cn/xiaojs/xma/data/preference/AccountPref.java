package cn.xiaojs.xma.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.model.AliasTags;
import cn.xiaojs.xma.model.account.Location;
import cn.xiaojs.xma.model.account.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.io.IOException;

/**
 * Created by maxiaobao on 2016/11/21.
 */

public class AccountPref {

    private static final String PREF_AUTH_TOKEN = "auth_token";

    private static final String PREF_PHONE = "active_phone";
    private static final String PREF_ID = "active_id";
    private static final String PREF_LOGIN = "auth_login";

    private static final String PREF_SUBJECT = "acc_subject";
    private static final String PREF_USER = "active_u";
    //private static final String PREF_NAME = "active_name";

    //
    private static final String PREF_ALIAS_TAGS = "alias_tags";
    private static final String PREF_ATAGS = "atgs";

    private static final String PREF_LOCATION = "location";

    private static final String PREF_ABILITY = "ability";


//    private static String makeAccountSpecificKey(String phone, String prefix) {
//        return prefix + phone;
//    }


    public static void addAbility(final Context context, String ability, boolean clear) {

        if (clear) {
            SharedPreferences sp = DataPref.getSharedPreferences(context);
            sp.edit().putString(PREF_ABILITY, "").apply();
            return;
        }


        if (TextUtils.isEmpty(ability)){
            return;
        }

        String oldAbilities = getAbilities(context);
        StringBuilder sb = new StringBuilder(oldAbilities);
        if (!TextUtils.isEmpty(oldAbilities)) {
            sb.append("、");
        }
        sb.append(ability);

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_ABILITY, sb.toString()).apply();

    }

    public static String getAbilities(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_ABILITY, "");
    }


    public static void setLocation(final Context context, Location location) {

        String userJson = "";
        try {
            userJson = new ObjectMapper().writeValueAsString(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_LOCATION, userJson).apply();

    }

    public static Location getLocation(final Context context) {

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        String locationJson = sp.getString(PREF_LOCATION, "");

        if (TextUtils.isEmpty(locationJson)) return null;

        try {
            return new ObjectMapper().readValue(locationJson, Location.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void setAtagsSuccess(final Context context, boolean success) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putBoolean(PREF_ATAGS, success).apply();

    }

    public static boolean getAtagsSuccess(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getBoolean(PREF_ATAGS, false);
    }

    public static void setPrefAliasTags(final Context context, AliasTags aliasTags) {

        String userJson = "";
        try {
            userJson = new ObjectMapper().writeValueAsString(aliasTags);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_ALIAS_TAGS, userJson).apply();

    }

    public static AliasTags getPrefAliasTags(final Context context) {

        AliasTags aliasTags = null;

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        String userJson = sp.getString(PREF_ALIAS_TAGS, "");
        if (!TextUtils.isEmpty(userJson)) {
            try {
                aliasTags = new ObjectMapper().readValue(userJson, AliasTags.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return aliasTags;
    }


    public static void setLoginStatus(final Context context, boolean status) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putBoolean(PREF_LOGIN, status).apply();

    }

    public static boolean getLoginStatus(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getBoolean(PREF_LOGIN, false);
    }

    public static void setUser(final Context context, User user) {

        String userJson = "";
        try {
            userJson = new ObjectMapper().writeValueAsString(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_USER, userJson).apply();

    }

    public static User getUser(final Context context) {

        User user = null;

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        String userJson = sp.getString(PREF_USER, "");
        if (!TextUtils.isEmpty(userJson)) {
            try {
                user = new ObjectMapper().readValue(userJson, User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return user;
    }


    public static void setSubject(final Context context, String subject) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_SUBJECT, subject).apply();

    }

    public static String getSubject(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_SUBJECT, "");
    }

    public static void setAccountID(final Context context, String id) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_ID, id).apply();

    }

    public static String getAccountID(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_ID, "");
    }

    public static void setPhone(final Context context, final String phone) {

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_PHONE, phone).apply();

    }

    public static String getPhone(final Context context) {

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_PHONE, "");
    }


    public static void setAuthToken(final Context context, final String authToken) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("Auth token of length "
                    + (TextUtils.isEmpty(authToken) ? 0 : authToken.length()));
        }

        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putString(PREF_AUTH_TOKEN, authToken).apply();

        if (XiaojsConfig.DEBUG) {
            Logger.d("Auth Token: " + authToken);
        }

    }

    public static String getAuthToken(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getString(PREF_AUTH_TOKEN, "");
    }


}
