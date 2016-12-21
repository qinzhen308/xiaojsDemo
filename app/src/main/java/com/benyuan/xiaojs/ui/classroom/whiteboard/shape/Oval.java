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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.Whiteboard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.GeometryShape;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.IntersectionHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.TwoDimensionalShape;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;

public class Oval extends TwoDimensionalShape {

    public Oval(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.OVAL);
        setPaint(paint);
    }

    public Oval(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.OVAL);
        setDoodleId(doodleId);
        setPaint(paint);
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
    public void drawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mTransRect.set(mDoodleRect);

        mDrawingPath.reset();
        mDrawingPath.addOval(mTransRect, Path.Direction.CCW);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());

        canvas.restore();
    }

    @Override
    public Path getScreenPath() {
        mScreenPath.reset();
        mTransRect.set(mDoodleRect);
        mScreenPath.addRect(mTransRect, Path.Direction.CCW);
        mScreenPath.transform(mDrawingMatrix);
        mScreenPath.transform(mDisplayMatrix);
        return mScreenPath;
    }

    @Override
    public void scale(float oldX, float oldY, float x, float y) {
        super.scale(oldX, oldY, x, y);
    }

    @Override
    public int checkPressedRegion(float x, float y) {
        return super.checkPressedRegion(x, y);
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            return IntersectionHelper.checkOvalFramePress(p.x, p.y, mTransRect, matrix);
        }

        return false;
    }
}
