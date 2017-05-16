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
 * Date:2017/5/8
 * Desc:
 *
 * ======================================================================================== */

import java.util.Comparator;

import cn.xiaojs.xma.model.live.TalkItem;

public class TalkComparator implements Comparator<TalkItem> {
    @Override
    public int compare(TalkItem o1, TalkItem o2) {
        if (o1 == null || o2 == null) {
            return 0;
        }

        if (o1.time == null || o2.time == null) {
            return 0;
        }

        return o1.time.getTime() > o2.time.getTime() ? 1 : o1.time.getTime() == o2.time.getTime() ? 0 : -1;
    }
}
