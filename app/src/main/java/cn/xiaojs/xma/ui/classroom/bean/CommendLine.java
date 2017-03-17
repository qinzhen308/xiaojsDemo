package cn.xiaojs.xma.ui.classroom.bean;
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
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

import java.util.List;

public class CommendLine {
    public List<Commend> whiteboardCommends;
    public String src;
    public long time;

    @Override
    public String toString() {
        int size = whiteboardCommends != null ? whiteboardCommends.size() : 0;
        return "---------whiteboardCommends=" + size + " src=" + src + " time=" + time;
    }
}
