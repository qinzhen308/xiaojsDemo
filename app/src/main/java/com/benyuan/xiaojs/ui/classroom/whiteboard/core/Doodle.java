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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;

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

    protected float mDegree = 0;
    protected float mTotalScale = 1.0f;

    protected int mStyle;

    protected Path mNormalizedPath;
    protected RectF mRect;
    protected Path mOriginalPath;
    protected Matrix mMatrix;
    protected int mState = STATE_IDLE;

    protected float mOffsetX;
    protected float mOffsetY;

    protected Doodle(WhiteBoard whiteBoard, int style) {
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
        mMatrix = new Matrix();

        mNormalizedPath = new Path();
        mOriginalPath = new Path();
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
        return mDegree;
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
        if (mMatrix != null) {
            mMatrix.set(matrix);
        } else {
            mMatrix = matrix;
        }
    }


    //==============================
    public abstract Path getOriginalPath();

    public abstract void drawSelf(Canvas canvas);

    public abstract void drawBorder(Canvas canvas);

    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT && mPoints.size() > 1) {
            WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
            PointF dp = mPoints.get(0);
            PointF up = mPoints.get(1);
            int corner = Utils.isPressedCorner(x, y, dp, up, params.drawingBounds);
            if (corner != Utils.RECT_NO_SELECTED) {
                return corner;
            } else {
                return Utils.checkRectPressed(x, y, dp, up, params.drawingBounds);
            }
        }

        return Utils.RECT_NO_SELECTED;
    }

    public abstract boolean isSelected(float x, float y);

    @Override
    public void move(float x, float y) {

    }

    @Override
    public void scale(float oldX, float oldY, float x, float y) {

    }

    @Override
    public void rotate(float degree) {

    }

    @Override
    public void changeArea(float downX, float downY) {

    }

}
