package com.benyuan.xiaojs.ui.classroom;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

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
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

public class MainPanel extends RelativeLayout {
    private ClassRoomGestureDetector mGestureDetector;

    public MainPanel(Context context) {
        super(context);
    }

    public MainPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MainPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public void setGestureDetector(ClassRoomGestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            if (ClassRoomActivity.STATE_MAIN_PANEL == mGestureDetector.getState()) {
                mGestureDetector.onTouchEvent(event);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
