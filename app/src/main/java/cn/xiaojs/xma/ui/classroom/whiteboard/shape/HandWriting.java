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
import android.graphics.RectF;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;

public class HandWriting extends Doodle {
    private Path mNormalizedPath;

    private HandWriting(Whiteboard whiteboard) {
        super(whiteboard, Doodle.STYLE_HAND_WRITING);
    }

    public HandWriting(Whiteboard whiteboard, Paint paint) {
        this(whiteboard);
        setPaint(paint);
        init();
    }

    public HandWriting(Whiteboard whiteboard, Paint paint, String doodleId) {
        this(whiteboard);
        setDoodleId(doodleId);
        setPaint(paint);
        init();
    }

    private void init() {
        mNormalizedPath = new Path();
    }

    private void setFirstPoint(float x, float y) {
        addControlPoint(x, y);
        mNormalizedPath.moveTo(x, y);
    }

    /**
     * Path.quadTo(): If no moveTo() call has been made for this contour, the first point is
     * automatically set to (0,0)
     */
    @Override
    public void addControlPoint(PointF point) {
        if (!mPoints.isEmpty()) {
            PointF last = mPoints.get(mPoints.size() - 1);
            mNormalizedPath.quadTo(last.x, last.y, (last.x + point.x) / 2, (last.y + point.y) / 2);
            mNormalizedPath.computeBounds(mDoodleRect, true);
        } else {
            mNormalizedPath.moveTo(point.x, point.y);
        }
        mPoints.add(point);
    }

    @Override
    public void setDrawingMatrix(Matrix matrix) {
        super.setDrawingMatrix(matrix);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        canvas.save();
        mDrawingPath.set(mNormalizedPath);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());
        canvas.restore();
    }

    @Override
    public Path getScreenPath() {
        mScreenPath.reset();
        mScreenPath.set(mNormalizedPath);
        mScreenPath.transform(mDrawingMatrix);
        mScreenPath.transform(mDisplayMatrix);
        return mScreenPath;
    }

    @Override
    public int onCheckPressedRegion(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mTransRect.set(mDoodleRect);
            int corner = IntersectionHelper.whichCornerPressed(p.x, p.y, mTransRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            long s = System.currentTimeMillis();
            boolean intersect = IntersectionHelper.intersect(x, y, this);
            return intersect;
        }

        return false;
    }

    @Override
    public RectF getDoodleScreenRect() {
        mDrawingPath.computeBounds(mTransRect, true);
        mDisplayMatrix.mapRect(mTransRect);
        return mTransRect;
    }
}
