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
 * Date:2016/12/7
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.GeometryShape;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.IntersectionHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.LineSegment;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.TwoDimensionalShape;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;

import java.util.Vector;

public class Triangle extends TwoDimensionalShape {
    private Vector<PointF> mTriangleCoordinates;
    private LineSegment[] mLineSegments;

    protected Triangle(WhiteBoard whiteBoard) {
        super(whiteBoard, GeometryShape.TRIANGLE);
    }

    public Triangle(WhiteBoard whiteBoard, Paint paint) {
        this(whiteBoard);
        setPaint(paint);

        init();
    }

    private void init() {
        mTriangleCoordinates = new Vector<PointF>(3);
        mLineSegments = new LineSegment[3];

        //fill empty lineSegment
        mLineSegments[0] = new LineSegment();
        mLineSegments[1] = new LineSegment();
        mLineSegments[2] = new LineSegment();
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

            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);
            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mDoodleRect.set(x1, y1, x2, y2);
        }

        updateTriangleCoordinates();
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
    public Path getScreenPath() {
        return null;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        if (mTriangleCoordinates.size() == 3) {
            mDrawingPath.reset();

            mDrawingPath.moveTo(mTriangleCoordinates.get(0).x, mTriangleCoordinates.get(0).y);
            mDrawingPath.lineTo(mTriangleCoordinates.get(1).x, mTriangleCoordinates.get(1).y);
            mDrawingPath.lineTo(mTriangleCoordinates.get(2).x, mTriangleCoordinates.get(2).y);
            mDrawingPath.lineTo(mTriangleCoordinates.get(0).x, mTriangleCoordinates.get(0).y);
            mDrawingMatrix.postConcat(mTransformMatrix);
            mDrawingPath.transform(mDrawingMatrix);

            canvas.drawPath(mDrawingPath, getPaint());
        }
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y, this);
        }

        return false;
    }

    private void updateTriangleCoordinates() {
        if (mPoints.size() >= 2) {
            mTriangleCoordinates.clear();
            mTriangleCoordinates.add(new PointF((mDoodleRect.left + mDoodleRect.right) / 2.0F, mDoodleRect.top));
            mTriangleCoordinates.add(new PointF(mDoodleRect.left, mDoodleRect.bottom));
            mTriangleCoordinates.add(new PointF(mDoodleRect.right, mDoodleRect.bottom));
        }
    }

    public LineSegment[] getLineSegments() {
        float stx;
        float sty;
        float edx;
        float edy;

        int size = mTriangleCoordinates.size();
        for (int i = 0; i < size; i++) {
            stx = mTriangleCoordinates.get(i).x;
            sty = mTriangleCoordinates.get(i).y;
            if (i == size - 1) {
                //last point,
                edx = mTriangleCoordinates.get(0).x;
                edy = mTriangleCoordinates.get(0).y;
            } else {
                edx = mTriangleCoordinates.get(i + 1).x;
                edy = mTriangleCoordinates.get(i + 1).y;
            }
            Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
            Utils.getLineSegment(stx, sty, edx, edy, matrix, mLineSegments[i]);
        }

        return mLineSegments;
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        super.changeAreaByEdge(oldX, oldY, x, y, edge);

        //update triangle points
        updateTriangleCoordinates();
    }
}
