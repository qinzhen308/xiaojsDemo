package com.benyuan.xiaojs.ui.classroom.whiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.DrawingHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.GeometryShape;
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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

public class Oval extends TwoDimensionalShape {

    public Oval(WhiteBoard whiteBoard, Paint paint) {
        super(whiteBoard, GeometryShape.OVAL);
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

        //map point
        /*PointF p = Utils.setBlackboardToScreen(mStartX, mStartY, mWhiteboard.getBlackParams());
        float x1 = p.x;
        float y1 = p.y;
        p = Utils.setBlackboardToScreen(mEndX, mEndY, mWhiteboard.getBlackParams());
        float x2 = p.x;
        float y2 = p.y;

        float temp1 = Math.min(x1, x2);
        float temp2 = Math.max(x1, x2);
        x1 = temp1;
        x2 = temp2;

        temp1 = Math.min(y1, y2);
        temp2 = Math.max(y1, y2);
        y1 = temp1;
        y2 = temp2;

        mRect.set(x1, y1, x2, y2);
        //draw oval
        canvas.drawOval(mRect, getPaint());*/

        float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
        float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);

        float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
        float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);

        mRect.set(x1, y1, x2, y2);

        mNormalizedPath.reset();
        mNormalizedPath.addOval(mRect, Path.Direction.CCW);
        mNormalizedPath.transform(mDrawingMatrix);
        canvas.drawPath(mNormalizedPath, getPaint());

        canvas.restore();
    }

    @Override
    public void drawBorder(Canvas canvas) {
        if (mPoints.size() >  1) {
            DrawingHelper.drawBorder(canvas,  getWhiteboard().getBlackParams(),
                    mPoints.get(0), mPoints.get(1), mPaint.getStrokeWidth());
        }
    }

    @Override
    public Path getOriginalPath() {
        WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
        PointF p = Utils.mapDoodlePointToScreen(mRect.left, mRect.top, params.drawingBounds);
        int x1 = (int)p.x;
        int y1 = (int)p.y;

        p = Utils.mapDoodlePointToScreen(mRect.right, mRect.bottom, params.drawingBounds);
        int x2 = (int)p.x;
        int y2 = (int)p.y;

        mRect.set(x1, y1, x2, y2);
        mOriginalPath.addOval(mRect, Path.Direction.CCW);
        return mOriginalPath;
    }

    @Override
    public void changeArea(float downX, float downY) {
    }

    @Override
    public void move(float deltaX, float deltaY) {
        WhiteBoard.BlackParams params = getWhiteboard().getBlackParams();
        PointF p = Utils.normalizeScreenPoint(deltaX, deltaY, params.drawingBounds);

        PointF dp = mPoints.get(0);
        PointF up = mPoints.get(1);
        dp.set(dp.x + p.x, dp.y + p.y);
        up.set(up.x + p.x, up.y + p.y);
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        return super.checkRegionPressedArea(x, y);
    }

    @Override
    public boolean isSelected(float x, float y) {
        //return Utils.intersect(x, y , this);
        WhiteBoard.BlackParams params = getWhiteboard().getBlackParams();
        PointF dp = mPoints.get(0);
        PointF up = mPoints.get(1);
        return Utils.checkOvalFramePress(x, y, dp, up, params.drawingBounds);
    }
}
