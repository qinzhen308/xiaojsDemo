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

import java.util.ArrayList;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;

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
    private boolean mBorderVisible;

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
        mTotalScaleX = 1.0f;
        mTotalScaleY = 1.0f;
        mTotalDegree = 0;
        mSelectedDoodle = null;
        mTransformMatrix.reset();
        mBorderTransformMatrix.reset();
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                d.setState(Doodle.STATE_IDLE);
            }
        }
        transInOneSelecting=false;
        setState(Doodle.STATE_IDLE);
    }

    boolean transInOneSelecting=false;

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
            mTransRect.set(x1, y1, x2, y2);
            mDrawingPath.reset();
            mDrawingPath.addRect(mTransRect, Path.Direction.CCW);
//            mDrawingPath.transform(mGroupTransformMatrix);
            mDrawingPath.transform(mDrawingMatrix);

            canvas.drawPath(mDrawingPath, mSelectingBgPaint);
            canvas.drawPath(mDrawingPath, mSelectingDashPaint);

            canvas.restore();
        }
    }

    @Override
    protected void onDrawSelf(Canvas canvas) {
        //do nothing
    }

    @Override
    public void drawBorder(Canvas canvas) {
        try {
            if (!mBorderVisible) {
                return;
            }

            if (mSelectedDoodle != null) {
                canvas.save();
                canvas.concat(mDisplayMatrix);
                mSelectedDoodle.drawBorder(canvas);
                canvas.restore();
                return;
            } else {
                if (mDoodleRect.isEmpty()) {
                    return;
                }

                Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
                float paddingX = params.paintStrokeWidth / 2 / mTotalScaleX;
                float paddingY = params.paintStrokeWidth / 2 / mTotalScaleY;
                float exPaddingX = WhiteboardConfigs.BORDER_PADDING / mTotalScaleX;
                float exPaddingY = WhiteboardConfigs.BORDER_PADDING / mTotalScaleY;
                paddingX = paddingX * params.scale + exPaddingX;
                paddingY = paddingY * params.scale + exPaddingY;

                mBorderRect.set(mDoodleRect.left - paddingX, mDoodleRect.top - paddingY, mDoodleRect.right + paddingX, mDoodleRect.bottom + paddingY);
                mBorderDrawingPath.reset();
                mBorderDrawingPath.addRect(mBorderRect, Path.Direction.CCW);
//                mBorderDrawingPath.transform(mGroupTransformMatrix);
                mBorderDrawingPath.transform(mTransformMatrix);
                canvas.drawPath(mBorderDrawingPath, mBorderPaint);

                //draw controller
                float radiusX = mControllerPaint.getStrokeWidth() / mTotalScaleX;
                float radiusY = mControllerPaint.getStrokeWidth() / mTotalScaleY;
                mBorderRect.set(mDoodleRect.right + paddingX - radiusX, mDoodleRect.top - paddingY- radiusY,
                        mDoodleRect.right + paddingX + radiusX, mDoodleRect.top - paddingY + radiusY);
                mBorderDrawingPath.reset();
                mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
                mBorderDrawingPath.transform(mTransformMatrix);
                canvas.drawPath(mBorderDrawingPath, mControllerPaint);

                //draw del btn
                mBorderRect.set(mDoodleRect.left - paddingX - radiusX, mDoodleRect.bottom + paddingY - radiusY,
                        mDoodleRect.left - paddingX + radiusX, mDoodleRect.bottom + paddingY + radiusY);
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
    public float[] scaleAndRotate(float oldX, float oldY, float x, float y) {
        if (mSelectedDoodle != null) {
           return mSelectedDoodle.scaleAndRotate(oldX, oldY, x, y);
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
                        d.scaleRotateByAnchor(scale, degree, mTransformCenter[0], mTransformCenter[1]);
                    }
                }

                matrix = Utils.transformScreenMatrix(mTransformMatrix, null);
                arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mDoodleRect, matrix);
                scale = arr[0];
                degree = arr[1];
                computeCenterPoint(mDoodleRect);

                scaleRotateByAnchor(scale, degree, mRectCenter[0], mRectCenter[1]);

                float[] result = new float[2];
                result[0] = scale;
                result[1] = degree;

                return result;
            }
        }

        return null;
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
                int edge = IntersectionHelper.whichEdgePressed(p.x, p.y, mDoodleRect, matrix);
                if (edge != IntersectionHelper.RECT_NO_SELECTED) {
                    return edge;
                }
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
    public void changeByEdge(float oldX, float oldY, float x, float y, int edge) {
        if (mSelectedDoodle instanceof GeometryShape) {
            mSelectedDoodle.changeByEdge(oldX, oldY, x, y, edge);
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
                float scaleX=1;
                float scaleY=1;
                float cx=mDoodleRect.centerX();
                float cy=mDoodleRect.centerY();
                float[] _c=new float[]{cx,cy};
                float[] _p=new float[]{x,y};
                float[] _pOld=new float[]{oldX,oldY};

                //从屏幕坐标映射到画布rect里
                matrix.reset();
                mDisplayMatrix.invert(matrix);
                matrix.mapPoints(_p);
                matrix.mapPoints(_pOld);
                matrix.mapPoints(_c);

               /* //算出变换后的位置
                mTransformMatrix.mapPoints(_p);
                mTransformMatrix.mapPoints(_pOld);
                mTransformMatrix.mapPoints(_c);*/

//                routeM.preConcat(matrix);
//                routeM.mapPoints(_p);
//                routeM.mapPoints(_pOld);
                Matrix rotateM=new Matrix();
                mTransformMatrix.invert(rotateM);
                rotateM.mapPoints(_p);
                rotateM.mapPoints(_pOld);

                switch (edge) {
                    case IntersectionHelper.TOP_EDGE:
                        if(_p[1]>=_c[1]){
                            scaleY = 1f;
                        }else {
                            scaleY = (_p[1] - _c[1]) / (_pOld[1] - _c[1]);
                        }
                        break;
                    case IntersectionHelper.RIGHT_EDGE:

                        if(_p[0]<=_c[0]){
                            scaleX = 1f;
                        }else {
                            scaleX = (_p[0] - _c[0]) / (_pOld[0] - _c[0]);
                        }
                        break;
                    case IntersectionHelper.BOTTOM_EDGE:
                        if(_p[1]<=_c[1]){
                            scaleY = 1f;
                        }else {
                            scaleY = (_p[1] - _c[1]) / (_pOld[1] - _c[1]);
                        }
                        break;
                    case IntersectionHelper.LEFT_EDGE:

                        if(_p[0]>=_c[0]){
                            scaleX = 1f;
                        }else {
                            scaleX = (_p[0] - _c[0]) / (_pOld[0] - _c[0]);
                        }
                        break;
                }

                mTransformMatrix.mapPoints(_c);

                for (Doodle d : allDoodles) {
                    if (d.getState() == Doodle.STATE_EDIT) {
                        if(Float.compare(getTotalDegree(),0.0f)==0){
                            d.scaleInSelector(scaleX,scaleY,_c[0],_c[1]);
                            d.reset();
                        }else {
                            d.preScaleInSelector(scaleX,scaleY,_c[0],_c[1],getTotalDegree(),_c[0],_c[1]);
                            d.reset();
                        }
                    }
                }
                matrix = Utils.transformScreenMatrix(mTransformMatrix, null);
                arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mDoodleRect, matrix);
                scale = arr[0];
                degree = arr[1];
                computeCenterPoint(mDoodleRect);
//                scaleInSelector(scaleX,scaleY,_c[0],_c[1]);
                scaleInSelector(scaleX,scaleY,cx,cy);


                float[] result = new float[2];
                result[0] = scale;
                result[1] = degree;

            }
        }
    }


    @Override
    public void scaleInSelector(float scaleX, float scaleY, float px, float py) {
        mTotalScaleX = mTotalScaleX * scaleX;
        mTotalScaleY = mTotalScaleY * scaleY;
        mTransformMatrix.preScale(scaleX, scaleY, px, py);
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

    @Override
    public void setVisibility(int visibility) {
        ArrayList<Doodle> allDoodles = getWhiteboard().getAllDoodles();
        if (allDoodles != null) {
            for (Doodle d : allDoodles) {
                if (d.getState() == STATE_EDIT) {
                    d.setVisibility(visibility);
                }
            }
        }
    }

    public void setBorderVisible(boolean visible) {
        mBorderVisible = visible;
    }
}
