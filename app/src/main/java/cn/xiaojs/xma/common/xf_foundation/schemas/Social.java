package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2016/12/26.
 */

public class Social {

    /**
    * Defines the follow types.
    */
    public class FllowType {
        // For relation-analysis only
        public static final int NA = 0;
        // Followed me and is my fan
        public static final int FAN_ONLY = 1;
        // Followed him or her and added to my friend list
        public static final int FOLLOW_SHIP = 2;
        // Mutual-followed and are close friend
        public static final int MUTUAL = 3;
    }

    /**
     * Defines the contact groups.
     */
    public class ContactGroup {

        public static final int TEACHERS = 1;
        public static final int STUDENTS = 2;
        public static final int CLASSMATES = 3;
        public static final int FRIENDS = 4;
        public static final int ORGANIZATIONS = 5;
        public static final int COLLEAGUES = 6;
        public static final int USER_DEFINED = 10;
        public static final int STRANGERS = 99;

    }

    /**
     * Defines the type of share for an activity.
     */
    public class ShareScope {

        public static final int FRIENDS = 1;
        // Not alumni
        public static final int CLASSES = 2;
        // See XADG (Social Overview)
        public static final int PUBLIC = 3;
        // Accessible to myself only
        public static final int PRIVATE = 4;
        public static final int SPECIFIC = 5;
    }

}
