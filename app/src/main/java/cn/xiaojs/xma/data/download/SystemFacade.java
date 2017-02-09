package cn.xiaojs.xma.data.download;

import android.content.Intent;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by maxiaobao on 2017/2/8.
 */

public interface SystemFacade {
    /**
     * @see System#currentTimeMillis()
     */
    public long currentTimeMillis();

    public Network getActiveNetwork(int uid, boolean ignoreBlocked);

    public NetworkInfo getNetworkInfo(Network network, int uid, boolean ignoreBlocked);

    /**
     * @return maximum size, in bytes, of downloads that may go over a mobile connection; or null if
     * there's no limit
     */
    public long getMaxBytesOverMobile();

    /**
     * @return recommended maximum size, in bytes, of downloads that may go over a mobile
     * connection; or null if there's no recommended limit.  The user will have the option to bypass
     * this limit.
     */
    public long getRecommendedMaxBytesOverMobile();

    /**
     * Send a broadcast intent.
     */
    public void sendBroadcast(Intent intent);

    /**
     * Returns true if the specified UID owns the specified package name.
     */
    //public boolean userOwnsPackage(int uid, String pckg) throws NameNotFoundException;

    /**
     * Returns true if cleartext network traffic is permitted for the specified UID.
     */
    //public boolean isCleartextTrafficPermitted(int uid);

    /**
     * Return a {@link SSLContext} configured using the specified package's configuration.
     */
//    public SSLContext getSSLContextForPackage(Context context, String pckg)
//            throws GeneralSecurityException;
}
