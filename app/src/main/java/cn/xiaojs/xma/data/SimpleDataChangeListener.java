package cn.xiaojs.xma.data;

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
 * Date:2017/3/27
 * Desc:
 *
 * ======================================================================================== */

public abstract class SimpleDataChangeListener implements DataChangeListener {
    private ListenerType mType;

    public SimpleDataChangeListener(ListenerType type) {
        mType = type;
    }

    public ListenerType getType() {
        return mType;
    }

    public static enum ListenerType{
        LESSON_CREATION_CHANGED,
        DYNAMIC_CHANGED
    }
}

