package com.benyuan.xiaojs.ui.classroom;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.benyuan.xiaojs.ui.classroom.whiteboard.Whiteboard;

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
    private Whiteboard mWhiteboard;
    private boolean mWhiteBoardTransformation = false;

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
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }

    public void setGestureDetector(ClassRoomGestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    public void setTransformationWhiteBoard(Whiteboard whiteboard) {
        mWhiteboard = whiteboard;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            if (ClassRoomActivity.STATE_MAIN_PANEL == mGestureDetector.getState()) {
                int action = event.getActionMasked();
                int pointCnt = event.getPointerCount();
                if (pointCnt > 1) {
                    mWhiteBoardTransformation = true;
                } else {
                    mGestureDetector.onTouchEvent(event);
                }

                if (mWhiteBoardTransformation && mWhiteboard != null) {
                    mWhiteboard.transformation(event);
                    switch (action) {
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            mWhiteBoardTransformation = false;
                            break;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
