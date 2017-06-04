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
    private int mType;

    public SimpleDataChangeListener(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public final static int LESSON_CREATION_CHANGED = 1 << 1;
    public final static int DYNAMIC_CHANGED = 1 << 2;
    public final static int LESSON_ENROLL_CHANGED = 1 << 3;
    public final static int CREATE_CLASS_CHANGED = 1 << 4;
}

