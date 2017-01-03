package cn.xiaojs.xma.ui.classroom.whiteboard.action;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;

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
    private Paint mSelectingBgPaint;
    private Paint mSelectingDashPaint;
    private Doodle mSelectedDoodle;

    private float[] mTransformCenter;

    public Selector(Whiteboard whiteboard) {
        super(whiteboard, SELECTION);

        init();
    }

    private void init() {
        mSelectingDashPaint = buildDashPaint();
        mSelectingBgPaint = buildDefaultPaint();
        mTransformCenter = new float[2];
    }

    private Paint buildDashPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(WhiteboardConfigs.SELECTOR_STROKE_WIDTH);
        p.setColor(Color.WHITE);
        float dw = WhiteboardConfigs.SELECTOR_DASH_WIDTH;
        PathEffect blackEffects = new DashPathEffect(new float[]{dw, dw}, 0);
        p.setPathEffect(blackEffects);
        return p;
    }

    private Paint buildDefaultPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        p.setStrokeWidth(WhiteboardConfigs.SELECTOR_STROKE_WIDTH);
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
        mSelectedDoodle = null;
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
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        if (mState == STATE_DRAWING) {
            canvas.save();
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);

            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mTransRect.set(x1, y1, x2, y2);
            mDrawingPath.reset();
            mDrawingPath.addRect(mTransRect, Path.Direction.CCW);
            mDrawingPath.transform(mDrawingMatrix);

            canvas.drawPath(mDrawingPath, mSelectingBgPaint);
            canvas.drawPath(mDrawingPath, mSelectingDashPaint);

            canvas.restore();
        }
    }

    @Override
    public void drawBorder(Canvas canvas) {
        try {
            if (mSelectedDoodle != null) {
                canvas.save();
                canvas.concat(mDisplayMatrix);
                mSelectedDoodle.drawBorder(canvas);
                canvas.restore();
                return;
            } else {
                if (mDoodleRect.isEmpty() || mState == Doodle.STATE_IDLE) {
                    return;
                }

                Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
                float padding = params.paintStrokeWidth / 2 / mTotalScale;
                float exPadding = WhiteboardConfigs.BORDER_PADDING / mTotalScale;
                padding = padding * params.scale + exPadding;

                mBorderRect.set(mDoodleRect.left - padding, mDoodleRect.top - padding, mDoodleRect.right + padding, mDoodleRect.bottom + padding);
                mBorderDrawingPath.reset();
                mBorderDrawingPath.addRect(mBorderRect, Path.Direction.CCW);
                mBorderDrawingPath.transform(mTransformMatrix);
                canvas.drawPath(mBorderDrawingPath, mBorderPaint);

                //draw controller
                float radius = mControllerPaint.getStrokeWidth() / mTotalScale;
                mBorderRect.set(mDoodleRect.right + padding - radius, mDoodleRect.top - padding- radius,
                        mDoodleRect.right + padding + radius, mDoodleRect.top - padding + radius);
                mBorderDrawingPath.reset();
                mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
                mBorderDrawingPath.transform(mTransformMatrix);
                canvas.drawPath(mBorderDrawingPath, mControllerPaint);

                //draw del btn
                mBorderRect.set(mDoodleRect.left - padding - radius, mDoodleRect.bottom + padding - radius,
                        mDoodleRect.left - padding + radius, mDoodleRect.bottom + padding + radius);
                mBorderDrawingPath.reset();
                mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
                mBorderDrawingPath.transform(mTransformMatrix);
                canvas.drawPath(mBorderDrawingPath, mControllerPaint);
                Utils.drawDelBtn(canvas, mBorderRect, mBorderDrawingPath, mTransformMatrix, params);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void move(float deltaX, float deltaY) {
        if (mSelectedDoodle != null) {
            mSelectedDoodle.move(deltaX, deltaY);
            return;
        }

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
        if (mSelectedDoodle != null) {
            mSelectedDoodle.scaleAndRotate(oldX, oldY, x, y);
            return;
        }

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
                Whiteboard.WhiteboardParams params = getWhiteboard().getParams();
                mTransRect.set(mDoodleRect);
                mTransformMatrix.mapRect(mTransRect);
                PointF p = Utils.mapScreenToDoodlePoint(mTransRect.left, mTransRect.top, params.drawingBounds);
                float left = p.x;
                float top = p.y;
                p = Utils.mapScreenToDoodlePoint(mTransRect.right, mTransRect.bottom, params.drawingBounds);
                float right = p.x;
                float bottom = p.y;

                mTransRect.set(left, top, right, bottom);
                Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
                float[] arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mTransRect, matrix);
                float scale = arr[0];
                float degree = arr[1];

                computeTransformCenterPoint(mTransRect);

                for (Doodle d : allDoodles) {
                    if (d.getState() == Doodle.STATE_EDIT) {
                        d.scaleRotateByPoint(scale, degree, mTransformCenter[0], mTransformCenter[1]);
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
        if (mSelectedDoodle != null) {
            mSelectedDoodle.isSelected(x, y);
        }

        return false;
    }

    @Override
    public int checkPressedRegion(float x, float y) {
        if (getState() == STATE_EDIT) {
            if (mSelectedDoodle != null) {
                return mSelectedDoodle.checkPressedRegion(x, y);
            }

            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mTransformMatrix, null, mRectCenter, mTotalDegree);
            int corner = IntersectionHelper.whichCornerPressed(p.x, p.y, mDoodleRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                return IntersectionHelper.checkRectPressed(p.x, p.y, mDoodleRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    @Override
    public Path getScreenPath() {
        return null;
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        if (mSelectedDoodle instanceof GeometryShape) {
            mSelectedDoodle.changeAreaByEdge(oldX, oldY, x, y, edge);
        }
    }

    public int checkIntersect() {
        cleanAllDoodlesState();
        int intersectCount = 0;
        if (mPoints.size() > 1) {
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);

            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            intersectCount = intersect(x1, y1, x2, y2);
        }

        if (intersectCount == 1) {
            ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
            if (allDoodles == null) {
                return 0;
            }

            for (Doodle d : allDoodles) {
                if (d.getState() == STATE_EDIT) {
                    mSelectedDoodle = d;
                    break;
                }
            }
        }

        return intersectCount;
    }

    public int checkSingleIntersect(float x, float y) {
        cleanAllDoodlesState();
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        int intersectCount = 0;
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (IntersectionHelper.intersect(x, y, d)) {
                    intersectCount++;
                    mDoodleRect.set(0, 0, 0, 0);
                    updateRect(d);
                    mSelectedDoodle = d;
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
        Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
        PointF p = Utils.mapDoodlePointToScreen(x1, y1, params.drawingBounds);
        x1 = p.x;
        y1 = p.y;

        p = Utils.mapDoodlePointToScreen(x2, y2, params.drawingBounds);
        x2 = p.x;
        y2 = p.y;

        mDoodleRect.set(x1, y1, x2, y2);

        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        int intersectCount = IntersectionHelper.intersect(allDoodles, mDoodleRect);
        if (intersectCount > 0) {
            if (allDoodles == null) {
                return 0;
            }

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
        RectF rect = doodle.getDoodleScreenRect();
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

        Log.i("aaa", "updateRect mDoodleRect=" + mDoodleRect);

    }

    @Override
    protected void computeCenterPoint(RectF rect) {
        mRectCenter[0] = rect.centerX();
        mRectCenter[1] = rect.centerY();
        mTransformMatrix.mapPoints(mRectCenter);
    }

    private void computeTransformCenterPoint(RectF rect) {
        mTransformCenter[0] = rect.centerX();
        mTransformCenter[1] = rect.centerY();
        mDrawingMatrix.mapPoints(mTransformCenter);
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

    private void cleanAllDoodlesState() {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles == null) {
            return;
        }

        for (Doodle d : allDoodles) {
            d.setState(Doodle.STATE_IDLE);
        }
    }

    public Doodle getSelectedDoodle() {
        return mSelectedDoodle;
    }

}
