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

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;

import java.util.UUID;
import java.util.Vector;

public abstract class Doodle implements Action {
    public final static int SELECTION = 0;
    public final static int STYLE_HAND_WRITING = 1;
    public final static int STYLE_GEOMETRY = 2;
    public final static int STYLE_ERASER = 3;
    public final static int STYLE_TEXT = 4;

    public final static int STATE_IDLE = 0;
    public final static int STATE_DRAWING = 1;
    public final static int STATE_EDIT = 2;

    protected WhiteBoard mWhiteboard;
    protected Vector<PointF> mPoints;

    protected Paint mPaint;
    protected Paint mBorderPaint;
    protected Paint mControllerPaint;

    protected float mTotalDegree = 0;
    protected float mTotalScale = 1.0f;

    protected int mStyle;

    protected RectF mDoodleRect;
    protected RectF mTransRect;
    protected RectF mBorderRect;

    protected Path mDrawingPath;
    protected Path mBorderDrawingPath;
    protected Path mOriginalPath;

    protected float[] mRectCenter;

    protected Matrix mDrawingMatrix;
    protected Matrix mTransformMatrix;
    protected Matrix mDisplayMatrix;
    protected int mState = STATE_IDLE;

    private String mDoodleId;

    protected Doodle(WhiteBoard whiteBoard, int style) {
        mDoodleId = UUID.randomUUID().toString();
        mWhiteboard = whiteBoard;
        mStyle = style;
        mState = STATE_IDLE;

        int capacity = initialCapacity();
        if (capacity <= 0) {
            mPoints = new Vector<PointF>();
        } else {
            mPoints = new Vector<PointF>(capacity);
        }

        initParams();
    }

    private void initParams() {
        mTransRect = new RectF();
        mDoodleRect = new RectF();
        mBorderRect = new RectF();

        mDrawingMatrix = new Matrix();
        mTransformMatrix = new Matrix();
        mDisplayMatrix = new Matrix();

        mDrawingPath = new Path();
        mOriginalPath = new Path();
        mBorderDrawingPath = new Path();

        mRectCenter = new float[2];

        mBorderPaint = Utils.buildDashPaint();
        mControllerPaint = Utils.buildControllerPaint();
    }

    protected int initialCapacity() {
        return 0;
    }

    //==========================getter and setter==========================================

    public Paint getPaint() {
        return mPaint;
    }

    public Vector<PointF> getPoints() {
        return mPoints;
    }

