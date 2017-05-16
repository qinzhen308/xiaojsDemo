package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cn.xiaojs.xma.ui.widget.ClosableSlidingLayout;

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
 * Date:2017/5/9
 * Desc:
 *
 * ======================================================================================== */

public class ClosableAdapterSlidingLayout extends ClosableSlidingLayout {
    private float mInitialMotionX;
    private float mInitialMotionY;
    private View mSlideConflictView;
    private Rect mRect;

    public ClosableAdapterSlidingLayout(Context context) {
        super(context);
    }

    public ClosableAdapterSlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClosableAdapterSlidingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onInterceptChildTouchEvent(MotionEvent ev) {
        if (isHorizontalScroll()) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mInitialMotionX = ev.getX();
                    mInitialMotionY = ev.getY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (touchInConflictView(ev)
                            && Math.abs(ev.getX() - mInitialMotionX) < Math.abs(ev.getY() - mInitialMotionY)
                            && Math.abs(ev.getY() - mInitialMotionY) > mDragHelper.getTouchSlop()) {
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
                default:
                    break;
            }
        }

        return false;
    }

    public void setSlideConflictView(View view) {
        mSlideConflictView = view;
    }

    public boolean touchInConflictView(MotionEvent ev) {
        if (mSlideConflictView == null) {
            return true;
        }

        if (mRect == null) {
            mRect = new Rect();
            mRect.set(mSlideConflictView.getLeft(), mSlideConflictView.getTop(),
                    mSlideConflictView.getRight(), mSlideConflictView.getBottom());
        }
        return mRect.contains((int) ev.getX(), (int) ev.getY());
    }

}
