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

    public Payload payload;

    public static class Payload {
        public int to;
        public long time;
        public TalkContent body;
    }

    public static class TalkContent{
        public String text;
    }
}