    public int getStyle() {
        return mStyle;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public WhiteBoard getWhiteboard() {
        return mWhiteboard;
    }

    public float getTotalScale() {
        return mTotalScale;
    }

    public void setTotalScale(float totalScale) {
        mTotalScale = totalScale;
    }

    public float getDegree() {
        return mTotalDegree;
    }

    public RectF getRect() {
        return mTransRect;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public String getDoodleId() {
        return mDoodleId;
    }

    public void setDoodleId(String doodleId) {
        mDoodleId = doodleId;
    }

    //==========================getter and setter==========================================

    public void addControlPoint(PointF point) {
        mPoints.add(point);
    }

    public void addControlPoint(float x, float y) {
        addControlPoint(new PointF(x, y));
    }

    public PointF getFirstPoint() {
        return mPoints.firstElement();
    }

    public PointF getLastPoint() {
        return mPoints.lastElement();
    }

    public void setDrawingMatrix(Matrix matrix) {
        if (mDrawingMatrix != null) {
            mDrawingMatrix.set(matrix);
        } else {
            mDrawingMatrix = matrix;
        }
    }

    public void setDisplayMatrix(Matrix matrix) {
        mDisplayMatrix = matrix;
    }

    //==============================
    public abstract Path getOriginalPath();

    public abstract void drawSelf(Canvas canvas);

    public void reset() {

    }

    public void drawBorder(Canvas canvas) {
        if (mPoints.size() > 1 && !mDoodleRect.isEmpty()) {
            WhiteBoard.WhiteboardParams params = mWhiteboard.getParams();
            float dashW = WhiteboardConfigs.BORDER_DASH_WIDTH / params.scale;

            mBorderPaint.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH / params.scale);
            mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

            float paintStrokeWidth = mPaint != null ? mPaint.getStrokeWidth() : 0;
            float padding = (paintStrokeWidth + mBorderPaint.getStrokeWidth()) / 2;
            PointF p = Utils.normalizeScreenPoint(padding, padding, params.drawingBounds);
            float hPadding = p.x / mTotalScale * params.scale;
            float vPadding = p.y / mTotalScale * params.scale;
            mBorderRect.set(mDoodleRect.left - hPadding, mDoodleRect.top - vPadding, mDoodleRect.right + hPadding, mDoodleRect.bottom + vPadding);

            mBorderDrawingPath.reset();
            mBorderDrawingPath.addRect(mBorderRect, Path.Direction.CCW);
            mBorderDrawingPath.transform(mDrawingMatrix);
            canvas.drawPath(mBorderDrawingPath, mBorderPaint);

            //draw controller
            float radius = mControllerPaint.getStrokeWidth() / mTotalScale;
            p = Utils.normalizeScreenPoint(radius, radius, params.drawingBounds);
            mBorderRect.set(mDoodleRect.right - p.x, mDoodleRect.top - p.y, mDoodleRect.right + p.x, mDoodleRect.top + p.y);
            mBorderDrawingPath.reset();
            mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
            mBorderDrawingPath.transform(mDrawingMatrix);
            canvas.drawPath(mBorderDrawingPath, mControllerPaint);
        }
    }

    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT && mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            int corner = IntersectionHelper.isPressedCorner(p.x, p.y, mTransRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                int edge = IntersectionHelper.whichEdgePressed(p.x, p.y, mTransRect, matrix);
                if (edge != IntersectionHelper.RECT_NO_SELECTED) {
                    return edge;
                }

                return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    public abstract boolean isSelected(float x, float y);

    @Override
    public void move(float deltaX, float deltaY) {
        WhiteBoard.WhiteboardParams params = getWhiteboard().getParams();
        mTransformMatrix.postTranslate(deltaX / params.scale, deltaY / params.scale);
    }

    @Override
    public void scale(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float scale = Utils.calcRectScale(oldX, oldY, x, y, mTransRect, matrix);
            computeCenterPoint(mTransRect);

            mTotalScale = mTotalScale * scale;
            //mTransformMatrix.reset();
            mTransformMatrix.postScale(scale, scale, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public void rotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float degree = Utils.calcRectDegrees(oldX, oldY, x, y, mTransRect, matrix);
            computeCenterPoint(mTransRect);

            mTotalDegree += degree;
            //mTransformMatrix.reset();
            mTransformMatrix.postRotate(degree, mRectCenter[0], mRectCenter[1]);
        }
    }

    public void scaleAndRotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float[] arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mTransRect, matrix);
            float scale = arr[0];
            float degree = arr[1];

            computeCenterPoint(mTransRect);

            mTotalDegree += degree;
            mTotalScale = mTotalScale * scale;

            mTransformMatrix.postRotate(degree, mRectCenter[0], mRectCenter[1]);
            mTransformMatrix.postScale(scale, scale, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {

    }

    protected void computeCenterPoint(PointF rectP1, PointF rectP2) {
        float centerX = (rectP1.x + rectP2.x) / 2.0f;
        float centerY = (rectP1.y + rectP2.y) / 2.0f;
        mRectCenter[0] = centerX;
        mRectCenter[1] = centerY;
        mDrawingMatrix.mapPoints(mRectCenter);
    }

    protected void computeCenterPoint(RectF rect) {
        mRectCenter[0] = rect.centerX();
        mRectCenter[1] = rect.centerY();
        mDrawingMatrix.mapPoints(mRectCenter);
    }

    public RectF getDoodleTransformRect() {
        if (mPoints.size() > 1) {
            mDrawingPath.computeBounds(mTransRect, true);
        }

        mDisplayMatrix.mapRect(mTransRect);
        return mTransRect;
    }

    public RectF getDoodleRect() {
        return mDoodleRect;
    }

    public void scale(float scale, float px, float py) {
        mTotalScale = mTotalScale * scale;
        mTransformMatrix.postScale(scale, scale, px, py);
    }

    public void rotate(float degree, float px, float py) {
        mTotalDegree += degree;
        mTransformMatrix.postRotate(degree, px, py);
    }

    public void scaleRotateByPoint(float scale, float degree, float px, float py) {
        scale(scale, px, py);
        rotate(degree, px, py);
    }

}
