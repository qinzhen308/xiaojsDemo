package cn.xiaojs.xma.ui.classroom;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;

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
    private ClassroomGestureDetector mGestureDetector;
    private WhiteboardScrollerView mWhiteboardScrollview;

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

    public void setGestureDetector(ClassroomGestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    public void setTransformationWhiteBoard(Whiteboard whiteboard) {
        mWhiteboard = whiteboard;
    }

    public void setWhiteboardSv(WhiteboardScrollerView wbSv) {
        mWhiteboardScrollview = wbSv;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mWhiteBoardTransformation = false;
                break;
        }

        if (mGestureDetector != null) {
            if (ClassroomState.STATE_MAIN_PANEL == mGestureDetector.getState()) {
                int action = event.getActionMasked();
                int pointCnt = event.getPointerCount();
                if (pointCnt > 1) {
                    mWhiteBoardTransformation = true;
                } else {
                    //传递教室主控制面板的事件分发
                    mGestureDetector.onTouchEvent(event);
                    //传递水平滚动的的白板事件分发
                    if (!mWhiteBoardTransformation) {
                        mWhiteboardScrollview.onTouchEvent(event);
                    }
                }

                //传递白板的变换事件分发：如缩放，平移动动作
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
