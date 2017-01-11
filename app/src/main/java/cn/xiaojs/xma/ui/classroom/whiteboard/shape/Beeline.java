package cn.xiaojs.xma.ui.classroom.whiteboard.shape;
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

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.LineSegment;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;

public class Beeline extends TwoDimensionalShape {
    /**
     * 如果直线的第一个控制点坐标值比第二个控制点坐标值小，则表示该直线是正向
     */
    private static final int FORWARD = 0;
    private static final int REVERSE = 1;

    private LineSegment mLineSegment;
    private int mOrientation = FORWARD;

    private Beeline(Whiteboard whiteboard) {
        super(whiteboard, GeometryShape.BEELINE);
    }

    public Beeline(Whiteboard whiteboard, Paint paint) {
        this(whiteboard);
        setPaint(paint);

        init();
    }

    public Beeline(Whiteboard whiteboard, Paint paint, String doodleId) {
        this(whiteboard);
        setDoodleId(doodleId);
        setPaint(paint);

        init();
    }

    private void init() {
        mLineSegment = new LineSegment();
    }

    @Override
    protected int initialCapacity() {
        return 2;
    }

    @Override
    public void addControlPoint(PointF point) {
        if (mPoints.isEmpty()) {
            mPoints.add(point);
        } else if (mPoints.size() == 1) {
            mPoints.add(point);
        } else if(mPoints.size() >= 2){
            mPoints.set(1, point);
        }

        if (mPoints.size() > 1) {
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);
            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mOrientation = mPoints.get(1).x >=  mPoints.get(0).x && mPoints.get(1).y >=  mPoints.get(0).y ? FORWARD : REVERSE;
            mDoodleRect.set(x1, y1, x2, y2);
        }
    }

    @Override
    public Path getScreenPath() {
        return null;
    }

    public LineSegment getLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), mPoints.get(1), matrix, mLineSegment);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mDrawingPath.reset();

        mDrawingPath.moveTo(mPoints.get(0).x, mPoints.get(0).y);
        mDrawingPath.lineTo(mPoints.get(1).x, mPoints.get(1).y);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);

        canvas.drawPath(mDrawingPath, getPaint());

        canvas.restore();
    }

    @Override
    protected void changeShape(float touchX, float touchY) {

    }

    @Override
    protected double computeArea() {
        return 0;
    }

    @Override
    protected double computeEdgeLength() {
        return 0;
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y , this);
        }

        return false;

    }

    @Override
    public void updatePointByRect() {
        ////update control points
        if (isForward()) {
            mPoints.get(0).set(mDoodleRect.left, mDoodleRect.top);
            mPoints.get(1).set(mDoodleRect.right, mDoodleRect.bottom);
        } else {
            mPoints.get(0).set(mDoodleRect.right, mDoodleRect.top);
            mPoints.get(1).set(mDoodleRect.left, mDoodleRect.bottom);
        }
    }

    public boolean isForward() {
        return mOrientation == FORWARD;
    }

}
