package cn.xiaojs.xma.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class DataPref {

    protected static SharedPreferences getSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
