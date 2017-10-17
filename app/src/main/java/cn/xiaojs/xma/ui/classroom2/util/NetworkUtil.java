package cn.xiaojs.xma.ui.classroom2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;

/**
 * Created by maxiaobao on 2017/10/17.
 */

public class NetworkUtil {

    public static int getCurrentNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo currentInfo = cm.getActiveNetworkInfo();
        if (currentInfo == null || !currentInfo.isConnected()) {
            return CTLConstant.NetworkType.TYPE_NONE;
        } else if (currentInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return CTLConstant.NetworkType.TYPE_WIFI;
        } else {
            return CTLConstant.NetworkType.TYPE_MOBILE;
        }
    }

    public static boolean isWIFI(Context context) {
        return getCurrentNetwork(context) == CTLConstant.NetworkType.TYPE_WIFI? true : false;
    }
}
