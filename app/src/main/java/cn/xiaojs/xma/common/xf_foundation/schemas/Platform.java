package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2016/10/29.
 */

public class Platform {


    //
    // Defines the general student join privateClass lifecycle states, which inherits those common MSP states.
    //
    public static class JoinClassState {
        public static final String PENDING_FOR_ACCEPTANCE = "PendingForAcceptance";
        public static final String ACCEPTED = "Accepted";
        public static final String REJECTTED = "Rejected";
        public static final String NONE = "none";
        public static final String FAILED = "Failed";
        public static final String DELETED = "Deleted";
    }

    //
    // Defines the general verification lifecycle states, which inherits those common MSP states.
    //
    public static class VerificationState {
        public static final String DRAFT = "Draft";
        public static final String PENDING_FOR_REVIEW = "PendingForReview";
        public static final String VERIFIED = "Verified";
        public static final String DENIED = "Denied";
        public static final String NONE = "none";
        public static final String FAILED = "Failed";
        public static final String DELETED = "Deleted";
    }


    /**
     * Defines the period types.
     */
    public static class PeriodType {
        public static final int NA = 0;
        public static final int DAILY = 1;
        public static final int WEEKLY = 2;
        public static final int MONTHLY = 3;
        public static final int THREE_MONTHLY = 4;
        public static final int YEARLY = 5;
    }


    /**
     * Defines the client app types.
     */
    public static class AppType {
        public static final int MOBILE_WEB = 2;
        public static final int MOBILE_ANDROID = 3;
        public static final int MOBILE_IOS = 4;
        public static final int MOBILE_WP = 5;
        public static final int TABLET_ANDROID = 6;
        public static final int TABLET_IOS = 7;
        public static final int UNKNOWN = 10;
        public static final int WEB_CLASSROOM = 11;
    }

    /**
     * Defines the mobile app new version available actions.
     */
    public static class AvailableAction {

        // Ignore this version
        public static final int IGNORE = 1;

        // Show available tips
        public static final int TIPS = 2;

        // Require upgrade
        public static final int UPGRADE = 3;

    }

    //
    // Defines the general notification states, which inherits those common MSP states.
    //
    public static class NotificationState {

        public static final String DRAFT = "Draft";
        // Pending for operation
        public static final String DISPATCHED = "Dispatched";

        // Hide notification but does not mean operation is not necessary
        public static final String DISMISSED = "Dismissed";

        // Operation done or operation is not necessary
        public static final String ACK_NOW_LEDGED = "Acknowledged";
        public static final String OBSOLETE = "Obsolete";
        public static final String REMOVED = "Removed";

        // ---------------------------------------------// DO NOT INHERIT FROM FROM MSPState

        public static final String NONE = "none";
        public static final String FAILED = "Failed";
        public static final String DELETED = "Deleted";
    }


    //
    // Defines the external 3rd-party app types.
    //

    public static class ExternalApp {
        public static final int WECHAT = 1;
        public static final int QQ = 2;
    }

}
