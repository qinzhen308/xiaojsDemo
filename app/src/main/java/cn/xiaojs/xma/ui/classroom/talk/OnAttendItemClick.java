package cn.xiaojs.xma.ui.classroom.talk;
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
 * Date:2017/2/24
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.model.live.Attendee;

public interface OnAttendItemClick {
    public final static int ACTION_OPEN_CAMERA = 1;
    public final static int ACTION_OPEN_TALK = 2;

    public void onItemClick(int action, Attendee attendee);
}
