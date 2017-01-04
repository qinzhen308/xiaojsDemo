package cn.xiaojs.xma.ui.classroom.whiteboard.core;
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
 * Date:2016/12/6
 * Desc:
 *
 * ======================================================================================== */

public interface Action {
    public static final int NO_ACTION = -1;
    public static final int ADD_ACTION = 0;
    public static final int MOVE_ACTION = 1;
    public static final int SCALE_ACTION = 2;
    public static final int ROTATE_ACTION = 3;
    public static final int SCALE_ROTATE_ACTION = 4;
    public static final int CHANGE_AREA_ACTION = 5;
    public static final int DELETE_ACTION = 6;
}
