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
        mDrawingPath.set(mNormalizedPath);
        if (matrix != null) {
            mDrawingPath.transform(matrix);
        }
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        canvas.drawPath(mDrawingPath, getPaint());
        canvas.restore();
    }

    @Override
    public void move(float x, float y) {

    }

    @Override
    public Path getOriginalPath() {
        return mOriginalPath;
    }

}
