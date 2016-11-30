package com.benyuan.xiaojs.ui.classroom.whiteboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.benyuan.xiaojs.ui.classroom.ClassRoomActivity;
import com.benyuan.xiaojs.ui.classroom.ClassRoomGestureDetector;
import com.benyuan.xiaojs.ui.classroom.MainPanel;

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

public class WhiteBoard extends View {
    private ClassRoomGestureDetector mGestureDetector;

    public WhiteBoard(Context context) {
        super(context);
    }

    public WhiteBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WhiteBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public WhiteBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setGestureDetector(ClassRoomGestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            if (ClassRoomActivity.STATE_WHITE_BOARD == mGestureDetector.getState()) {
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

