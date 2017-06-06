package cn.xiaojs.xma.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;


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

public class ClosableSlidingLayout extends FrameLayout {
    public final static int SLIDE_FROM_BOTTOM_TO_TOP = 0;
    public final static int SLIDE_FROM_TOP_TO_BOTTOM = 1;
    public final static int SLIDE_FROM_LEFT_TO_RIGHT = 2;
    public final static int SLIDE_FROM_RIGHT_TO_LEFT = 3;

    private static final int INVALID_POINTER = -1;
    private final float MINVEL;
    private View mTarget;
    boolean mSwipeable = true;
    private SlideListener mListener;
    private int height;
    private int top;
    private int left;
    private int width;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean collapsible = false;
    private float xDiff;
    private float yDiff;
    private float mDownX;
    private float mDownY;

    protected ViewDragHelper mDragHelper;

    protected int mSlideOrientation = SLIDE_FROM_LEFT_TO_RIGHT;

    public ClosableSlidingLayout(Context context) {
        this(context, null);
    }

    public ClosableSlidingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ClosableSlidingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDragHelper = ViewDragHelper.create(this, 0.8f, new ViewDragCallback());
        MINVEL = getResources().getDisplayMetrics().density * 400;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        if (!isEnabled()) {
            return false;
        }
        // Fail fast if we're not in a state where a swipe is possible
        /*if (isVerticalScroll()) {
            if (canChildScrollUp()) {
                return false;
            }
        } else {
            if (canChildScrollLeft()) {
                return false;
            }
        }*/

        if (onInterceptChildTouchEvent(event)) {
            return false;
        }

        //=======
        if (mTarget != null && mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM) {
            int bottom = mTarget.getBottom();
            if (event.getY() > bottom) {
                return false;
            }
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mActivePointerId = INVALID_POINTER;
            mIsBeingDragged = false;
            if (collapsible && -yDiff > mDragHelper.getTouchSlop()) {
                expand(mDragHelper.getCapturedView(), 0);
            }
            mDragHelper.cancel();
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                View firstView = getChildAt(0);
                height = firstView.getHeight();
                top = firstView.getTop();
                width = firstView.getWidth();
                left = firstView.getLeft();
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                mIsBeingDragged = false;
                final float initialMotionX = getMotionEventX(event, mActivePointerId);
                final float initialMotionY = getMotionEventY(event, mActivePointerId);
                if ((initialMotionY == -1 && isVerticalScroll())
                        || (initialMotionX == -1 && isHorizontalScroll())) {
                    return false;
                }
                mInitialMotionX = initialMotionX;
                mInitialMotionY = initialMotionY;
                yDiff = 0;
                xDiff = 0;

                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isVerticalScroll()) {
                    if (Math.abs(event.getX() - mDownX) < Math.abs(event.getY() - mDownY)
                            && Math.abs(event.getX() - mDownY) < mDragHelper.getTouchSlop()) {
                        return false;
                    }
                } else {
                    if (Math.abs(event.getX() - mDownX) > Math.abs(event.getY() - mDownY)
                            && Math.abs(event.getY() - mDownX) < mDragHelper.getTouchSlop()) {
                        return false;
                    }
                }

                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(event, mActivePointerId);
                final float x = getMotionEventX(event, mActivePointerId);
                if ((y == -1 && isVerticalScroll())
                        || (x == -1 && isHorizontalScroll())) {
                    return false;
                }

