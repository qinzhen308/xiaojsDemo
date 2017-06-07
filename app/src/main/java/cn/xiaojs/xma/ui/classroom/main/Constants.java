package cn.xiaojs.xma.ui.classroom.main;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/1/4
 * Desc:
 *
 * ======================================================================================== */

public interface Constants {
    /**
     * first using classroom
     */
    public final static String KEY_CLASSROOM_FIRST_USE = "key_classroom_first_use";

    public final static String KEY_CAMERA_OPEN = "key_camera_open";
    public final static String KEY_MICROPHONE_OPEN = "key_microphone_open";
    public final static String KEY_MULTI_TALK_OPEN = "key_multi_talk_open";
    public final static String KEY_QUALITY = "key_quality";
    public final static String KEY_TICKET = "key_ticket";
    public final static String KEY_LESSON_ID = "key_lesson_id";
    public final static String KEY_CLASS_ID = "key_class_id";
    public final static String KEY_LIB_DOC = "key_lib_doc";
    public final static String KEY_IMG_DISPLAY_TYPE = "key_img_display_mode";
    public final static String KEY_IMG_LIST = "key_img_list";
    public final static String KEY_DOODLE_RATIO = "key_doodle_ratio";
    public final static String KEY_CTL_SESSION = "key_ctl_session";
    public final static String KEY_MSG_INPUT_TXT = "key_msg_input_txt";
    public final static String KEY_MSG_INPUT_FROM = "key_msg_input_from";
    public final static String KEY_TALK_ATTEND = "key_talk_attend";
    public final static String KEY_TALK_ACTION = "key_talk_action";
    public final static String KEY_OPEN_DOC_BEAN = "key_open_doc_bean";
    public final static String KEY_SHEET_GRAVITY = "key_sheet_gravity";
    public final static String KEY_PAGE_HEIGHT = "key_page_height";


    public final static String KEY_PUBLISH_TYPE = "key_publish_type";
    public final static String KEY_PLAY_URL = "key_play_url";
    public final static String KEY_PUBLISH_URL = "key_publish_url";
    public final static String KEY_BEFORE_LIVE_STATE = "key_before_live_state";
    public final static String KEY_INDIVIDUAL_RESPONSE = "key_individual_response";
    public final static String KEY_INDIVIDUAL_DURATION = "key_individual_duration";
    public final static String KEY_INDIVIDUAL_NAME = "key_individual_name";
    public final static String KEY_COUNT_TIME = "key_count_time";
    public final static String KEY_FROM = "key_from";

    public final static int FROM_ACTIVITY = 0;
    public final static int FROM_PLAY_FRAGMENT = 1;
    public final static int FROM_PUBLISH_FRAGMENT = 2;

    /**
     * if allow to live for mobile network
     */
    public final static String KEY_MOBILE_NETWORK_LIVE = "key_mobile_network_live";
    /**
     * if allow to live single lesson for mobile network
     */
    public final static String KEY_MOBILE_NETWORK_LIVE_4_LESSON = "key_mobile_network_live_4_lesson";

    /**
     * quality
     */
    public final static String KEY_RESOLUTION_CHANGE = "key_resolution_change";
    public final static int QUALITY_HIGH = 0; // 高清
    public final static int QUALITY_STANDARD = 1; // 标准
    public final static int QUALITY_FLUENT = 2; // 流畅

    /**
     * switcher
     */
    public final static String KEY_SWITCH_CHANGE = "key_switch_change";
    public final static int SWITCHER_MOBILE_NETWORK_LIVE = 1; //移动网络直播
    public final static int SWITCHER_CAMERA = 2; //摄像头是否打开
    public final static int SWITCHER_AUDIO = 3; //麦克风是否打开

    public final static int SHARE_IMG_SIZE = 800; //分享图片的大小不超过800

    public final static float VIDEO_VIEW_RATIO = 4 / 3.0f;

    /**
     * 客户端用户
     */
    public static enum User {
        NONE("None"),                                  //无效身份
        ADVISER("AdviserSession"),                     //班主任
        TEACHER("LeadSession"),                        //老师
        ASSISTANT("AssistantSession"),                 //助教
        REMOTE_ASSISTANT("RemoteAssistantSession"),    //远程助教
        STUDENT("StudentSession"),                     //学生
        MANAGER("ManagerSession"),                     //监管
        AUDITOR("AuditorSession"),                     //旁听
        ADMINISTRATOR("AdministrationSession");        //管理员

        private final String text;

        private User(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    /**
     * 客户端用户
     */
    public static enum UserMode {
        PREVIEW("preview"),                         //预览
        PARTICIPANT("participant"),                 //参与者
        TEACHING("teaching"),                       //教学
        MEDIA("media");                             //媒体

        private final String text;

        private UserMode(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    /**
     * classroom mode
     */
    public static final int TEACHING_MODE = 1;
    public static final int PARTICIPANT_MODE = 2;
    public static final int MEDIA_MODE = 3;
    public static final int PREVIEW_MODE = 4;

    //教室类型
    public static enum ClassroomType {
        PrivateClass,
        StandaloneLesson,
    }
}
