package com.benyuan.xiaojs.ui.classroom.whiteboard.core;
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
 * Date:2016/12/6
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;

public class DrawingHelper {
    private static int DEFAULT_DASH_WIDTH = 5;
    private static int DEFAULT_COLOR = 0XFF0076FF;
    private static Paint mDashPaint;

    public static void drawBorder(Canvas canvas, WhiteBoard.BlackParams params, PointF p1, PointF p2, float doodleWidth) {
        if (mDashPaint == null) {
            mDashPaint = buildDashPaint();
        }

        float padding = Math.max(0 ,doodleWidth * params.scale / 2 + DEFAULT_DASH_WIDTH / 2);

        PointF p = Utils.mapDoodlePointToScreen(p1.x, p1.y, params.drawingBounds);
        float x1 = p.x;
        float y1 = p.y;
        p = Utils.mapDoodlePointToScreen(p2.x, p2.y, params.drawingBounds);
        float x2 = p.x;
        float y2 = p.y;

        float temp1 = Math.min(x1, x2);
        float temp2 = Math.max(x1, x2);
        x1 = temp1 - padding;
        x2 = temp2 + padding;

        temp1 = Math.min(y1, y2);
        temp2 = Math.max(y1, y2);
        y1 = temp1 - padding;
        y2 = temp2 + padding;
        //draw rect
        canvas.drawRect(x1, y1, x2, y2, mDashPaint);
    }

    public static void drawBorder(Canvas canvas, WhiteBoard.BlackParams params, RectF rectF, float doodleWidth) {
        if (mDashPaint == null) {
            mDashPaint = buildDashPaint();
        }

        float padding = Math.max(0 ,doodleWidth * params.scale / 2 + DEFAULT_DASH_WIDTH / 2);

        PointF p = Utils.mapDoodlePointToScreen(rectF.left, rectF.top, params.drawingBounds);
        float x1 = p.x;
        float y1 = p.y;
        p = Utils.mapDoodlePointToScreen(rectF.right, rectF.bottom, params.drawingBounds);
        float x2 = p.x;
        float y2 = p.y;

        float temp1 = Math.min(x1, x2);
        float temp2 = Math.max(x1, x2);
        x1 = temp1 - padding;
        x2 = temp2 + padding;

        temp1 = Math.min(y1, y2);
        temp2 = Math.max(y1, y2);
        y1 = temp1 - padding;
        y2 = temp2 + padding;
        //draw rect
        canvas.drawRect(x1, y1, x2, y2, mDashPaint);
    }

    public static void drawTextBorder(Canvas canvas, WhiteBoard.BlackParams params, PointF p1, PointF p2, float doodleWidth) {
        if (mDashPaint == null) {
            mDashPaint = buildDashPaint();
        }

        float padding = Math.max(0 ,doodleWidth * params.scale / 2 + DEFAULT_DASH_WIDTH / 2);
        padding += WhiteBoard.TEXT_BORDER_PADDING;

        PointF p = Utils.mapDoodlePointToScreen(p1.x, p1.y, params.drawingBounds);
        float x1 = p.x;
        float y1 = p.y;
        p = Utils.mapDoodlePointToScreen(p2.x, p2.y, params.drawingBounds);
        float x2 = p.x;
        float y2 = p.y;

        float temp1 = Math.min(x1, x2);
        float temp2 = Math.max(x1, x2);
        x1 = temp1 - padding;
        x2 = temp2 + padding;

        temp1 = Math.min(y1, y2);
        temp2 = Math.max(y1, y2);
        y1 = temp1 - padding;
        y2 = temp2 + padding;
        //draw rect
        canvas.drawRect(x1, y1, x2, y2, mDashPaint);
    }

    private static Paint buildDashPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(DEFAULT_DASH_WIDTH);
        p.setColor(DEFAULT_COLOR);
        PathEffect blackEffects = new DashPathEffect(new float[] { 20, 20}, 0);
        p.setPathEffect(blackEffects);
        return p;
    }

}
