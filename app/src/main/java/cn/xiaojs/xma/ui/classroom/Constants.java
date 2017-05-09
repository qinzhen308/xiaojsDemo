package cn.xiaojs.xma.ui.classroom;
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
    public final static String KEY_URL = "key_url";
    public final static String KEY_LIB_DOC = "key_lib_doc";
    public final static String KEY_IMG_DISPLAY_MODE = "key_img_display_mode";
    public final static String KEY_IMG_LIST = "key_img_list";

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
    public final static int QUALITY_HIGH = 0; // 高清
    public final static int QUALITY_STANDARD = 1; // 标准
    public final static int QUALITY_FLUENT = 2; // 流畅

    /**
     * switcher
     */
    public final static int SWITCHER_MOBILE_NETWORK_LIVE = 1; //移动网络直播
    public final static int SWITCHER_CAMERA = 2; //摄像头是否打开
    public final static int SWITCHER_AUDIO = 3; //麦克风是否打开

    public final static int SHARE_IMG_SIZE = 800; //分享图片的大小不超过800

    /**
     * 客户端用户
     */
    public static enum User {
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
     * classroom mode
     */
    public static final int TEACHING_MODE = 1;
    public static final int PARTICIPANT_MODE = 2;
    public static final int MEDIA_MODE = 3;
    public static final int PREVIEW_MODE = 4;
}
