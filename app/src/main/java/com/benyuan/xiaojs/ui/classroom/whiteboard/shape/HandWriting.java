package com.benyuan.xiaojs.ui.classroom.whiteboard.shape;
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

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;


public class HandWriting extends Doodle {
    private Path mTransformPath;

    public HandWriting(WhiteBoard whiteBoard) {
        super(whiteBoard, Doodle.STYLE_HAND_WRITING);
    }

    public HandWriting(WhiteBoard whiteBoard, Paint paint, float x, float y) {
        this(whiteBoard);
        setPaint(paint);
        setFirstPoint(x, y);

        init();
    }

    private void init() {
        mTransformPath = new Path();
    }

    private void setFirstPoint(float x, float y) {
        addControlPoint(x, y);
        mDrawingPath.moveTo(x, y);

        PointF p = Utils.mapDoodlePointToScreen(x, y, getWhiteboard().getBlackParams().drawingBounds);
        mOriginalPath.moveTo(p.x, p.y);
    }

    /**
     * Path.quadTo(): If no moveTo() call has been made for
     * this contour, the first point is automatically set to (0,0)
     * @param point
     */
    @Override
    public void addControlPoint(PointF point) {
        if (!mPoints.isEmpty()) {
            PointF last = mPoints.lastElement();
            mDrawingPath.quadTo(last.x, last.y, (last.x + point.x) / 2, (last.y + point.y) / 2);
        }
        mPoints.add(point);
    }

    @Override
    public void setDrawingMatrix(Matrix matrix) {
        super.setDrawingMatrix(matrix);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        mTransformPath.set(mDrawingPath);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mTransformPath.transform(mDrawingMatrix);
        canvas.drawPath(mTransformPath, getPaint());
        canvas.restore();
    }

    @Override
    public void drawBorder(Canvas canvas) {
        mDrawingPath.computeBounds(mRect, true);
        super.drawBorder(canvas);
    }

    @Override
    public Path getOriginalPath() {
        mOriginalPath.reset();
        mOriginalPath.set(mDrawingPath);
        mOriginalPath.transform(mDrawingMatrix);
        mOriginalPath.transform(mDisplayMatrix);
        return mOriginalPath;
    }

    @Override
    public void changeArea(float downX, float downY) {

    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mDrawingPath.computeBounds(mRect, true);
            int corner = Utils.isPressedCorner(p.x, p.y, mRect, matrix);
            if (corner != Utils.RECT_NO_SELECTED) {
                return corner;
            } else {
                return Utils.checkRectPressed(p.x, p.y, mRect, matrix);
            }
        }

        return 0;
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            long s = System.currentTimeMillis();
            boolean intersect = Utils.intersect(x, y , this);
            Log.i("aaa", "take=" + (System.currentTimeMillis() - s)+"   intersect="+intersect);
            return intersect;
        }

        return false;
    }

    @Override
    public void scale(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mDrawingPath.computeBounds(mRect, true);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float scale = Utils.calcRectScale(oldX, oldY, x, y, mRect, matrix);
            computeCenterPoint(null, null);

            mXTotalScale = mXTotalScale * scale;
            mYTotalScale = mYTotalScale * scale;
            mTransformMatrix.postScale(scale, scale, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public void rotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mDrawingPath.computeBounds(mRect, true);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float degree = Utils.calcRectDegrees(oldX, oldY, x, y, mRect, matrix);
            computeCenterPoint(null, null);

            mTotalDegree += degree;
            mTransformMatrix.postRotate(degree, mRectCenter[0], mRectCenter[1]);
        }
    }

    public void scaleAndRotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mDrawingPath.computeBounds(mRect, true);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float[] arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mRect, matrix);
            float scale = arr[0];
            float degree = arr[1];

            computeCenterPoint(null, null);

            mTotalDegree += degree;
            mXTotalScale = mXTotalScale * scale;
            mYTotalScale = mYTotalScale * scale;

            mTransformMatrix.postScale(scale, scale, mRectCenter[0], mRectCenter[1]);
            mTransformMatrix.postRotate(degree, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    protected void computeCenterPoint(PointF rectP1, PointF rectP2) {
        mDrawingPath.computeBounds(mRect, true);
        float centerX = (mRect.left + mRect.right) / 2.0f;
        float centerY = (mRect.top + mRect.bottom) / 2.0f;
        mRectCenter[0] = centerX;
        mRectCenter[1] = centerY;
        mDrawingMatrix.mapPoints(mRectCenter);
    }

    @Override
    public RectF getDoodleTransformRect() {
        mTransformPath.computeBounds(mRect, true);
        mDisplayMatrix.mapRect(mRect);
        return mRect;
    }
}
