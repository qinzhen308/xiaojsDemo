package cn.xiaojs.xma.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class DataPref {

    //private static final String PREF_CONTACT_GROUP = "self_groups";

    private static final String PREF_ALLOW_DOWNLOAD = "download_allow";

    protected static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static void setAllowDownload(final Context context,boolean forbidden) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putBoolean(PREF_ALLOW_DOWNLOAD, forbidden).apply();

    }

    public static boolean allowDownload(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getBoolean(PREF_ALLOW_DOWNLOAD,false);
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
