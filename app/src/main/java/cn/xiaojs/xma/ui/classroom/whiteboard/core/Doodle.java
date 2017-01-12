package cn.xiaojs.xma.ui.classroom.whiteboard.core;
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
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

public abstract class Doodle implements Transformation {
    public final static int SELECTION = 0;
    public final static int STYLE_HAND_WRITING = 1;
    public final static int STYLE_GEOMETRY = 2;
    public final static int STYLE_ERASER = 3;
    public final static int STYLE_TEXT = 4;

    public final static int STATE_IDLE = 0;
    public final static int STATE_DRAWING = 1;
    public final static int STATE_EDIT = 2;

    protected Whiteboard mWhiteboard;
    protected List<PointF> mPoints;

    protected Paint mPaint;
    protected Paint mBorderPaint;
    protected Paint mControllerPaint;
    protected Paint mDelBtnPaint;

    protected float mTotalDegree = 0;
    protected float mTotalScale = 1.0f;
    protected float mTranslateX = 0;
    protected float mTranslateY = 0;

    protected int mStyle;

    protected final RectF mDoodleRect;
    protected RectF mTransRect;
    protected RectF mBorderRect;

    protected Path mDrawingPath;
    protected Path mBorderDrawingPath;
    protected Path mScreenPath;

    protected PointF mBorderPadding;
    protected float[] mRectCenter;

    protected Matrix mDrawingMatrix;
    protected Matrix mTransformMatrix;
    protected Matrix mDisplayMatrix;
    protected int mState = STATE_IDLE;

    private String mDoodleId;
    protected List<ActionRecord> mUndoRecords;
    protected List<ActionRecord> mRedoRecords;
    private int mVisibility = View.VISIBLE;

    protected Doodle(Whiteboard whiteboard, int style) {
        mDoodleId = Utils.buildDoodleId();
        mWhiteboard = whiteboard;
        mStyle = style;
        mState = STATE_IDLE;

        int capacity = initialCapacity();
        if (capacity <= 0) {
            mPoints = new Vector<PointF>();
        } else {
            mPoints = new Vector<PointF>(capacity);
        }

        mDoodleRect = new RectF();

        initParams();
    }

    private void initParams() {
        mTransRect = new RectF();
        mBorderRect = new RectF();

        mDrawingMatrix = new Matrix();
        mTransformMatrix = new Matrix();
        mDisplayMatrix = new Matrix();

        mDrawingPath = new Path();
        mScreenPath = new Path();
        mBorderDrawingPath = new Path();

        mRectCenter = new float[2];

        mBorderPaint = Utils.buildDashPaint();
        mControllerPaint = Utils.buildControllerPaint();
        mDelBtnPaint = Utils.buildDelBtnPaint();

        mUndoRecords = new ArrayList<ActionRecord>();
        mRedoRecords = new ArrayList<ActionRecord>();

        mBorderPadding = new PointF();
    }

    protected int initialCapacity() {
        return 0;
    }

    //==========================getter and setter==========================================

    public Paint getPaint() {
        return mPaint;
    }

    public List<PointF> getPoints() {
        return mPoints;
    }

    public void setPoints(List<PointF> points) {
        mPoints = points;
    }

    public int getStyle() {
        return mStyle;
    }

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public Whiteboard getWhiteboard() {
        return mWhiteboard;
    }

    public float getTotalScale() {
        return mTotalScale;
    }

    public void setTotalScale(float totalScale) {
        mTotalScale = totalScale;
    }

    public float getTotalDegree() {
        return mTotalDegree;
    }

    public void setTotalDegree(float degree) {
        mTotalDegree = degree;
    }

