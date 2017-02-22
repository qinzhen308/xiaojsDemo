package cn.xiaojs.xma.common.xf_foundation.schemas;


import cn.xiaojs.xma.common.xf_foundation.Constants;

/**
 * Created by maxiaobao on 2016/12/26.
 */

public class Social {

    public class GroupSetType {
        public static final String CONTACTS = "contacts";
        public static final String CLASSES = "classes";
        public static final String RECENT = "recent";
    }

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

        //班级不属于默认分组
        public static final int CLASSES = -1;

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

    /**
     * Defines the type of relationship between two persons on the Xiaojs platform.
     * @readonly
     * @enum {Number}
     */
    public class RelationType {
        // For people followed by somebody or followed each other, it's 1st degree relationship
        public static final String FRIENDS = "A";
        // For teachers (both lead and assistants), classmates or students within specific classes, it's 2nd degree relationship
        public static final String CLASSES = "B";
        // For those you've not followed and also do not participate in any class they've involved
        public static final String STRANGERS = "C";
    }


    /**
     * Defines the in-class relationship between two participants.
     */
    public class InClassRelation {
        public static final int LEAD_TEACHER = 1;
        public static final int ASSISTANT_TEACHER = 2;
        public static final int CLASSMATE = 3;
        public static final int STUDENT = 4;
    }

    /**
     *
     * Returns the user-friendly name for the specified contact group.
     * @param groupId
     * @return
     */
    public static String getContactName(int groupId) {
        switch (groupId)
        {
            case ContactGroup.TEACHERS:
                return "老师";

            case ContactGroup.STUDENTS:
                return "学生";

            case ContactGroup.CLASSMATES:
                return "同学";

            case ContactGroup.FRIENDS:
                return "好友";

            case ContactGroup.ORGANIZATIONS:
                return "机构";

            case ContactGroup.COLLEAGUES:
                return "同事";

            case ContactGroup.STRANGERS:
                return "陌生人";

            case ContactGroup.CLASSES://班级不属于默认分组
                return "班级";

            default:
                return "n/a";
        }
    }

    /**
     * Returns the user-friendly name or tag for the specified in-class relationship.
     * @param relation
     * @return
     */
    public static String getInClassRelationTag(int relation) {

        switch (relation)
        {
            case InClassRelation.LEAD_TEACHER:
                return "主讲";

            case InClassRelation.ASSISTANT_TEACHER:
                return "助教";

            case InClassRelation.CLASSMATE:
                return "同学";

            case InClassRelation.STUDENT:
                return "学生";

            default:
                return "n/a";
        }

    }

    /**
     * Defines support activity types.
     */
    public class ActivityType {
        //个人动态
        public static final String POST_ACTIIVTY = "PostActivity";
        //直播课公开（选项）分享动态
        public static final String LESSON_ACCESSIBLE_PREFERRED_ACTIIVTY = "LessonAccessiblePreferredActivity";
        //报名（成功）分享动态
        public static final String ENROLLMENT_SHARE_ACTIIVTY = "EnrollmentShareActivity";
        //转发动态
        public static final String FORWARD_ACTIIVTY = "ForwardActivity";
        //资料分享动态
        public static final String DOCUMENT_SHARE_ACTIIVTY = "DocumentShareActivity";
        //班级公告（选项）分享动态
        public static final String ANNOUNCEMENT_PREFERRED_ACTIIVTY = "AnnouncementPreferredActivity";
        //提问动态
        public static final String TOPIC_ACTIIVTY = "TopicActivity";
        //解答动态
        public static final String SOLUTION_ACTIIVTY = "SolutionActivity";
    }

    /**
     * Defines support Behavior types.
     */
    public class BehaviorType {
        //动态评论
        public static final String COMMENT = "Comment";
        //动态评论回复
        public static final String REPLY = "Reply";
        //动态点赞
        public static final String LIKED = "Liked";

    }

    public class UpdateType{
        public static final String NEW_UPDATE = "NewUpdate";
        public static final String MENTIONED_UPDATE = "ForwardedUpdate";
        public static final String FORWARDED_UPDATE = "NewUpdate";
        public static final String COMMENTED_UPDATE = "CommentedUpdate";
        public static final String REPLIED_UPDATE = "RepliedUpdate";
        public static final String LIKED_UPDATE = "LikedUpdate";
        public static final String SOLUTION_UPDATE = "SolutionUpdate";
    }

    public class SearchType{
        public static final String PERSON = "Person";
        public static final String LESSON = "Lesson";
        public static final String ORGANIZATION = "Organization";
    }

    public static class Relationship{
        public static String CLASS_MATE = "classmate";
        public static String FRIENDS = "friends";
        public static String TEACHER = "teacher";
        public static String STRANGER = "stranger";
    }

    /**
     * Returns the URL to a drawing associated with specific activity.
     * @param key
     * @param thumbnailOnly
     * @return
     */
    public static String getDrawing(String key, boolean thumbnailOnly) {

        StringBuilder sb = new StringBuilder(Constants.XCFSUrl)
                .append("/")
                .append(key);

        if (thumbnailOnly) {
            sb.append("?imageView2/0/w/690");
        }

        return sb.toString();
    }
}
