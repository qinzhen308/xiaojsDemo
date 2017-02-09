package cn.xiaojs.xma.data.download;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by maxiaobao on 2017/2/8.
 */

public class RealSystemFacade implements SystemFacade {

    private Context context;

    public RealSystemFacade(Context context) {
        this.context = context;
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public Network getActiveNetwork(int uid, boolean ignoreBlocked) {
        return null;
    }

    @Override
    public NetworkInfo getNetworkInfo(Network network, int uid, boolean ignoreBlocked) {
        return null;
    }

    @Override
    public long getMaxBytesOverMobile() {
        return 0;
    }

    @Override
    public long getRecommendedMaxBytesOverMobile() {
        return 0;
    }

    @Override
    public void sendBroadcast(Intent intent) {

    }
}
