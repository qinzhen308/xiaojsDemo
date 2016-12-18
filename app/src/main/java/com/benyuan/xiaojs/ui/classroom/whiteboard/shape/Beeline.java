package com.benyuan.xiaojs.ui.classroom.whiteboard.shape;

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

public class Beeline extends TwoDimensionalShape {
    /**
     * 如果直线的第一个控制点坐标值比第二个控制点坐标值小，则表示该直线是正向
     */
    private static final int FORWARD = 0;
    private static final int REVERSE = 1;

    private LineSegment mLineSegment;
    private int mOrientation = FORWARD;

    public Beeline(WhiteBoard whiteBoard) {
        super(whiteBoard, GeometryShape.BEELINE);
    }

    public Beeline(WhiteBoard whiteBoard, Paint paint) {
        this(whiteBoard);
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

            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);
            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mOrientation = mPoints.get(1).x >=  mPoints.get(0).x && mPoints.get(1).y >=  mPoints.get(0).y ? FORWARD : REVERSE;
            mDoodleRect.set(x1, y1, x2, y2);
        }
    }

    @Override
    public Path getOriginalPath() {
        WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
        float stx = mPoints.get(0).x;
        float sty = mPoints.get(0).y;
        float edx = mPoints.get(1).x;
        float edy = mPoints.get(1).y;

        PointF p = Utils.mapDoodlePointToScreen(stx, sty, params.drawingBounds);
        int x1 = (int)p.x;
        int y1 = (int)p.y;

        p = Utils.mapDoodlePointToScreen(edx, edy, params.drawingBounds);
        int x2 = (int)p.x;
        int y2 = (int)p.y;

        mOriginalPath.moveTo(x1, y1);
        mOriginalPath.lineTo(x2, y2);

        return mOriginalPath;
    }

    public LineSegment getLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), mPoints.get(1), matrix, mLineSegment);
    }

    @Override
    public void drawSelf(Canvas canvas) {
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
    public int checkRegionPressedArea(float x, float y) {
        return super.checkRegionPressedArea(x, y);
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y , this);
        }

        return false;

    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        updateRectOnChangeAreaByEdge(oldX, oldY, x, y, edge);
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
