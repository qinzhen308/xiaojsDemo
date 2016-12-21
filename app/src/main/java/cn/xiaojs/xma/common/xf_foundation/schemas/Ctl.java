package cn.xiaojs.xma.common.xf_foundation.schemas;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class Ctl {

    //region Objects

    //
    // Defines the lesson source criteria.
    //
    public class LessonSource{
        // All lessons
        public static final String ALL = "All";
        // Created by myself
        public static final String MYSELF = "Myself";
        // Invited by someone else
        public static final String INVITATIONS = "Invitations";
    }

    //
    // Defines the registration types.
    //
    public class EnrollType{

        // No need to enroll first
        public static final int NOT_REQUIRED = 0;
        // Online registration
        public static final int ONLINE = 1;
        // Offline registration
        public static final int OFFLINE = 2;

    }

    //
    // Defines the teaching modes.
    //
    public class TeachMode{
        // Online registration
        public static final int ONLINE = 2;
        // Offline registration
        public static final int OFFLINE = 1;
    }

    //
    // Defines the course.
    //
    public class CourseType {
        // Live course
        public static final int LIVE = 1;
        // Recorded course
        public static final int RECORDED = 2;
    }

    //
    // Defines the teaching mode.
    //
    public class TeachingMode {
        public static final int ONE_2_ONE = 1;
        public static final int ONE_2_MANY = 2;
        public static final int LECTURE = 3;
    }


    ////
    //// Defines the lesson type.
    ////
    //LessonType: {
    //   Live: 1,                                        // Live lesson of course
    //   Standalone: 2,                                  // Standalone live lesson
    //   Recorded: 3                                     // Recorded lesson
    //},

    //
    // Defines the enrollment trends.
    //
    public class EnrollTrend {
        // Not changed
        public static final int STEADY = 0;
        // Increasing
        public static final int ASC = 1;
        // Descreasing
        public static final int DESC = 2;
    }

    //region Schedule

    //
    // Defines the index types.
    //
    public class IndexType {
        public static final int FIRST = 1;
        public static final int SECOND = 2;
        public static final int THIRD = 3;
        public static final int FOURTH = 4;
        public static final int LAST = 5;
    }

    //
    // Defines the week days.
    //
    public class WeekDay {
        public static final int MONDAY = 1;
        public static final int TUESDAY = 2;
        public static final int WEDNESDAY = 3;
        public static final int THURSDAY = 4;
        public static final int FRIDAY = 5;
        public static final int SATURDAY = 6;
        public static final int SUNDAY = 7;
    }


    //region MSP States

    //
    // Defines the standalone lesson lifecycle states, which inherits those common MSP states.
    public class StandaloneLessonState {

        public static final String DRAFT = "Draft";
        // Pending for on-shelves approval
        public static final String PENDING_FOR_APPROVAL = "PendingForApproval";
        // Pending for live session
        public static final String PENDING_FOR_LIVE = "PendingForLive";
        // In class
        public static final String LIVE = "Live";

        public static final String FINISHED = "Finished";

        public static final String REJECTED = "Rejected";

        public static final String CANCELLED = "Cancelled";

        public static final String STOPPED = "Stopped";

    }

}