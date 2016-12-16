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
import android.util.Log;

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
    protected Vector<Doodle> mDoodlePaths = new Vector<Doodle>();

    protected Vector<PointF> mPoints;

    protected Paint mPaint;
    protected Paint mBorderPaint;
    protected Paint mControllerPaint;

    protected float mTotalDegree = 0;
    protected float mYTotalScale = 1.0f;
    protected float mXTotalScale = 1.0f;

    protected int mStyle;

    protected Path mDrawingPath;
    protected RectF mDoodleRect;
    protected RectF mRect;
    protected Path mBorderNormalizedPath;
    protected RectF mBorderRect;

    protected float[] mRectCenter;

    protected Path mOriginalPath;
    protected Matrix mDrawingMatrix;
    protected Matrix mBorderDrawingMatrix;
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
        mDoodlePaths.add(this);

        initParams();
    }

    private void initParams() {
        mRect = new RectF();
        mDoodleRect = new RectF();
        mBorderRect = new RectF();
        mDrawingMatrix = new Matrix();
        mBorderDrawingMatrix = new Matrix();
        mTransformMatrix = new Matrix();
        mDisplayMatrix = new Matrix();

        mDrawingPath = new Path();
        mOriginalPath = new Path();

        mDrawingPath = new Path();
        mOriginalPath = new Path();
        mBorderNormalizedPath = new Path();

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
        return mYTotalScale;
    }

    public void setTotalScale(float totalScale) {
        mYTotalScale = totalScale;
    }

    public float getDegree() {
        return mTotalDegree;
    }

    public RectF getRect() {
        return mRect;
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

    public void merge(Doodle d) {
        if (d != this) {
            mDoodlePaths.add(d);
        }
    }

    public void deMerge(Doodle d) {
        if (d != this) {
            mDoodlePaths.remove(d);
        }
    }

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

        if (mBorderDrawingMatrix != null) {
            mBorderDrawingMatrix.set(matrix);
        } else {
            mBorderDrawingMatrix = matrix;
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
            WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
            float dashW = Utils.DEFAULT_DASH_WIDTH / params.scale;

            mBorderPaint.setStrokeWidth(Utils.DEFAULT_BORDER_WIDTH / params.scale);
            mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

            float paintStrokeWidth = mPaint != null ? mPaint.getStrokeWidth() : 0;
            float padding = (paintStrokeWidth + mBorderPaint.getStrokeWidth()) / 2;
            PointF p = Utils.normalizeScreenPoint(padding, padding, mWhiteboard.getBlackParams().drawingBounds);
            float hPadding = p.x / mYTotalScale * params.scale;
            float vPadding = p.y / mYTotalScale * params.scale;
            mBorderRect.set(mDoodleRect.left - hPadding, mDoodleRect.top - vPadding, mDoodleRect.right + hPadding, mDoodleRect.bottom + vPadding);

            mBorderNormalizedPath.reset();
            mBorderNormalizedPath.addRect(mBorderRect, Path.Direction.CCW);
            mBorderNormalizedPath.transform(mDrawingMatrix);
            canvas.drawPath(mBorderNormalizedPath, mBorderPaint);

            //draw controller
            float radius = mControllerPaint.getStrokeWidth() / mYTotalScale;
            p = Utils.normalizeScreenPoint(radius, radius, params.drawingBounds);
            mBorderRect.set(mDoodleRect.right - p.x, mDoodleRect.top - p.y, mDoodleRect.right + p.x, mDoodleRect.top + p.y);
            mBorderNormalizedPath.reset();
            mBorderNormalizedPath.addOval(mBorderRect, Path.Direction.CCW);
            mBorderNormalizedPath.transform(mDrawingMatrix);
            canvas.drawPath(mBorderNormalizedPath, mControllerPaint);
        }
    }

    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT && mPoints.size() > 1) {
            mRect.set(mDoodleRect);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            int corner = Utils.isPressedCorner(p.x, p.y, mRect, matrix);
            Log.i("aaa", "corner="+corner);
            if (corner != Utils.RECT_NO_SELECTED) {
                return corner;
            } else {
                return Utils.checkRectPressed(p.x, p.y, mRect, matrix);
            }
        }

        return Utils.RECT_NO_SELECTED;
    }

    public abstract boolean isSelected(float x, float y);

    @Override
    public void move(float deltaX, float deltaY) {
        WhiteBoard.BlackParams params = getWhiteboard().getBlackParams();
        mTransformMatrix.postTranslate(deltaX / params.scale, deltaY / params.scale);
    }

    @Override
    public void scale(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mRect.set(mDoodleRect);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float scale = Utils.calcRectScale(oldX, oldY, x, y, mRect, matrix);
            computeCenterPoint(mRect);

            mXTotalScale = mXTotalScale * scale;
            mYTotalScale = mYTotalScale * scale;
            //mTransformMatrix.reset();
            mTransformMatrix.postScale(scale, scale, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public void rotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mRect.set(mDoodleRect);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float degree = Utils.calcRectDegrees(oldX, oldY, x, y, mRect, matrix);
            computeCenterPoint(mRect);

            mTotalDegree += degree;
            //mTransformMatrix.reset();
            mTransformMatrix.postRotate(degree, mRectCenter[0], mRectCenter[1]);
        }
    }

    public void scaleAndRotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mRect.set(mDoodleRect);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, 0);
            float[] arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mRect, matrix);
            float scale = arr[0];
            float degree = arr[1];

            computeCenterPoint(mRect);

            mTotalDegree += degree;
            mXTotalScale = mXTotalScale * scale;
            mYTotalScale = mYTotalScale * scale;

            mTransformMatrix.postRotate(degree, mRectCenter[0], mRectCenter[1]);
            mTransformMatrix.postScale(scale, scale, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public void changeArea(float downX, float downY) {
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
            mDrawingPath.computeBounds(mRect, true);
        }

        mDisplayMatrix.mapRect(mRect);
        return mRect;
    }

    public Matrix getMapScreenMatrix() {
        return Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
    }

}