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
 * Date:2017/5/16
 * Desc:
 *
 * ======================================================================================== */

import java.util.Comparator;

import cn.xiaojs.xma.model.live.Attendee;

public class AttendsComparator implements Comparator<Attendee> {
    @Override
    public int compare(Attendee o1, Attendee o2) {
        if (o1 == null || o2 == null) {
            return 0;
        }

        return o1.sort > o2.sort ? 1 : o1.sort == o2.sort ? 0 : -1;
    }
}
