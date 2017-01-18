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
 * Date:2016/12/27
 * Desc:
 *
 * ======================================================================================== */

public interface PanelCallback {
    public final static int COURSE_PANEL = 1;
    public final static int TALK_PANEL_MSG = 2;
    public final static int TALK_PANEL_CONTACT = 3;
    public final static int MESSAGE_PANEL = 4;
    public final static int SETTING_PANEL = 5;
    public final static int INVITE_FRIEND_PANEL = 6;
    public final static int QUESTION_ANSWER_PANEL = 7;

    public void onOpenPanel(int panel);

    public void onClosePanel(int panel);
}
