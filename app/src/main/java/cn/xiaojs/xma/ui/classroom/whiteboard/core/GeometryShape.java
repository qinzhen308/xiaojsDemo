package cn.xiaojs.xma.ui.classroom.whiteboard.core;
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

import android.graphics.PointF;
import android.graphics.RectF;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

public abstract class GeometryShape extends Doodle {
    public final static int BEELINE = 0;
    public final static int RECTANGLE = 1;
    public final static int OVAL = 2;
    public final static int TRIANGLE = 3;
    public final static int ARROW = 4;
    public final static int DOUBLE_ARROW = 5;

    protected int mGeometryId;

    private PointF mHorizontalVector = new PointF();
    private PointF mVerticalVector = new PointF();
    private PointF mCurrVector = new PointF();

    protected GeometryShape(Whiteboard whiteboard, int style, int geometryId) {
        super(whiteboard, style);
        mGeometryId = geometryId;
    }

    protected abstract void changeShape(float touchX, float touchY);

    protected abstract double computeArea();

    protected abstract double computeEdgeLength();

    public static boolean isTwoDimension(int geometryId) {
        switch (geometryId) {
            case ARROW:
            case DOUBLE_ARROW:
            case BEELINE:
            case RECTANGLE:
            case OVAL:
                return true;
        }

        return false;
    }

    public int getGeometryId() {
        return mGeometryId;
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        updateRectOnChangeAreaByEdge(oldX, oldY, x, y, edge);

        //update control points
        if (mPoints.size() >= 2) {
            mPoints.get(0).set(mDoodleRect.left, mDoodleRect.top);
            mPoints.get(1).set(mDoodleRect.right, mDoodleRect.bottom);
        }
    }

    protected RectF updateRectOnChangeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        RectF drawingBounds = getWhiteboard().getParams().drawingBounds;
        PointF p = Utils.normalizeScreenPoint(x - oldX, y - oldY, drawingBounds);
        mCurrVector.x = p.x / mTotalScale;
        mCurrVector.y = p.y / mTotalScale;
        float degree = mTotalDegree % 360;

        //使用向量变换求的delta值
        mHorizontalVector.x = mDoodleRect.right - mDoodleRect.left;
        mHorizontalVector.y = 0;

        mVerticalVector.x = 0;
        mVerticalVector.y = mDoodleRect.bottom - mDoodleRect.top;

        Utils.mapRotateVector(mHorizontalVector, degree);
        Utils.mapRotateVector(mVerticalVector, degree);

        float deltaX = (float) Utils.vectorProjection(mCurrVector, mHorizontalVector);
        float deltaY = (float) Utils.vectorProjection(mCurrVector, mVerticalVector);

        switch (edge) {
            case IntersectionHelper.TOP_EDGE:
                float top = mDoodleRect.top + deltaY;
                if (top < mDoodleRect.bottom) {
                    mDoodleRect.set(mDoodleRect.left, top, mDoodleRect.right, mDoodleRect.bottom);
                }
                break;
            case IntersectionHelper.RIGHT_EDGE:
                float right = mDoodleRect.right + deltaX;
                if (right > mDoodleRect.left) {
                    mDoodleRect.set(mDoodleRect.left, mDoodleRect.top , right, mDoodleRect.bottom);
                }
                break;
            case IntersectionHelper.BOTTOM_EDGE:
                float bottom = mDoodleRect.bottom + deltaY;
                if (bottom > mDoodleRect.top) {
                    mDoodleRect.set(mDoodleRect.left, mDoodleRect.top, mDoodleRect.right, bottom);
                }
                break;
            case IntersectionHelper.LEFT_EDGE:
                float left = mDoodleRect.left + deltaX;
                if (left < mDoodleRect.right) {
                    mDoodleRect.set(left, mDoodleRect.top, mDoodleRect.right, mDoodleRect.bottom);
                }
                break;
        }

        return mDoodleRect;
    }

}