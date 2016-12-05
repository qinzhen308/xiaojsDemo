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

public abstract class Doodle {
    public final static int SELECTION = 0;
    public final static int STYLE_HAND_WRITING = 1;
    public final static int STYLE_GEOMETRY = 2;
    public final static int STYLE_ERASER = 3;
    public final static int STYLE_TEXT = 4;

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

    private boolean mSelected = false;

    protected Doodle(WhiteBoard whiteBoard, int style) {
        mWhiteboard = whiteBoard;
        mStyle = style;
        int capacity = initialCapacity();
        if (capacity <= 0) {
            mPoints = new Vector<PointF>();
        } else {
            mPoints = new Vector<PointF>(capacity);
        }
        mDoodlePaths.add(this);

        initParams();
    }

    private void initParams () {
        mRect = new RectF();

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

    public PointF getDownPoint() {
        return mPoints.firstElement();
    }

    public PointF getUpPoint() {
        return mPoints.lastElement();
    }

    /**change the first point */
    public void changeDownPoint(PointF point) {
        if (mPoints != null && !mPoints.isEmpty()) {
            mPoints.firstElement().set(point.x, point.y);
        }
    }

    public void changeDownPoint(float x, float y) {
        if (mPoints != null && !mPoints.isEmpty()) {
            mPoints.firstElement().set(x, y);
        }
    }

    /**change the last point */
    public void changeUpPoint(PointF point) {
        if (mPoints != null && !mPoints.isEmpty()) {
            mPoints.lastElement().set(point.x, point.y);
        }
    }

    public void changeUpPoint(float x, float y) {
        if (mPoints != null && !mPoints.isEmpty()) {
            mPoints.lastElement().set(x, y);
        }
    }

    public void setDrawingMatrix(Matrix matrix) {
        mMatrix = matrix;
    }

    public abstract void move(float x, float y);

    public void rotate(float degree) {
        mDegree += degree;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public abstract Path getOriginalPath();

    public abstract void drawSelf(Canvas canvas);

}
