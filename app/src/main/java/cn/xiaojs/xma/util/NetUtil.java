package cn.xiaojs.xma.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by maxiaobao on 2017/3/16.
 */

public class NetUtil {

    public static final int NETWORK_NONE = 1;
    public static final int NETWORK_WIFI = 2;
    public static final int NETWORK_OTHER = 3;

    public static int getCurrentNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo currentInfo = cm.getActiveNetworkInfo();
        if (currentInfo == null || !currentInfo.isConnected()) {
            return NETWORK_NONE;
        } else if (currentInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI;
        } else {
            return NETWORK_OTHER;
        }
    }
}
