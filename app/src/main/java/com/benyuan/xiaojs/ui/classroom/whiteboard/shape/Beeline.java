package com.benyuan.xiaojs.ui.classroom.whiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
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
    private PointF mStartP;
    private PointF mEndP;
    private LineSegment mLineSegment;

    protected float mStartX;
    protected float mStartY;
    protected float mEndX;
    protected float mEndY;

    public Beeline(WhiteBoard whiteBoard) {
        super(whiteBoard);
    }

    public Beeline(WhiteBoard whiteBoard, Paint paint) {
        this(whiteBoard);
        setPaint(paint);

        init();
    }

    private void init() {
        mStartP = new PointF();
        mEndP = new PointF();
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
            mStartX = point.x;
            mStartY = point.y;
        } else if (mPoints.size() == 1) {
            mPoints.add(point);
            mEndX = point.x;
            mEndY = point.y;
        } else if(mPoints.size() >= 2){
            mPoints.set(1, point);
            mEndX = point.x;
            mEndY = point.y;
        }
    }

    @Override
    public Path getOriginalPath() {
        WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
        PointF p = Utils.mapDoodlePointToScreen(mStartX, mStartY, params.drawingBounds);
        int x1 = (int)p.x;
        int y1 = (int)p.y;

        p = Utils.mapDoodlePointToScreen(mEndX, mEndY, params.drawingBounds);
        int x2 = (int)p.x;
        int y2 = (int)p.y;

        mOriginalPath.moveTo(x1, y1);
        mOriginalPath.lineTo(x2, y2);

        return mOriginalPath;
    }

    public LineSegment getLineSegment(boolean original) {
        if (original) {
            WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
            PointF p = Utils.mapDoodlePointToScreen(mStartX, mStartY, params.drawingBounds);
            int x1 = (int)p.x;
            int y1 = (int)p.y;

            p = Utils.mapDoodlePointToScreen(mEndX, mEndY, params.drawingBounds);
            int x2 = (int)p.x;
            int y2 = (int)p.y;

            mLineSegment.point1.set(x1, y1);
            mLineSegment.point2.set(x2, y2);
        } else {
            mLineSegment.point1.set(mStartX, mStartY);
            mLineSegment.point2.set(mEndX, mEndY);
        }
        return mLineSegment;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        //method 1:not need to call setDrawingMatrix()
        /*PointF p = Utils.setBlackboardToScreen(mStartX, mStartY, mWhiteboard.getBlackParams());
        float x1 = p.x;
        float y1 = p.y;
        p = Utils.setBlackboardToScreen(mEndX, mEndY, mWhiteboard.getBlackParams());
        float x2 = p.x;
        float y2 = p.y;

        //draw line
        canvas.drawLine(x1, y1, x2, y2, getPaint());*/

        //method 2:need to call setDrawingMatrix()
        //mRect.set(mStartX, mStartY, mEndX, mEndY);
        mNormalizedPath.reset();

        mNormalizedPath.moveTo(mStartX, mStartY);
        mNormalizedPath.lineTo(mEndX, mEndY);
        mNormalizedPath.transform(mMatrix);

        canvas.drawPath(mNormalizedPath, getPaint());

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
}
