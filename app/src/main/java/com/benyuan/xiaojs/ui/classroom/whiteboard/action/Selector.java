package com.benyuan.xiaojs.ui.classroom.whiteboard.action;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
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
    private final static int DEFAULT_DASH_WIDTH = 5;

    private final static int DEFAULT_COLOR = Color.BLACK;

    private Paint mSelectingBgPaint;
    private Paint mSelectingDashPaint;

    private Region mRegion;
    private Region mSelectorRegion;
    private int mSelectCount;

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
        PathEffect blackEffects = new DashPathEffect(new float[]{10, 10}, 0);
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
        if (mPoints.isEmpty()) {
            mPoints.add(point);
        } else if (mPoints.size() == 1) {
            mPoints.add(point);
        } else if (mPoints.size() >= 2) {
            mPoints.set(1, point);
        }
    }

    @Override
    public void reset() {
        if (mPoints != null) {
            mPoints.clear();
        }

        mSelectCount = 0;

        mRect.set(0, 0, 0, 0);
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                d.setState(Doodle.STATE_IDLE);
            }
        }

        setState(Doodle.STATE_IDLE);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        if (mState == STATE_DRAWING) {
            canvas.save();
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);

            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mRect.set(x1, y1, x2, y2);
            mDrawingPath.reset();
            mDrawingPath.addRect(mRect, Path.Direction.CCW);
            mDrawingPath.transform(mDrawingMatrix);

            canvas.drawPath(mDrawingPath, mSelectingBgPaint);
            canvas.drawPath(mDrawingPath, mSelectingDashPaint);

            canvas.restore();
        }
    }

    @Override
    public void drawBorder(Canvas canvas) {
        try {
            float padding = getWhiteboard().getBlackParams().paintStrokeWidth / 2.0f;
            mBorderRect.set(mRect.left - padding, mRect.top - padding, mRect.right + padding, mRect.bottom + padding);
            Log.i("aaa", "draw border="+mRect);
            mBorderNormalizedPath.reset();
            mBorderNormalizedPath.addRect(mBorderRect, Path.Direction.CCW);
            mBorderNormalizedPath.transform(mTransformMatrix);
            canvas.drawPath(mBorderNormalizedPath, mBorderPaint);
        } catch (Exception e) {

        }
    }

    @Override
    public void move(float deltaX, float deltaY) {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            int count = 0;
            for (Doodle d : allDoodles) {
                if (d.getState() == Doodle.STATE_EDIT) {
                    count++;
                    d.move(deltaX, deltaY);
                }
            }

            if (count > 0) {
                mRect.offset(deltaX, deltaY);
            }
        }
    }

    @Override
    public boolean isSelected(float x, float y) {
        return false;
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        return mRect.contains(x, y) ? Utils.RECT_BODY : Utils.RECT_NO_SELECTED;
    }

    @Override
    public Path getOriginalPath() {
        return null;
    }

    public int checkIntersect() {
        int intersectCount = 0;
        if (mPoints.size() > 1) {
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);

            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            intersectCount = intersect(x1, y1, x2, y2);
        }

        mSelectCount = intersectCount;
        return mSelectCount;
    }

    public int checkIntersect(float x, float y) {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        int intersectCount = 0;
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (Utils.intersect(x, y, d)) {
                    intersectCount++;
                    d.setState(STATE_EDIT);
                    mRect.set(0, 0, 0, 0);
                    updateRect(d);
                    break;
                }
            }
        }

        if (intersectCount > 0) {
            setState(STATE_EDIT);
        }
        mSelectCount = intersectCount;
        return mSelectCount;
    }

    /**
     * 返回是否有相交
	 * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public int intersect(float x1, float y1, float x2, float y2) {
        //map points
        WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
        PointF p = Utils.mapDoodlePointToScreen(x1, y1, params.drawingBounds);
        x1 = p.x;
        y1 = p.y;

        p = Utils.mapDoodlePointToScreen(x2, y2, params.drawingBounds);
        x2 = p.x;
        y2 = p.y;

        mRect.set(x1, y1, x2, y2);
        mSelectorRegion.set((int) x1, (int) y1, (int) x2, (int) y2);

        boolean intersect = false;
        int intersectCount = 0;
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        long s = System.currentTimeMillis();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (d instanceof Beeline) {
                    LineSegment beeLineSeg = ((Beeline) d).getLineSegment();
                    intersect = Utils.intersect(mRect, beeLineSeg);
                    if (intersect) {
                        intersectCount++;
                        d.setState(Doodle.STATE_EDIT);
                    }
                } else if (d instanceof Triangle) {
                    LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments();
                    for (LineSegment lineSegment : beeLineSeg) {
                        intersect = Utils.intersect(mRect, lineSegment);
                        if (intersect) {
                            intersectCount++;
                            d.setState(Doodle.STATE_EDIT);
                            break;
                        }
                    }
                } else {
                    Path originalPath = d.getOriginalPath();
                    if (originalPath == null) {
                        continue;
                    }

                    intersect = mRegion.setPath(originalPath, mSelectorRegion);
                    if (intersect) {
                        intersectCount++;
                        d.setState(Doodle.STATE_EDIT);
                    }
                }
            }
        }

        Log.i("aaa", "intersect take="+(System.currentTimeMillis() - s)+"   doodle size="+ allDoodles.size());
        if (intersectCount > 0) {
            mRect.set(0, 0, 0, 0);
            for (Doodle d : allDoodles) {
                if (d.getState() == STATE_EDIT) {
                    updateRect(d);
                }
            }
            setState(STATE_EDIT);
        }

        return intersectCount;
    }

    public void updateRect(Doodle doodle) {
        RectF rect = doodle.getDoodleTransformRect();
        if (mRect.isEmpty()) {
            mRect.set(rect);
        } else {
            if (rect.left < mRect.left) {
                mRect.set(rect.left, mRect.top, mRect.right, mRect.bottom);
            }

            if (rect.top < mRect.top) {
                mRect.set(mRect.left, rect.top, mRect.right, mRect.bottom);
            }

            if (rect.right > mRect.right) {
                mRect.set(mRect.left, mRect.top, rect.right, mRect.bottom);
            }

            if (rect.bottom > mRect.bottom) {
                mRect.set(mRect.left, mRect.top, mRect.right, rect.bottom);
            }
        }

        computeCenterPoint(null, null);
        Log.i("aaa", "updateRect Rect="+mRect);

    }

    @Override
    protected void computeCenterPoint(PointF rectP1, PointF rectP2) {
        float centerX = (mRect.left + mRect.right) / 2.0f;
        float centerY = (mRect.top + mRect.bottom) / 2.0f;
        mRectCenter[0] = centerX;
        mRectCenter[1] = centerY;
        mDrawingMatrix.mapPoints(mRectCenter);
    }

    public int getSelectCount() {
        return mSelectCount;
    }

    public void updateDoodleColor(int color) {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (d.getState() == STATE_EDIT) {
                    d.getPaint().setColor(color);
                }
            }
        }
    }

}
