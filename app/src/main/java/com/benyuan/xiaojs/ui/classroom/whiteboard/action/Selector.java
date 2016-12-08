package com.benyuan.xiaojs.ui.classroom.whiteboard.action;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Region;
import android.util.Log;


import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.LineSegment;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Beeline;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Triangle;

import java.util.ArrayList;

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

public class Selector extends Doodle {
    public final static int SELECTING = 0;
    public final static int SELECTED = 1;

    private final static int DEFAULT_DASH_WIDTH = 5;

    private final static int DEFAULT_COLOR = Color.BLACK;

    private Paint mSelectingBgPaint;
    private Paint mSelectingDashPaint;
    private Paint mSelectedPaint;
    private int mSelectStatus = SELECTING;

    private Region mRegion;
    private Region mSelectorRegion;

    public Selector(WhiteBoard whiteBoard) {
        super(whiteBoard, SELECTION);

        init();
    }

    private void init() {
        mSelectingDashPaint = buildDashPaint();
        mSelectingBgPaint = buildDefaultPaint();
        mRegion = new Region();
        mSelectorRegion = new Region();
    }

    private Paint buildDashPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(DEFAULT_DASH_WIDTH);
        p.setColor(Color.WHITE);
        PathEffect blackEffects = new DashPathEffect(new float[] { 10, 10}, 0);
        p.setPathEffect(blackEffects);
        return p;
    }

    private Paint buildDefaultPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(DEFAULT_DASH_WIDTH);
        return p;
    }

    @Override
    protected int initialCapacity() {
        return 2;
    }

    @Override
    public void addControlPoint(PointF point) {
        //clamp to [0, 1]
        point.set(Utils.clamp(point.x, 0, 1), Utils.clamp(point.y, 0, 1));
        if (mPoints.size() < 2) {
            mPoints.add(point);
        } else {
            mPoints.set(1, point);
        }
    }

    @Override
    public void addControlPoint(float x, float y) {
        addControlPoint(new PointF(x, y));
    }

    @Override
    public void drawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
        float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);

        float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
        float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);

        mRect.set(x1, y1, x2, y2);
        mNormalizedPath.reset();
        mNormalizedPath.addRect(mRect, Path.Direction.CCW);
        mNormalizedPath.transform(mMatrix);
        if (mSelectStatus == SELECTING) {
            canvas.drawPath(mNormalizedPath, mSelectingBgPaint);
            canvas.drawPath(mNormalizedPath, mSelectingDashPaint);
        } else {
            canvas.drawPath(mNormalizedPath, mSelectedPaint);
        }


        intersect(x1, y1, x2, y2);
        canvas.restore();
    }

    @Override
    public void drawBorder(Canvas canvas) {

    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        return 0;
    }

    @Override
    public boolean isSelected(float x, float y) {
        return false;
    }

    @Override
    public void move(float x, float y) {

    }

    @Override
    public Path getOriginalPath() {
        return null;
    }

    public boolean intersect(float x1, float y1, float x2, float y2) {
        //map points
        WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
        PointF p = Utils.mapDoodlePointToScreen(x1, y1, params.drawingBounds);
        x1 = p.x;
        y1 = p.y;

        p = Utils.mapDoodlePointToScreen(x2, y2, params.drawingBounds);
        x2 = p.x;
        y2 = p.y;

        mRect.set(x1, y1, x2, y2);
        mSelectorRegion.set((int)x1, (int)y1, (int)x2, (int)y2);

        boolean intersect = false;
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (d instanceof Beeline) {
                    LineSegment beeLineSeg = ((Beeline)d).getLineSegment(true);
                    intersect = Utils.intersect(mRect, beeLineSeg);
                    Log.i("aaa", "======================intersect="+intersect);
                } else if (d instanceof Triangle) {
                    LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments(true);
                    for (LineSegment lineSegment : beeLineSeg) {
                        intersect = Utils.intersect(mRect, lineSegment);
                        if (intersect) {
                            d.setState(Doodle.STATE_EDIT);
                        }
                    }
                } else {
                    Path originalPath = d.getOriginalPath();
                    if (originalPath == null) {
                        continue;
                    }

                    intersect = mRegion.setPath(originalPath, mSelectorRegion);
                    if (intersect) {
                        d.setState(Doodle.STATE_EDIT);
                    }

                    Log.i("aaa", "======================intersect="+intersect);
                }
            }
        }

        return intersect;

    }
}
