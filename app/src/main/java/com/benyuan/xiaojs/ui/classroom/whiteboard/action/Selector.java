package com.benyuan.xiaojs.ui.classroom.whiteboard.action;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
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
    private float[] mDoodleRectCenter;

    public Selector(WhiteBoard whiteBoard) {
        super(whiteBoard, SELECTION);

        init();
    }

    private void init() {
        mSelectingDashPaint = buildDashPaint();
        mSelectingBgPaint = buildDefaultPaint();
        mRegion = new Region();
        mSelectorRegion = new Region();
        mDoodleRectCenter = new float[2];
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

        mDoodleRect.set(0, 0, 0, 0);
        mTotalScale = 1.0f;
        mTotalDegree = 0;
        mTransformMatrix.reset();
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
            if (mDoodleRect.isEmpty()) {
                return;
            }

            Log.i("aaa", "mDoodleRect="+mDoodleRect +"   mTransformMatrix="+mTransformMatrix);
            WhiteBoard.BlackParams params = mWhiteboard.getBlackParams();
            float padding = params.paintStrokeWidth / 2.0f;
            mBorderRect.set(mDoodleRect.left - padding, mDoodleRect.top - padding, mDoodleRect.right + padding, mDoodleRect.bottom + padding);
            mBorderNormalizedPath.reset();
            mBorderNormalizedPath.addRect(mBorderRect, Path.Direction.CCW);
            mBorderNormalizedPath.transform(mTransformMatrix);
            canvas.drawPath(mBorderNormalizedPath, mBorderPaint);

            //draw controller
            float radius = mControllerPaint.getStrokeWidth() / mTotalScale;
            mBorderRect.set(mDoodleRect.right - radius, mDoodleRect.top - radius, mDoodleRect.right + radius, mDoodleRect.top + radius);
            mBorderNormalizedPath.reset();
            mBorderNormalizedPath.addOval(mBorderRect, Path.Direction.CCW);
            mBorderNormalizedPath.transform(mTransformMatrix);
            canvas.drawPath(mBorderNormalizedPath, mControllerPaint);
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
                mTransformMatrix.postTranslate(deltaX, deltaY);
            }
        }
    }

    @Override
    public void scaleAndRotate(float oldX, float oldY, float x, float y) {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            int count = 0;
            for (Doodle d : allDoodles) {
                if (d.getState() == Doodle.STATE_EDIT) {
                    count++;
                    break;
                }
            }

            if (count > 0) {
                //Log.i("aaa", "mDoodleRect="+mDoodleRect);
                WhiteBoard.BlackParams params = getWhiteboard().getBlackParams();
                mRect.set(mDoodleRect);
                mTransformMatrix.mapRect(mRect);
                PointF p = Utils.mapScreenToDoodlePoint(mRect.left, mRect.top, params.drawingBounds);
                float left = p.x;
                float top = p.y;
                p = Utils.mapScreenToDoodlePoint(mRect.right, mRect.bottom, params.drawingBounds);
                float right = p.x;
                float bottom = p.y;

                mRect.set(left, top, right, bottom);
                Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
                float[] arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mRect, matrix);
                float scale = arr[0];
                float degree = arr[1];

                computeDoodleCenterPoint(mRect);

                for (Doodle d : allDoodles) {
                    if (d.getState() == Doodle.STATE_EDIT) {
                        d.scaleRotateByPoint(scale, degree, mDoodleRectCenter[0], mDoodleRectCenter[1]);
                    }
                }


                matrix = Utils.transformScreenMatrix(mTransformMatrix, null);
                arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mDoodleRect, matrix);
                scale = arr[0];
                degree = arr[1];
                computeCenterPoint(mDoodleRect);

                scaleRotateByPoint(scale, degree, mRectCenter[0], mRectCenter[1]);

            }
        }
    }

    @Override
    public boolean isSelected(float x, float y) {
        return false;
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mTransformMatrix, null, mRectCenter, mTotalDegree);
            int corner = Utils.isPressedCorner(p.x, p.y, mDoodleRect, matrix);
            if (corner != Utils.RECT_NO_SELECTED) {
                return corner;
            } else {
                return Utils.checkRectPressed(p.x, p.y, mDoodleRect, matrix);
            }
        }

        return Utils.RECT_NO_SELECTED;
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

        return intersectCount;
    }

    public int checkIntersect(float x, float y) {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        int intersectCount = 0;
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (Utils.intersect(x, y, d)) {
                    intersectCount++;
                    d.setState(STATE_EDIT);
                    mDoodleRect.set(0, 0, 0, 0);
                    updateRect(d);
                    break;
                }
            }
        }

        if (intersectCount > 0) {
            setState(STATE_EDIT);
        }
        return intersectCount;
    }

    /**
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

        mDoodleRect.set(x1, y1, x2, y2);
        mSelectorRegion.set((int) x1, (int) y1, (int) x2, (int) y2);

        boolean intersect = false;
        int intersectCount = 0;
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        long s = System.currentTimeMillis();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (d instanceof Beeline) {
                    LineSegment beeLineSeg = ((Beeline) d).getLineSegment();
                    intersect = Utils.intersect(mDoodleRect, beeLineSeg);
                    if (intersect) {
                        intersectCount++;
                        d.setState(Doodle.STATE_EDIT);
                    }
                } else if (d instanceof Triangle) {
                    LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments();
                    for (LineSegment lineSegment : beeLineSeg) {
                        intersect = Utils.intersect(mDoodleRect, lineSegment);
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

        //Log.i("aaa", "intersect take="+(System.currentTimeMillis() - s)+"   doodle size="+ allDoodles.size());
        if (intersectCount > 0) {
            mDoodleRect.set(0, 0, 0, 0);
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
        mTransformMatrix.reset();
        if (mDoodleRect.isEmpty()) {
            mDoodleRect.set(rect);
        } else {
            if (rect.left < mDoodleRect.left) {
                mDoodleRect.set(rect.left, mDoodleRect.top, mDoodleRect.right, mDoodleRect.bottom);
            }

            if (rect.top < mDoodleRect.top) {
                mDoodleRect.set(mDoodleRect.left, rect.top, mDoodleRect.right, mDoodleRect.bottom);
            }

            if (rect.right > mDoodleRect.right) {
                mDoodleRect.set(mDoodleRect.left, mDoodleRect.top, rect.right, mDoodleRect.bottom);
            }

            if (rect.bottom > mDoodleRect.bottom) {
                mDoodleRect.set(mDoodleRect.left, mDoodleRect.top, mDoodleRect.right, rect.bottom);
            }
        }

        Log.i("aaa", "updateRect mDoodleRect="+mDoodleRect);

    }

    @Override
    protected void computeCenterPoint(RectF rect) {
        mRectCenter[0] = rect.centerX();
        mRectCenter[1] = rect.centerY();
        mTransformMatrix.mapPoints(mRectCenter);
    }

    private void computeDoodleCenterPoint(RectF rect) {
        mDoodleRectCenter[0] = rect.centerX();
        mDoodleRectCenter[1] = rect.centerY();
        mDrawingMatrix.mapPoints(mDoodleRectCenter);
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
