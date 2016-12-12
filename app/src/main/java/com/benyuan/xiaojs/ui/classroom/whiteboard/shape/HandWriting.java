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

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.DrawingHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;


public class HandWriting extends Doodle {
    private Path mDrawingPath;

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
        mDrawingPath = new Path();
    }

    private void setFirstPoint(float x, float y) {
        addControlPoint(x, y);
        mNormalizedPath.moveTo(x, y);

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
            mNormalizedPath.quadTo(last.x, last.y, (last.x + point.x) / 2, (last.y + point.y) / 2);

            WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
            PointF lastP = Utils.mapDoodlePointToScreen(last.x, last.y, params.drawingBounds);
            PointF currP = Utils.mapDoodlePointToScreen(point.x, point.y, params.drawingBounds);
            mOriginalPath.quadTo(lastP.x, lastP.y, (lastP.x + currP.x) / 2, (lastP.y + currP.y) / 2);
        }
        mPoints.add(point);
    }

    /**
     * Gets the drawing path from the normalized doodle path.
     */
    public void getDrawingPath(Matrix matrix, Path path) {
        path.set(mNormalizedPath);
        if (matrix != null) {
            path.transform(matrix);
        }
    }

    /**
     * Checks if the constructed doodle path is in (0, 0, 1, 1) bounds.
     */
    public boolean inBounds() {
        RectF r = new RectF();
        return r.intersects(0, 0, 1, 1);
    }

    @Override
    public void setDrawingMatrix(Matrix matrix) {
        super.setDrawingMatrix(matrix);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        mDrawingPath.set(mNormalizedPath);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());
        canvas.restore();
    }

    @Override
    public void drawBorder(Canvas canvas) {
        mNormalizedPath.computeBounds(mRect, true);
        super.drawBorder(canvas);
    }

    @Override
    public Path getOriginalPath() {
        mOriginalPath.reset();
        mOriginalPath.set(mNormalizedPath);
        mOriginalPath.transform(mDrawingMatrix);
        mOriginalPath.transform(mDisplayMatrix);
        return mOriginalPath;
    }

    @Override
    public void changeArea(float downX, float downY) {

    }

    @Override
    public void move(float deltaX, float deltaY) {
        WhiteBoard.BlackParams params = getWhiteboard().getBlackParams();
        PointF normP = Utils.normalizeScreenPoint(deltaX, deltaY, params.drawingBounds);

        //update all points
        if (mPoints != null && !mPoints.isEmpty()) {
            for (PointF p : mPoints) {
                p.set(p.x + normP.x, p.y + normP.y);
            }
        }

        updatePath();
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT) {

            mNormalizedPath.computeBounds(mRect, true);
            int corner = Utils.isPressedCorner(x, y, mRect, mDrawingMatrix, mDisplayMatrix);
            if (corner != Utils.RECT_NO_SELECTED) {
                return corner;
            } else {
                return Utils.checkRectPressed(x, y, mRect, mDrawingMatrix, mDisplayMatrix);
            }
        }

        return 0;
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return Utils.intersect(x, y , this);
        }

        return false;
    }

    private void updatePath() {
        if (mPoints.size() > 1) {
            mNormalizedPath.reset();
            mOriginalPath.reset();
            WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();

            PointF last = mPoints.firstElement();

            mNormalizedPath.moveTo(last.x, last.y);
            PointF p = Utils.mapDoodlePointToScreen(last.x, last.y, getWhiteboard().getBlackParams().drawingBounds);
            mOriginalPath.moveTo(p.x, p.y);

            for (int i = 1; i < mPoints.size(); i++) {
                PointF point = mPoints.get(i);

                mNormalizedPath.quadTo(last.x, last.y, (last.x + point.x) / 2, (last.y + point.y) / 2);

                PointF lastP = Utils.mapDoodlePointToScreen(last.x, last.y, params.drawingBounds);
                PointF currP = Utils.mapDoodlePointToScreen(point.x, point.y, params.drawingBounds);
                mOriginalPath.quadTo(lastP.x, lastP.y, (lastP.x + currP.x) / 2, (lastP.y + currP.y) / 2);

                last = point;
            }
        }
    }

    @Override
    protected void computeCenterPoint(PointF rectP1, PointF rectP2) {
        mNormalizedPath.computeBounds(mRect, true);
        float centerX = (mRect.left + mRect.right) / 2.0f;
        float centerY = (mRect.top + mRect.bottom) / 2.0f;
        mRectCenter[0] = centerX;
        mRectCenter[1] = centerY;
        mDrawingMatrix.mapPoints(mRectCenter);
    }
}
