package com.benyuan.xiaojs.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2016/10/29.
 */

public class Platform {

    /**
     * Defines the client app types.
     */
    public static class AppType {
        public static final int MOBILE_WEB = 2;
        public static final int MOBILE_ANDROID = 3;
        public static final int TABLET_ANDROID = 6;
        public static final int UNKNOWN = 10;
    }

    /**
     * Defines the mobile app new version available actions.
     */
    public static class AvailableAction{

        // Ignore this version
        private static final int IGNORE = 1;

        // Show available tips
        private static final int TIPS = 2;

        // Require upgrade
        private static  final int UPGRADE = 3;

    }

}