    public RectF getRect() {
        return mTransRect;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public String getDoodleId() {
        return mDoodleId;
    }

    public void setDoodleId(String doodleId) {
        mDoodleId = doodleId;
    }

    public float getTranslateX() {
        return mTranslateX;
    }

    public void setTranslateX(float translateX) {
        mTranslateX = translateX;
    }

    public float getTranslateY() {
        return mTranslateY;
    }

    public void setTranslateY(float translateY) {
        mTranslateY = translateY;
    }

    public void setDoodleRect(RectF rect) {
        if (rect != null) {
            mDoodleRect.set(rect);
        }
    }

    //==========================getter and setter==========================================

    public void addControlPoint(PointF point) {
        mPoints.add(point);
    }

    public void addControlPoint(float x, float y) {
        addControlPoint(new PointF(x, y));
    }

    public PointF getFirstPoint() {
        return mPoints.get(0);
    }

    public PointF getLastPoint() {
        return mPoints.get(mPoints.size() - 1);
    }

    public void setWhiteboard(Whiteboard whiteboard) {
        mWhiteboard = whiteboard;
    }

    public void setDrawingMatrix(Matrix matrix) {
        if (mDrawingMatrix != null) {
            mDrawingMatrix.set(matrix);
        } else {
            mDrawingMatrix = matrix;
        }
    }

    public void setDisplayMatrix(Matrix matrix) {
        if (mDisplayMatrix != null) {
            mDisplayMatrix.set(matrix);
        } else {
            mDisplayMatrix = matrix;
        }
    }

    //==============================
    public abstract Path getScreenPath();

    public void drawSelf(Canvas canvas) {
        if (isShow()) {
            onDrawSelf(canvas);
        }
    }

    protected abstract void onDrawSelf(Canvas canvas);

    public void reset() {

    }

    public void drawBorder(Canvas canvas) {
        if (isShow() && mPoints.size() > 1 && !mDoodleRect.isEmpty()) {
            onDrawBorder(canvas);
        }
    }

    protected void onDrawBorder(Canvas canvas) {
        computeBorderPadding();

        Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
        float hPadding = mBorderPadding.x;
        float vPadding = mBorderPadding.y;
        mBorderRect.set(mDoodleRect.left - hPadding, mDoodleRect.top - vPadding,
                mDoodleRect.right + hPadding, mDoodleRect.bottom + vPadding);

        //draw border
        mBorderDrawingPath.reset();
        mBorderDrawingPath.addRect(mBorderRect, Path.Direction.CCW);
        mBorderDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mBorderDrawingPath, mBorderPaint);

        //draw controller
        float radius = mControllerPaint.getStrokeWidth() / mTotalScale;
        PointF p = Utils.normalizeScreenPoint(radius, radius, params.drawingBounds);
        mBorderRect.set(mDoodleRect.right + hPadding - p.x, mDoodleRect.top - vPadding - p.y,
                mDoodleRect.right + hPadding + p.x, mDoodleRect.top - vPadding + p.y);
        mBorderDrawingPath.reset();
        mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
        mBorderDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mBorderDrawingPath, mControllerPaint);

