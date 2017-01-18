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
 * Date:2017/1/17
 * Desc:
 *
 * ======================================================================================== */

public class TalkBean {
    public static final int TYPE_MINE = 1;
    public static final int TYPE_OTHER = 2;
    public static final int TYPE_DESC = 3;

    public String name;
    public String portrait;
    public String content;
    public long time;
    public int messageType;
}
