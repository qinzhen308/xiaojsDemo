package cn.xiaojs.xma.ui.classroom.whiteboard;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;

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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

public class WhiteboardScrollerView extends ViewPager {
    private Context mContext;
    private int mMode = PhotoDoodleFragment.MODE_PREVIEW;

    public WhiteboardScrollerView(Context context) {
        super(context);
        init(context);
    }

    public WhiteboardScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mMode == PhotoDoodleFragment.MODE_EDIT) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

}
