package cn.xiaojs.xma.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.qiniu.pili.droid.streaming.StreamingProfile;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class ClassroomPref {

    private static final String PREF_LIVE_4G = "allow_4g";
    private static final String PREF_LIVING_LEVEL = "living_level";



    public static void setAllowLive4g(final Context context, boolean allow) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putBoolean(PREF_LIVE_4G, allow).apply();

    }

    public static boolean allowLive4G(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getBoolean(PREF_LIVE_4G,false);
    }

    public static void setLivingLevel(final Context context,int level) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        sp.edit().putInt(PREF_LIVING_LEVEL,level).apply();
    }

    public static int getLivingLevel(final Context context) {
        SharedPreferences sp = DataPref.getSharedPreferences(context);
        return sp.getInt(PREF_LIVING_LEVEL, StreamingProfile.VIDEO_QUALITY_MEDIUM2);
    }





}