                yDiff = y - mInitialMotionY;
                xDiff = x - mInitialMotionX;
                if (isVerticalScroll()) {
                    if (mSwipeable && Math.abs(yDiff) > mDragHelper.getTouchSlop() && !mIsBeingDragged) {
                        if ((mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM && yDiff > 0)
                                || (mSlideOrientation == SLIDE_FROM_BOTTOM_TO_TOP && yDiff < 0)) {
                            mIsBeingDragged = true;
                            mDragHelper.captureChildView(getChildAt(0), 0);
                        }
                    }
                } else {
                    if (mSwipeable && Math.abs(xDiff) > mDragHelper.getTouchSlop() && !mIsBeingDragged) {
                        if ((mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT && xDiff > 0)
                                || (mSlideOrientation == SLIDE_FROM_RIGHT_TO_LEFT && xDiff < 0)) {
                            mIsBeingDragged = true;
                            mDragHelper.captureChildView(getChildAt(0), 0);
                        }
                    }
                }
                break;
        }
        mDragHelper.shouldInterceptTouchEvent(event);
        return mIsBeingDragged;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
    }

    /**
     * @return Whether it is possible for the child view of this layout to scroll up. Override this
     * if the child view is a custom view.
     */
    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to scroll left. Override
     * this if the child view is a custom view.
     */
    private boolean canChildScrollLeft() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getLeft() < absListView.getPaddingLeft());
            } else {
                return mTarget.getScrollX() > 0;
            }
        } else {
            return ViewCompat.canScrollHorizontally(mTarget, -1);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private float getMotionEventX(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getX(ev, index);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return super.onTouchEvent(ev);
        }

        /*if (isVerticalScroll()) {
            if (canChildScrollUp()) {
                return super.onTouchEvent(ev);
            }
        } else {
            if (canChildScrollLeft()) {
                return super.onTouchEvent(ev);
            }
        }*/

        try {
            if (mSwipeable) {
                mDragHelper.processTouchEvent(ev);
            }
        } catch (Exception ignored) {
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setSlideListener(SlideListener listener) {
        mListener = listener;
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
    }

    private void expand(View releasedChild, float yvel) {
        if (mListener != null) {
            mListener.onOpened();
        }
    }

    private void dismiss(View view) {
        if (isVerticalScroll()) {
            int offsetY = mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM ? top + height : -(top + height);
            mDragHelper.smoothSlideViewTo(view, 0, offsetY);
        } else {
            int offsetX = mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT ? left + width : -(left + width);
            mDragHelper.smoothSlideViewTo(view, offsetX, 0);
        }
        ViewCompat.postInvalidateOnAnimation(ClosableSlidingLayout.this);
    }

    /**
     * set listener
     */
    public interface SlideListener {
        void onClosed();

        void onOpened();
    }

    /**
     * Callback
     */
    private class ViewDragCallback extends ViewDragHelper.Callback {


        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (isVerticalScroll()) {
                if (Math.abs(yvel) > MINVEL) {
                    if ((mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM && yvel > 0)
                            || (mSlideOrientation == SLIDE_FROM_BOTTOM_TO_TOP && yvel < 0)) {
                        dismiss(releasedChild);
                    } else if ((mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM && yvel < 0)
                            || (mSlideOrientation == SLIDE_FROM_BOTTOM_TO_TOP && yvel > 0)) {
                        dismissOrSmoothSlideClose(releasedChild);
                    }
                } else {
                    dismissOrSmoothSlideClose(releasedChild);
                }
            } else {
                if (Math.abs(xvel) > MINVEL) {
                    if ((mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT && xvel > 0)
                            || (mSlideOrientation == SLIDE_FROM_RIGHT_TO_LEFT && xvel < 0)) {
                        dismiss(releasedChild);
                    } else if ((mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT && xvel < 0)
                            || (mSlideOrientation == SLIDE_FROM_RIGHT_TO_LEFT && xvel > 0)) {
                        dismissOrSmoothSlideClose(releasedChild);
                    }
                } else {
                    dismissOrSmoothSlideClose(releasedChild);
                }
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (isVerticalScroll()) {
                if ((mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM && height - top < 1)
                        || (mSlideOrientation == SLIDE_FROM_BOTTOM_TO_TOP && height + top < 1)) {
                    mDragHelper.cancel();
                    mDragHelper.smoothSlideViewTo(changedView, 0, top);
                    if (mListener != null) {
                        mListener.onClosed();
                    }
                }
            } else {
                if ((mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT && width - left < 1)
                        || (mSlideOrientation == SLIDE_FROM_RIGHT_TO_LEFT && width + left < 1)) {
                    mDragHelper.cancel();
                    mDragHelper.smoothSlideViewTo(changedView, left, 0);
                    if (mListener != null) {
                        mListener.onClosed();
                    }
                }
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM) {
                return Math.max(top, ClosableSlidingLayout.this.top);
            } else if (mSlideOrientation == SLIDE_FROM_BOTTOM_TO_TOP) {
                return Math.min(top, ClosableSlidingLayout.this.top);
            }

            return super.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT) {
                return Math.max(left, ClosableSlidingLayout.this.left);
            } else if (mSlideOrientation == SLIDE_FROM_RIGHT_TO_LEFT) {
                return Math.min(left, ClosableSlidingLayout.this.left);
            }

            return super.clampViewPositionHorizontal(child, left, dx);
        }
    }

    public void setTarget(View target) {
        mTarget = target;
    }

    public void setSlideOrientation(int slideOrientation) {
        mSlideOrientation = slideOrientation;
    }


    /**
     * 处理手指离开后view的滑动效果
     */
    private void dismissOrSmoothSlideClose(View releasedChild) {
        switch (mSlideOrientation) {
            case SLIDE_FROM_TOP_TO_BOTTOM:
                if (releasedChild.getTop() >= top + height / 2) {
                    dismiss(releasedChild);
                } else {
                    mDragHelper.smoothSlideViewTo(releasedChild, 0, top);
                    ViewCompat.postInvalidateOnAnimation(ClosableSlidingLayout.this);
                }
                break;
            case SLIDE_FROM_BOTTOM_TO_TOP:
                if (releasedChild.getTop() <= top - height / 2) {
                    dismiss(releasedChild);
                } else {
                    mDragHelper.smoothSlideViewTo(releasedChild, 0, -(top + height));
                    ViewCompat.postInvalidateOnAnimation(ClosableSlidingLayout.this);
                }
                break;
            case SLIDE_FROM_LEFT_TO_RIGHT:
                if (releasedChild.getLeft() >= left + width / 2) {
                    dismiss(releasedChild);
                } else {
                    mDragHelper.smoothSlideViewTo(releasedChild, left, 0);
                    ViewCompat.postInvalidateOnAnimation(ClosableSlidingLayout.this);
                }
                break;
            case SLIDE_FROM_RIGHT_TO_LEFT:
                if (releasedChild.getLeft() <= left - width / 2) {
                    dismiss(releasedChild);
                } else {
                    mDragHelper.smoothSlideViewTo(releasedChild, -(left + width), 0);
                    ViewCompat.postInvalidateOnAnimation(ClosableSlidingLayout.this);
                }
                break;
        }
    }

    /**
     * 手动开启关闭动画
     */
    public void startCloseAnim(MotionEvent event, int slideOrientation) {
        View view = getChildAt(0);
        if (view != null && mDragHelper != null) {
            mDragHelper.captureChildView(view, 0);
            mDragHelper.processTouchEvent(event);

            height = view.getHeight();
            top = view.getTop();
            width = view.getWidth();
            left = view.getLeft();

            int finalTop = 0;
            int finalLeft = 0;
            switch (slideOrientation) {
                case SLIDE_FROM_TOP_TO_BOTTOM:
                    finalTop = top + height;
                    mDragHelper.smoothSlideViewTo(view, 0, finalTop);
                    break;
                case SLIDE_FROM_BOTTOM_TO_TOP:
                    finalTop = -(top + height);
                    mDragHelper.smoothSlideViewTo(view, 0, finalTop);
                    break;
                case SLIDE_FROM_LEFT_TO_RIGHT:
                    finalLeft = left + width;
                    mDragHelper.smoothSlideViewTo(view, finalLeft, 0);
                    break;
                case SLIDE_FROM_RIGHT_TO_LEFT:
                    finalLeft = -(left + width);
                    mDragHelper.smoothSlideViewTo(view, -finalLeft, 0);
                    break;
            }
        }
    }

    protected boolean isHorizontalScroll() {
        return mSlideOrientation == SLIDE_FROM_LEFT_TO_RIGHT || mSlideOrientation == SLIDE_FROM_RIGHT_TO_LEFT;
    }

    protected boolean isVerticalScroll() {
        return mSlideOrientation == SLIDE_FROM_TOP_TO_BOTTOM || mSlideOrientation == SLIDE_FROM_BOTTOM_TO_TOP;
    }

    protected boolean onInterceptChildTouchEvent(MotionEvent event) {
        return false;
    }

}