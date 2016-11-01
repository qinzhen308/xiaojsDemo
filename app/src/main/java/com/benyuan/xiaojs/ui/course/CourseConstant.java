package com.benyuan.xiaojs.ui.course;
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

}