
package com.benyuan.xiaojs.ui.classroom.whiteboard.core;
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
 * Date:2016/10/18
 * Desc:
 *
 * ======================================================================================== */

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ViewGestureListener {

    public static final float MAX_SCALE = 5.0f;
    public static final float MIN_SCALE = 1.0f;

    private int mVisibleWidth;
    private int mVisibleHeight;

    private int mImageWidth;
    private int mImageHeight;

    private float mScale;
    private float mInverseScale;
    private float mBeginScale;
    private float mOldScale;

    // translate offset
    private float mTranslateX;
    private float mTranslateY;

    private float mOldFocusX;
    private float mOldFocusY;

    private RectF mDstRect;

    private float mCorrectedScaleOffsetX;
    private float mCorrectedScaleOffsetY;

    private int mImageCenterX;
    private int mImageCenterY;

    private boolean mScaling = false;
    private boolean mFakeScale = false;

    private boolean hasScaleAction = false;

    private ValueHolder mValueHolder;
    private ValueAnimator mAnimator;

    private final ScaleGestureDetector mScaleDetector;
    private final GestureDetector mGestureDetector;
    private final DownUpDetector mDownUpDetector;

    private ViewRectChangedListener mRectChangedListener;
    private onTouchEventListener mOnTouchEventListener;

    private float mRelativeX;
    private float mRelativeY;
    //private float mScreenSwitchScale;

    public interface ViewRectChangedListener {
        void onViewRectChanged();
    }

    public interface onTouchEventListener {
        public boolean onActionDown(MotionEvent event);

        public void onActionMove(MotionEvent event);

        public void onActionUP(MotionEvent event);

        public void onSingleTapConfirmed(MotionEvent event);
        
        public void onActionPointerDown(MotionEvent event);

        public void onSingleTapUp(MotionEvent event);

        public void onLongPress(MotionEvent event);

        public void onDoubleTap(MotionEvent event);

        public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
    }

    public ViewGestureListener(Context context, ViewRectChangedListener rectChangedlistener,
            onTouchEventListener touchListener) {
        mTranslateX = 0;
        mTranslateY = 0;

        mScale = 1.0f;
        mInverseScale = 1.0f;
        mOldScale = 1.0f;
        //mScreenSwitchScale = 1.0f;

        mDstRect = new RectF();
        mRectChangedListener = rectChangedlistener;
        mOnTouchEventListener = touchListener;

        mValueHolder = new ValueHolder();
        mAnimator = ObjectAnimator.ofFloat(mValueHolder, "factor", 0.0f, 1.0f);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mDownUpDetector = new DownUpDetector();
    }

    public void onViewChanged(int width, int height, int imageW, int imageH) {
        onViewChanged(width, height, imageW, imageH, true);
    }

    public void onViewChanged(int width, int height, int imageW, int imageH, boolean changed) {
        if (mImageWidth == 0 || mImageHeight == 0) {
            updateViewInfo(width, height, imageW, imageH);
            float offsetX = (mVisibleWidth - mImageWidth) / 2.0f;
            float offsetY = (mVisibleHeight - mImageHeight) / 2.0f;
            mDstRect.set(offsetX, offsetY, mVisibleWidth - offsetX, mVisibleHeight - offsetY);
        } else if (changed || imageW != mImageWidth || imageH != mImageHeight) {
            // ConfigurationChanged
            // 屏幕上的点映射到图片上的相对图片的偏移比例value<0，1>的点
            float oldRectW = mDstRect.width();
            float oldRectH = mDstRect.height();
            float s = imageW / (float) mImageWidth;

            mRelativeX = (mVisibleWidth / 2.0f - mDstRect.left) / oldRectW;
            mRelativeY = (mVisibleHeight / 2.0f - mDstRect.top) / oldRectH;

            float left = width / 2.0f - mRelativeX * (oldRectW * s);
            float top = height / 2.0f - mRelativeY * (oldRectH * s);

            updateViewInfo(width, height, imageW, imageH);
            mImageCenterX = Math.round(left + (oldRectW * s) * 0.5f);
            mImageCenterY = Math.round(top + (oldRectH * s) * 0.5f);
            calculateDestRect(mImageCenterX, mImageCenterY, mScale);

            // correct
            float offsetX = 0;
            float offsetY = 0;
            if (mDstRect.width() < mVisibleWidth) {
                offsetX = mVisibleWidth / 2.0f - mImageCenterX;
            } else {
                if (mDstRect.left > 0) {
                    offsetX = -mDstRect.left;
                }
                if (mDstRect.right < mVisibleWidth) {
                    offsetX = mVisibleWidth - mDstRect.right;
                }
            }

            if (mDstRect.height() < mVisibleHeight) {
                offsetY = mVisibleHeight / 2.0f - mImageCenterY;
            } else {
                if (mDstRect.bottom < mVisibleHeight) {
                    offsetY = mVisibleHeight - mDstRect.bottom;
                }
                if (mDstRect.top > 0) {
                    offsetY = -mDstRect.top;
                }
            }

            mDstRect.offset(offsetX, offsetY);
        }
        mRectChangedListener.onViewRectChanged();
    }

    private void updateViewInfo(int width, int height, int imageW, int imageH) {
        mVisibleWidth = width;
        mVisibleHeight = height;
        mImageWidth = imageW;
        mImageHeight = imageH;
    }

    public RectF getDestRect() {
        return mDstRect;
    }

    public float getTranslateX() {
        return mTranslateX;
    }

    public float getTranslateY() {
        return mTranslateY;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        if (scale > MAX_SCALE) {
            scale = MAX_SCALE;
        }

        if (scale < MIN_SCALE){
            scale = MIN_SCALE;
        }
        mScale = scale;
    }

    public float getInverseScale() {
        return mInverseScale;
    }

    public void reset() {
        mInverseScale = 1.0f;
        mOldScale = 1.0f;
        mScale = 1.0f;

        mTranslateX = 0;
        mTranslateY = 0;

        float offsetX = (mVisibleWidth - mImageWidth) / 2.0f;
        float offsetY = (mVisibleHeight - mImageHeight) / 2.0f;
        mDstRect.set(offsetX, offsetY, mVisibleWidth - offsetX, mVisibleHeight - offsetY);
    }

    public boolean onBackPressed() {

        return false;
    }

    public boolean scaleBegin(float focusX, float focusY) {
        mAnimator.cancel();
        mBeginScale = mScale;

        mOldScale = 1.0f;
        mOldFocusX = focusX;
        mOldFocusY = focusY;

        mImageCenterX = Math.round(mDstRect.centerX());
        mImageCenterY = Math.round(mDstRect.centerY());

        mCorrectedScaleOffsetX = 0;
        mCorrectedScaleOffsetY = 0;

        return true;
    }

    public boolean scaling(float focusX, float focusY, float scale) {
        mScale = mBeginScale * scale;
        mInverseScale = 1.0f / mScale;

        onScaleRectChanged(scale, focusX, focusY);
        return false;
    }

    public void scaleEnd() {
        hasScaleAction = true;
    }

    /**
     * move by two pointer
     * @param event
     */
    private void scrollAction(MotionEvent event) {
        if (!mScaling) {
            float focusX = (event.getX(0) + event.getX(1)) * 0.5f;
            float focusY = (event.getY(0) + event.getY(1)) * 0.5f;
            if (mFakeScale) {
                scaling(focusX, focusY, 1.0f);
            } else {
                mFakeScale = true;
                scaleBegin(focusX, focusY);
            }
        }
    }

    public void scaleEndAction() {
        if (!hasScaleAction)
            return;
        onScaleEndRectChanged();
    }

    private void onScaleRectChanged(float scale, float focusX, float focusY) {
        float translateX = 0;
        float translateY = 0;

        float scaleOffsetX = 0;
        float scaleOffsetY = 0;

        if (mDstRect.width() >= mVisibleWidth) {
            translateX = (focusX - mOldFocusX);
            scaleOffsetX = (focusX - mImageCenterX) * (1 - scale / mOldScale);
        }

        if (mDstRect.height() >= mVisibleHeight) {
            translateY = (focusY - mOldFocusY);
            scaleOffsetY = (focusY - mImageCenterY) * (1 - scale / mOldScale);
        }

        if (mScale > MAX_SCALE || mScale < MIN_SCALE) {
            mCorrectedScaleOffsetX -= scaleOffsetX + translateX;
            mCorrectedScaleOffsetY -= scaleOffsetY + translateY;
        }

        // update the position of picture
        mImageCenterX = Math.round(mImageCenterX + translateX + scaleOffsetX);
        mImageCenterY = Math.round(mImageCenterY + translateY + scaleOffsetY);
        calculateDestRect(mImageCenterX, mImageCenterY, mScale);
        // Record the current parameter
        mOldScale = scale;
        mOldFocusX = focusX;
        mOldFocusY = focusY;
    }

    private void onScaleEndRectChanged() {
        float offsetX = 0;
        float offsetY = 0;

        float scaleOffsetX = 0;
        float scaleOffsetY = 0;

        float scale = mScale;

        float left = mDstRect.left;
        float top = mDstRect.top;
        float right = mDstRect.right;
        float bottom = mDstRect.bottom;

        float imgCenterX = mImageCenterX;
        float imgCenterY = mImageCenterY;

        int width = Math.round(right - left);
        int height = Math.round(bottom - top);
        boolean scaleAnim = false;

        // the offset of scaling two pointer
        if (mScale > MAX_SCALE || mScale < MIN_SCALE) {
            scaleAnim = true;
            scale = scale > MAX_SCALE ? MAX_SCALE : MIN_SCALE;
            scaleOffsetX = mCorrectedScaleOffsetX;
            scaleOffsetY = mCorrectedScaleOffsetY;

            imgCenterX += scaleOffsetX;
            imgCenterY += scaleOffsetY;

            left = imgCenterX - (mImageWidth / 2.0f) * scale;
            right = imgCenterX + (mImageWidth / 2.0f) * scale;
            top = imgCenterY - (mImageHeight / 2.0f) * scale;
            bottom = imgCenterY + (mImageHeight / 2.0f) * scale;

            width = Math.round(right - left);
            height = Math.round(bottom - top);
        }

        // the offset of moving two pointer
        if (height >= mVisibleHeight) {
            if (bottom < mVisibleHeight) {
                offsetY = mVisibleHeight - bottom;
            }
            if (top > 0) {
                offsetY = -top;
            }
        } else {
            offsetY = mVisibleHeight / 2.0f - mImageCenterY;
        }

        if (width >= mVisibleWidth) {
            if (right < mVisibleWidth) {
                offsetX = mVisibleWidth - right;
            }
            if (left > 0) {
                offsetX = -left;
            }
        } else {
            offsetX = mVisibleWidth / 2.0f - mImageCenterX;
        }
        
         //mImageCenterX += offsetX + scaleOffsetX;
         //mImageCenterY += offsetY + scaleOffsetY;
         //mScale = scale;
         //calculateDestRect(mImageCenterX, mImageCenterY, mScale);

        // start anim
        if (offsetX != 0.0f || offsetY != 0.0f || scaleAnim) {
            mAnimator.cancel();
            mValueHolder.init(mScale, mImageCenterX, mImageCenterY, scale,
                    (offsetX + scaleOffsetX), (offsetY + scaleOffsetY));
            mAnimator.start();
        }
    }

    private void calculateDestRect(int imageCenterX, int imageCenterY, float scale) {
        float left = imageCenterX - (mImageWidth / 2.0f) * scale;
        float right = imageCenterX + (mImageWidth / 2.0f) * scale;
        float top = imageCenterY - (mImageHeight / 2.0f) * scale;
        float bottom = imageCenterY + (mImageHeight / 2.0f) * scale;
        mDstRect.set(left, top, right, bottom);
        mRectChangedListener.onViewRectChanged();
    }

    private boolean hasHandle = false;
    public void onTouchEvent(MotionEvent event) {
        int pointCnt = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                hasScaleAction = false;
                if (mOnTouchEventListener.onActionDown(event)){
                    hasHandle = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mOnTouchEventListener.onActionPointerDown(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (pointCnt == 2) {
                    if (mFakeScale) {
                        mFakeScale = false;
                        scaleEnd();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointCnt > 1 && !hasHandle) {
                    scrollAction(event);
                } else {
                    if (isDown() || hasHandle) {
                        // single pointer action move
                        mOnTouchEventListener.onActionMove(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                hasHandle = false;
                scaleEndAction();
                mOnTouchEventListener.onActionUP(event);
                break;
        }
        mGestureDetector.onTouchEvent(event);
        if (pointCnt > 1) {
            mScaleDetector.onTouchEvent(event);
        }
        mDownUpDetector.onTouchEvent(event);
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (hasHandle) {
                return false;
            }
            if (mFakeScale) {
                mFakeScale = false;
                mScaling = true;
                scaling(detector.getFocusX(), detector.getFocusY(), 1.0f);
                return true;
            }
            mScaling = true;
            return scaleBegin(detector.getFocusX(), detector.getFocusY());
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return scaling(detector.getFocusX(), detector.getFocusY(), detector.getScaleFactor());
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mScaling = false;
            mFakeScale = false;
            scaleEnd();
        }
    }

    private class GestureListener extends SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            mOnTouchEventListener.onLongPress(e);
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mOnTouchEventListener.onScroll(e1, e2, distanceX, distanceY);
            return true;
            // return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mOnTouchEventListener.onSingleTapUp(e);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mOnTouchEventListener.onDoubleTap(e);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mOnTouchEventListener.onSingleTapConfirmed(e);
            return super.onSingleTapConfirmed(e);
        }

    }

    private class DownUpDetector {

        private boolean mStillDown;

        private void setState(boolean down, MotionEvent e) {
            if (down == mStillDown)
                return;
            mStillDown = down;
        }

        public void onTouchEvent(MotionEvent ev) {
            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    setState(true, ev);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_DOWN: // Multitouch event - abort.
                    setState(false, ev);
                    break;
            }
        }

        public boolean isDown() {
            return mStillDown;
        }
    }

    public boolean isDown() {
        return mDownUpDetector.isDown();
    }

    private class ValueHolder {
        private float mFactor;

        private float mStartScale;
        private float mStartImageCenterX;
        private float mStartImageCenterY;
        private float mTargetScale;
        private float mOffsetX;
        private float mOffsetY;

        public void init(float startScale, float startImageCenterX, float startImageCenterY,
                float targetScale, float offsetX, float offsetY) {
            mStartScale = startScale;
            mStartImageCenterX = startImageCenterX;
            mStartImageCenterY = startImageCenterY;
            mTargetScale = targetScale;
            mOffsetX = offsetX;
            mOffsetY = offsetY;
        }

        @SuppressWarnings("unused")
        public void setFactor(float factor) {
            mFactor = factor;
            mImageCenterX = Math.round(mStartImageCenterX + factor * mOffsetX);
            mImageCenterY = Math.round(mStartImageCenterY + factor * mOffsetY);
            mScale = mStartScale + (mTargetScale - mStartScale) * factor;
            mInverseScale = 1.0f / mScale;
            calculateDestRect(mImageCenterX, mImageCenterY, mScale);
        }

        @SuppressWarnings("unused")
        public float getFactor() {
            return mFactor;
        }
    }

    public boolean hasScaleAction() {
        return hasScaleAction;
    }

}
