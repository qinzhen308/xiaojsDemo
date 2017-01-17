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

    /**
     * classroom url
     */
    public final static String CLASSROOM_BASE_URL = "192.168.100.3";
    /**
     * classroom port
     */
    public final static String CLASSROOM_PORT = "7000";
    /**
     * classroom path(namespace), which lesson id
     */
    public final static String CLASSROOM_PATH = "9689A606";
    /**
     * draw
     */
    public final static String ROOM_DRAW = "drawing";
    /**
     * chat
     */
    public final static String ROOM_CHAT = "聊天";

    /**
     * 客户端用户
     */
    public static enum User {
        TEACHER, //老师
        STUDENT, //学生
        ADMIN, //监管
        AUDIT //旁听
    }
}