        //draw del btn
        mBorderRect.set(mDoodleRect.left - hPadding - p.x, mDoodleRect.bottom + vPadding - p.y,
                mDoodleRect.left - hPadding + p.x, mDoodleRect.bottom + vPadding + p.y);
        mBorderDrawingPath.reset();
        mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
        mBorderDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mBorderDrawingPath, mControllerPaint);
        Utils.drawDelBtn(canvas, mBorderRect, mBorderDrawingPath, mDrawingMatrix, params);
    }

    protected void computeBorderPadding() {
        Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
        float dashW = WhiteboardConfigs.BORDER_DASH_WIDTH / params.scale;

        mBorderPaint.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH / params.scale);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

        float paintStrokeWidth = mPaint != null ? mPaint.getStrokeWidth() : 0;
        float padding = (paintStrokeWidth + mBorderPaint.getStrokeWidth()) / 2;
        PointF p = Utils.normalizeScreenPoint(padding, padding, params.drawingBounds);
        float hPadding = p.x / mTotalScale * params.scale;
        float vPadding = p.y / mTotalScale * params.scale;
        //add extra padding
        p = Utils.normalizeScreenPoint(WhiteboardConfigs.BORDER_PADDING, WhiteboardConfigs.BORDER_PADDING, params.drawingBounds);
        hPadding = hPadding + p.x / mTotalScale;
        vPadding = vPadding + p.y / mTotalScale;
        mBorderPadding.set(hPadding, vPadding);
    }

    public int checkPressedRegion(float x, float y) {
        if (isShow()) {
            return onCheckPressedRegion(x, y);
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    protected int onCheckPressedRegion(float x, float y) {
        if (getState() == STATE_EDIT && mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            int corner = IntersectionHelper.whichCornerPressed(p.x, p.y, mTransRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                int edge = IntersectionHelper.whichEdgePressed(p.x, p.y, mTransRect, matrix);
                if (edge != IntersectionHelper.RECT_NO_SELECTED) {
                    return edge;
                }

                return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    public boolean isSelected(float x, float y) {
        if (isShow()) {
            return onCheckSelected(x, y);
        }

        return false;
    }

    protected boolean onCheckSelected(float x, float y) {
        return false;
    }

    protected void computeCenterPoint(RectF rect) {
        mRectCenter[0] = rect.centerX();
        mRectCenter[1] = rect.centerY();
        mDrawingMatrix.mapPoints(mRectCenter);
    }

    /**
     * 返回doodle映射到屏幕上的rect
     */
    public RectF getDoodleScreenRect() {
        if (mPoints.size() > 1) {
            mDrawingPath.computeBounds(mTransRect, true);
        }

        mDisplayMatrix.mapRect(mTransRect);
        return mTransRect;
    }

    public Matrix getTransformMatrix() {
        return mTransformMatrix;
    }

    public void setTransformMatrix(Matrix matrix) {
        if (mTransformMatrix != null) {
            mTransformMatrix.set(matrix);
        } else {
            mTransformMatrix = matrix;
        }
    }

    public void addRecords(int action, int groupId) {
        ActionRecord record = new ActionRecord(getDoodleId(), groupId, action);
        record.scale = getTotalScale();
        record.degree = getTotalDegree();
        record.translateX = getTranslateX();
        record.translateY = getTranslateY();
        record.setRect(mDoodleRect);
        record.setMatrix(getTransformMatrix());
        record.setPoints(getPoints());

        mUndoRecords.add(record);
        mRedoRecords.clear();
    }

    /**
     * @return last and last but one Records
     */
    public ActionRecord[] undo(int groupId) {
        ActionRecord[] records = new ActionRecord[2];
        if (mUndoRecords.size() > 0) {
            ActionRecord lastRecord = mUndoRecords.get(mUndoRecords.size() - 1);
            if (lastRecord.groupId == groupId) {
                mUndoRecords.remove(lastRecord);
                mRedoRecords.add(lastRecord);
                records[0] = lastRecord;
                if (mUndoRecords.size() > 0) {
                    records[1] = mUndoRecords.get(mUndoRecords.size() - 1);
                }
            }
        }

        return records;
    }

    public ActionRecord redo(int groupId) {
        if (mRedoRecords.size() > 0) {
            ActionRecord record = mRedoRecords.get(mRedoRecords.size() - 1);
            if (record.groupId == groupId) {
                mRedoRecords.remove(record);
                mUndoRecords.add(record);
                return record;
            }
        }

        return null;
    }

    public List<ActionRecord> getUndoRecords() {
        return mUndoRecords;
    }

    public List<ActionRecord> getRedoRecords() {
        return mRedoRecords;
    }

    public boolean isCanUndo() {
        return mUndoRecords != null && !mUndoRecords.isEmpty();
    }

    public boolean isCanRedo() {
        return mRedoRecords != null && !mRedoRecords.isEmpty();
    }

    public void setVisibility(int visibility) {
        mVisibility = visibility;
    }

    public boolean isShow() {
        return mVisibility == View.VISIBLE;
    }

    @Override
    public void move(float oldX, float oldY, float x, float y) {
        move(x - oldX, y - oldY);
    }

    @Override
    public void move(float deltaX, float deltaY) {
        Whiteboard.WhiteboardParams params = getWhiteboard().getParams();
        float moveX = deltaX / params.scale;
        float moveY = deltaY / params.scale;
        translate(moveX, moveY);
    }

    @Override
    public void scale(float scale) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            computeCenterPoint(mTransRect);
            scale(scale, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public float scale(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
            float scale = Utils.calcRectScale(oldX, oldY, x, y, mTransRect, matrix);

            computeCenterPoint(mTransRect);
            scale(scale, mRectCenter[0], mRectCenter[1]);

            return scale;
        }

        return 1;
    }

    @Override
    public void rotate(float degree) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            computeCenterPoint(mTransRect);
            rotate(degree, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public float rotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
            float degree = Utils.calcRectDegrees(oldX, oldY, x, y, mTransRect, matrix);

            computeCenterPoint(mTransRect);
            rotate(degree, mRectCenter[0], mRectCenter[1]);
            return degree;
        }

        return 0;
    }

    @Override
    public void scaleAndRotate(float scale, float degree) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            computeCenterPoint(mTransRect);
            scaleRotateByAnchor(scale, degree, mRectCenter[0], mRectCenter[1]);
        }
    }

    @Override
    public float[] scaleAndRotate(float oldX, float oldY, float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
            float[] arr = Utils.calcRectDegreesAndScales(oldX, oldY, x, y, mTransRect, matrix);
            float scale = arr[0];
            float degree = arr[1];

            computeCenterPoint(mTransRect);
            scaleRotateByAnchor(scale, degree, mRectCenter[0], mRectCenter[1]);

            float[] result = new float[2];
            result[0] = scale;
            result[1] = degree;
            return result;
        }

        return null;
    }

    @Override
    public void changeByEdge(float oldX, float oldY, float x, float y, int edge) {
        //empty
    }

    @Override
    public void changeByEdge(float deltaX, float deltaY, int byEdge) {
        //empty
    }


    public void translate(float moveX, float moveY) {
        mTranslateX += moveX;
        mTranslateY += moveY;
        mTransformMatrix.postTranslate(moveX, moveY);
    }

    public void scale(float scale, float px, float py) {
        mTotalScale = mTotalScale * scale;
        mTransformMatrix.postScale(scale, scale, px, py);
    }

    public void rotate(float degree, float px, float py) {
        mTotalDegree += degree;
        mTransformMatrix.postRotate(degree, px, py);
    }

    public void scaleRotateByAnchor(float scale, float degree, float px, float py) {
        scale(scale, px, py);
        rotate(degree, px, py);
    }

    /**
     * 获取距上一次变换的旋转变化值
     */
    public float getDeltaDegree() {
        if (mUndoRecords == null || mUndoRecords.size() < 1) {
            return 0;
        }

        ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 2);
        return mTotalDegree - record.degree;
    }

    /**
     * 获取距上一次变换的缩放变化值
     */
    public float getDeltaScale() {
        if (mUndoRecords == null || mUndoRecords.size() < 1) {
            return 1;
        }

        ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 2);
        return mTotalScale - record.scale;
    }

    /**
     * 获取距上一次变换的平移变换值X
     */
    public float getDeltaTransX() {
        if (mUndoRecords == null || mUndoRecords.size() < 1) {
            return 0;
        }

        ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 2);
        return mTranslateX - record.translateX;
    }

    /**
     * 获取距上一次变换的平移变换值y
     */
    public float getDeltaTransY() {
        if (mUndoRecords == null || mUndoRecords.size() < 1) {
            return 0;
        }

        ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 2);
        return mTranslateY - record.translateY;
    }
}
