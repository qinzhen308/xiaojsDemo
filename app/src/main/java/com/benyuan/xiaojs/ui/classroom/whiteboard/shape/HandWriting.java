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
import android.util.Log;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;


public class HandWriting extends Doodle {
    private Path mTransformPath;

    public HandWriting(WhiteBoard whiteBoard) {
        super(whiteBoard, Doodle.STYLE_HAND_WRITING);
    }

    public HandWriting(WhiteBoard whiteBoard, Paint paint, float x, float y) {
        this(whiteBoard);
        setPaint(paint);
        setFirstPoint(x, y);

        init();
    }

    private void init() {
        mTransformPath = new Path();
    }

    private void setFirstPoint(float x, float y) {
        addControlPoint(x, y);
        mDrawingPath.moveTo(x, y);

        PointF p = Utils.mapDoodlePointToScreen(x, y, getWhiteboard().getBlackParams().drawingBounds);
        mOriginalPath.moveTo(p.x, p.y);
    }

    /**
     * Path.quadTo(): If no moveTo() call has been made for
     * this contour, the first point is automatically set to (0,0)
     * @param point
     */
    @Override
    public void addControlPoint(PointF point) {
        if (!mPoints.isEmpty()) {
            PointF last = mPoints.lastElement();
            mDrawingPath.quadTo(last.x, last.y, (last.x + point.x) / 2, (last.y + point.y) / 2);
            mDrawingPath.computeBounds(mDoodleRect, true);
        }
        mPoints.add(point);
    }

    @Override
    public void setDrawingMatrix(Matrix matrix) {
        super.setDrawingMatrix(matrix);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        mTransformPath.set(mDrawingPath);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mTransformPath.transform(mDrawingMatrix);
        canvas.drawPath(mTransformPath, getPaint());
        canvas.restore();
    }

    @Override
    public Path getOriginalPath() {
        mOriginalPath.reset();
        mOriginalPath.set(mDrawingPath);
        mOriginalPath.transform(mDrawingMatrix);
        mOriginalPath.transform(mDisplayMatrix);
        return mOriginalPath;
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mRect.set(mDoodleRect);
            int corner = Utils.isPressedCorner(p.x, p.y, mRect, matrix);
            if (corner != Utils.RECT_NO_SELECTED) {
                return corner;
            } else {
                return Utils.checkRectPressed(p.x, p.y, mRect, matrix);
            }
        }

        return 0;
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            long s = System.currentTimeMillis();
            boolean intersect = Utils.intersect(x, y , this);
            Log.i("aaa", "take=" + (System.currentTimeMillis() - s)+"   intersect="+intersect);
            return intersect;
        }

        return false;
    }

    @Override
    public RectF getDoodleTransformRect() {
        mTransformPath.computeBounds(mRect, true);
        mDisplayMatrix.mapRect(mRect);
        return mRect;
    }
}
