package cn.xiaojs.xma.ui.lesson;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

public interface CourseConstant {

    public static final int STU_ON_COURSING = 1;//学生开课中
    public static final int STU_WAIT_COURSE = 2;//学生待开课
    public static final int STU_END_COURSE = 3;//学生已完课
    public static final int STU_CANCEL_COURSE = 4;//学生已取消
    public static final int STU_PRIVATE_WAIT_COURSE = 5;//学生私密课待开课

    public static final int TEACHER_WAIT_GROUND = 11;//老师待上架
    public static final int TEACHER_EXAMINING = 12;//老师审核中
    public static final int TEACHER_GROUND_WAIT_COURSE = 13;//已上架待开课
    public static final int TEACHER_GROUND_COURSING = 14;//已上架开课中
    public static final int TEACHER_GROUND_END_COURSE = 15;//已上架已完课
    public static final int TEACHER_GROUND_CANCEL_COURSE = 16;//已上架已取消
    public static final int TEACHER_FAILURE = 17;//上架失败
    public static final int TEACHER_FORCE_CLOSE = 18;//强制关闭
    public static final int TEACHER_PRIVATE_WAIT_COURSE = 19;//老师私密课待开课

    public final static int COURSE_COVER_WIDTH = 520; //课程封面宽
    public final static int COURSE_COVER_HEIGHT = 294;//课程封面高

    public final static String KEY_LESSON_OPTIONAL_INFO = "key_lesson_optional_info";
    public final static String KEY_IS_TEACHER = "key_is_teacher";
    public final static String KEY_LESSON_BEAN = "lesson_bean";
    public final static String KEY_LESSON_ID = "lesson_id";
    public final static String KEY_ENTRANCE_TYPE = "key_entrance_type";
    public final static String KEY_TEACH_ACTION_TYPE = "teach_action_type";
    public final static String KEY_SUBJECT = "key_subject";
    public final static String KEY_CLOSE_COURSE_CREATE_TIP = "key_close_course_create_tips";
    //创建直播课
    public final static int TYPE_LESSON_CREATE = 0;
    //编辑课
    public final static int TYPE_LESSON_EDIT = 1;
    //再次开课
    public final static int TYPE_LESSON_AGAIN = 2;

    public final static int CODE_EDIT_LESSON = 1;
    public final static int CODE_LESSON_AGAIN = 2;
    public final static int CODE_CANCEL_LESSON = 3;
    public final static int CODE_CREATE_LESSON = 4;


}
