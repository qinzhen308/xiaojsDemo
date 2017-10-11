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

import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

/**
 * 坐标系或者rect有三个参照系：
 * 1、屏幕坐标系---（0,0,screenW,screenH）
 * 2、白板坐标系---（0,0,boardW,boardH），boardH可能有多屏
 * 3、单元坐标系---（0,0,1,1） h也可以为2,3,4...根据具体扩展了多少屏白板来确定。
 *
 * 关系：
 * <1>中的点或rect等可以通过mDisplayMatrix映射到<2>中
 * <3>中的点或rect可通过mDrawingMatrix映射到<2>中
 *
 * whiteboard.ondraw(canvas)中绘制完图形后作mDisplayMatrix变换，得到最终画面；
 * doodle中的绘制是相对于白板的图形绘制，所以doodle中的绘制不作mDisplayMatrix变换；
 * 对于doodle中的坐标或rect计算一般是相对于<3>中来处理的，
 * 但有可能传入到doodle某函数的点坐标属于<1>，所以需要进行映射，使得所有参与计算的参数都在一个参照系。
 *
 */
public abstract class Doodle implements Transformation{
    public final static int SELECTION = 0;
    public final static int STYLE_HAND_WRITING = 1;
    public final static int STYLE_GEOMETRY = 2;
    public final static int STYLE_ERASER = 3;
    public final static int STYLE_TEXT = 4;

    public final static int STYLE_SYNC_LAYER = 5;
    public final static int STYLE_SYNC_LAYER_IMG = 6;

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
    protected float mTotalScaleX = 1.0f;
    protected float mTotalScaleY = 1.0f;
    protected float mTranslateX = 0;
    protected float mTranslateY = 0;

    protected int mStyle;

    //创建完成后，就不会再变大小，绘制标准内容--->matrix transform
    protected final RectF mDoodleRect;
    protected RectF mTransRect;
    protected RectF mBorderRect;

    protected Path mDrawingPath;
    protected Path mBorderDrawingPath;
    protected Path mScreenPath;

    protected PointF mBorderPadding;
    //单元框中，图层rect的中心坐标，
    protected float[] mRectCenter;

    protected Matrix mDrawingMatrix;
    protected Matrix mTransformMatrix;
    protected Matrix mBorderTransformMatrix=new Matrix();
    protected Matrix mDisplayMatrix;
    protected int mState = STATE_IDLE;

    private String mDoodleId;
    protected List<ActionRecord> mUndoRecords;
    protected List<ActionRecord> mRedoRecords;
    private int mVisibility = View.VISIBLE;

    protected float mDrawScale = 1.0f;

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

    public float getTotalScaleX() {
        return mTotalScaleX;
    }

    public float getTotalScaleY() {
        return mTotalScaleY;
    }

    public void setTotalScaleX(float totalScaleX) {
        mTotalScaleX = totalScaleX;
    }
    public void setTotalScaleY(float totalScaleY) {
        mTotalScaleY = totalScaleY;
    }

    public float getTotalDegree() {
        return mTotalDegree;
    }

    public void setTotalDegree(float degree) {
        mTotalDegree = degree;
    }

    public RectF getTransRect() {
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

    public void setBorderRect(RectF rect) {
        if (rect != null) {
            mBorderRect.set(rect);
        }
    }

    public RectF getDoodleRect() {
        return mDoodleRect;
    }

    public void setDrawScale(float drawScale) {
        mDrawScale = drawScale;
    }

    //==========================getter and setter==========================================

    public final void addControlPoint(PointF point) {
        computeRectByCreate(point);
        mBorderRect.set(mDoodleRect);
    }

    public final void addControlPoint(float x, float y) {
        addControlPoint(new PointF(x, y));
    }

    public void setControlPoints(List<PointF> src) {
        mPoints=src;
    }

    public void computeRectByCreate(PointF point){
        mPoints.add(point);

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
        mTotalDegree=0;
        mTotalScaleX=0;
        mTotalScaleY=0;
        mTranslateX=0;
        mTranslateY=0;
        mBorderTransformMatrix.reset();
        mDrawingPath.computeBounds(mBorderRect,false);
        Matrix matrix=new Matrix();
        getWhiteboard().getDrawingMatrix().invert(matrix);
        matrix.mapRect(mBorderRect);
    }

    public void drawBorder(Canvas canvas) {
        if (isShow() && mPoints.size() > 1 && !mBorderRect.isEmpty()) {
            onDrawBorder(canvas);
        }
    }

    protected void onDrawBorder(Canvas canvas) {
        Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
        float dashW = WhiteboardConfigs.BORDER_DASH_WIDTH / params.scale;
        mBorderPaint.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH / params.scale);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

        Matrix unitToScreenMatrix=new Matrix(getWhiteboard().getDrawingMatrix());
        unitToScreenMatrix.postConcat(mBorderTransformMatrix);

        float paddingH=WhiteboardConfigs.BORDER_PADDING/params.scale/(float)params.originalHeight;
        float paddingW=WhiteboardConfigs.BORDER_PADDING/params.scale/(float)params.originalWidth;
        //draw border
        mBorderDrawingPath.reset();
        RectF realBorderRect=new RectF(mBorderRect.left-paddingW,mBorderRect.top-paddingH,mBorderRect.right+paddingW,mBorderRect.bottom+paddingH);
        mBorderDrawingPath.addRect(realBorderRect, Path.Direction.CCW);
        mBorderDrawingPath.transform(unitToScreenMatrix);
        canvas.drawPath(mBorderDrawingPath, mBorderPaint);

        //draw controller

        mBorderDrawingPath.reset();
        mBorderDrawingPath.addOval(ControlPointUtil.getRotatePoint(realBorderRect,unitToScreenMatrix,params.scale), Path.Direction.CCW);
        mBorderDrawingPath.addOval(ControlPointUtil.getDeletePoint(realBorderRect,unitToScreenMatrix,params.scale), Path.Direction.CCW);
        mBorderDrawingPath.addOval(ControlPointUtil.getLeftPoint(realBorderRect,unitToScreenMatrix,params.scale), Path.Direction.CCW);
        mBorderDrawingPath.addOval(ControlPointUtil.getTopPoint(realBorderRect,unitToScreenMatrix,params.scale), Path.Direction.CCW);
        mBorderDrawingPath.addOval(ControlPointUtil.getRightPoint(realBorderRect,unitToScreenMatrix,params.scale), Path.Direction.CCW);
        mBorderDrawingPath.addOval(ControlPointUtil.getBottomPoint(realBorderRect,unitToScreenMatrix,params.scale), Path.Direction.CCW);
        canvas.drawPath(mBorderDrawingPath, mControllerPaint);
    }

    protected void computeBorderPadding() {
        Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
        float dashW = WhiteboardConfigs.BORDER_DASH_WIDTH / params.scale;

        mBorderPaint.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH / params.scale);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

        float paintStrokeWidth = mPaint != null ? mPaint.getStrokeWidth() : 0;
        float padding = (paintStrokeWidth + mBorderPaint.getStrokeWidth()) / 2;
        PointF p = Utils.normalizeScreenPoint(padding, padding, params.drawingBounds);
        float hPadding = p.x / mTotalScaleX * params.scale;
        float vPadding = p.y / mTotalScaleY * params.scale;
        //add extra padding
        p = Utils.normalizeScreenPoint(WhiteboardConfigs.BORDER_PADDING, WhiteboardConfigs.BORDER_PADDING, params.drawingBounds);
        hPadding = hPadding + p.x / mTotalScaleX;
        vPadding = vPadding + p.y / mTotalScaleY;
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
            mTransRect.set(mBorderRect);
            Matrix drawingMatrix=new Matrix(getWhiteboard().getDrawingMatrix());
            drawingMatrix.postConcat(mBorderTransformMatrix);
            float[] center=new float[]{mTransRect.centerX(),mTransRect.centerY()};
            Matrix matrixC=new Matrix(drawingMatrix);
            matrixC.postConcat(mDisplayMatrix);
            matrixC.mapPoints(center);
            PointF p = Utils.transformPoint(x, y, center, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(drawingMatrix, mDisplayMatrix, center, mTotalDegree);
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

    public void setBorderTransformMatrix(Matrix matrix) {
        if (mBorderTransformMatrix != null) {
            mBorderTransformMatrix.set(matrix);
        } else {
            mBorderTransformMatrix = matrix;
        }
    }

    public void addRecords(int action, int groupId) {
        ActionRecord record = new ActionRecord(getDoodleId(), groupId, action);
        record.scaleX = getTotalScaleX();
        record.scaleY = getTotalScaleY();
        record.degree = getTotalDegree();
        record.translateX = getTranslateX();
        record.translateY = getTranslateY();
        record.setRect(mBorderRect);
        record.setMatrix(getTransformMatrix());
        record.setBorderMatrix(mBorderTransformMatrix);
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

    /**
     * 保证最终变换是基于 “白板标准坐标系<2>”
     * @param oldX   上一次按下的x坐标
     * @param oldY   上一次按下的y坐标
     * @param x      当前按下的x坐标
     * @param y      当前按下的y坐标
     * @param edge
     */
  /*  @Override
    public void changeByEdge(float oldX, float oldY, float x, float y, int edge) {
        //empty
        float[] p1=new float[]{oldX,oldY};
        float[] p2=new float[]{x,y};
        float[] center=new float[]{mBorderRect.centerX(),mBorderRect.centerY()};
        Matrix matrixC=new Matrix(getWhiteboard().getDrawingMatrix());
        matrixC.postConcat(mBorderTransformMatrix);
        matrixC.mapPoints(center);
        mRectCenter[0]=center[0];
        mRectCenter[1]=center[1];

        Matrix matrixP=new Matrix(getWhiteboard().getDrawingMatrix());
        matrixP.postConcat(mBorderTransformMatrix);
        matrixP.postConcat(mDisplayMatrix);
        matrixP.invert(matrixP);
        matrixP.mapPoints(p1);
        matrixP.mapPoints(p2);

        if(edge==IntersectionHelper.BOTTOM_EDGE||edge==IntersectionHelper.TOP_EDGE){
            p2[0]=p1[0];
        }else if(edge==IntersectionHelper.LEFT_EDGE||edge==IntersectionHelper.RIGHT_EDGE){
            p2[1]=p1[1];
        }

        matrixP.set(getWhiteboard().getDrawingMatrix());
        matrixP.postConcat(mBorderTransformMatrix);
        matrixP.mapPoints(p1);
        matrixP.mapPoints(p2);

        float sx=(p2[0]-mRectCenter[0])/(p1[0]-mRectCenter[0]);
        float sy=(p2[1]-mRectCenter[1])/(p1[1]-mRectCenter[1]);
        if(Float.isNaN(sx)){
            sx=1;
        }
        if(Float.isNaN(sy)){
            sy=1;
        }

        mBorderTransformMatrix.postScale(sx,sy,mRectCenter[0],mRectCenter[1]);
        Logger.d("-----qz---mBorderTransformMatrix-------"+mBorderTransformMatrix.toString());
        mTransformMatrix.postScale(sx,sy,mRectCenter[0],mRectCenter[1]);
        Logger.d("-----qz---mTransformMatrix-------"+mTransformMatrix.toString());
    }*/

    @Override
    public void changeByEdge(float oldX, float oldY, float x, float y, int edge) {
        float[] p1=new float[]{oldX,oldY};
        float[] p2=new float[]{x,y};
        float[] center=new float[]{mBorderRect.centerX(),mBorderRect.centerY()};
        Matrix matrixC=new Matrix(getWhiteboard().getDrawingMatrix());
//        matrixC.postConcat(mBorderTransformMatrix);
        matrixC.mapPoints(center);
//        mRectCenter[0]=center[0];
//        mRectCenter[1]=center[1];

        Matrix matrixP=new Matrix(getWhiteboard().getDrawingMatrix());
        matrixP.postConcat(mBorderTransformMatrix);
        matrixP.postConcat(mDisplayMatrix);
        matrixP.invert(matrixP);
        matrixP.mapPoints(p1);
        matrixP.mapPoints(p2);

        mBorderTransformMatrix.invert(matrixC);

        if(edge==IntersectionHelper.BOTTOM_EDGE||edge==IntersectionHelper.TOP_EDGE){
            float sx=1;
            float sy=(p2[1]-mBorderRect.centerY())/(p1[1]-mBorderRect.centerY());

            if(Float.isNaN(sy)){
                sy=1;
            }
            mBorderTransformMatrix.preScale(sx,sy,center[0],center[1]);
        }else if(edge==IntersectionHelper.LEFT_EDGE||edge==IntersectionHelper.RIGHT_EDGE){
            float sx=(p2[0]-mBorderRect.centerX())/(p1[0]- mBorderRect.centerX());
            float sy=1;
            if(Float.isNaN(sx)){
                sx=1;
            }
            mBorderTransformMatrix.preScale(sx,sy,center[0],center[1]);
        }

        mTransformMatrix.postConcat(matrixC);
        mTransformMatrix.postConcat(mBorderTransformMatrix);
//        mTransformMatrix.postScale(sx,sy,mRectCenter[0],mRectCenter[1]);


        Logger.d("-----qz---mBorderTransformMatrix-------"+mBorderTransformMatrix.toString());
        Logger.d("-----qz---mTransformMatrix-------"+mTransformMatrix.toString());
    }

    @Override
    public void changeByEdge(float deltaX, float deltaY, int byEdge) {
        //empty
    }


    public void translate(float moveX, float moveY) {
        mTranslateX += moveX;
        mTranslateY += moveY;
        mTransformMatrix.postTranslate(moveX, moveY);
        mBorderTransformMatrix.postTranslate(moveX, moveY);
    }

    public void scale(float scale, float px, float py) {
        mTotalScaleX = mTotalScaleX * scale;
        mTotalScaleY = mTotalScaleY * scale;
        mTransformMatrix.postScale(scale, scale, px, py);
        mBorderTransformMatrix.postScale(scale, scale, px, py);
    }

    public void scaleInSelector(float scaleX, float scaleY, float px, float py) {
        mTotalScaleX = mTotalScaleX * scaleX;
        mTotalScaleY = mTotalScaleY * scaleY;
        mTransformMatrix.postScale(scaleX, scaleY, px, py);
        mBorderTransformMatrix.postScale(scaleX, scaleY, px, py);
    }
    public void preScaleInSelector(float scaleX, float scaleY, float px, float py,float selectorRoute,float cx,float cy) {
        // TODO: 2017/9/28 paulz
        mTotalScaleX = mTotalScaleX * scaleX;
        mTotalScaleY = mTotalScaleY * scaleY;
        float[] transedCenter=new float[]{cx,cy};
        mTransformMatrix.postRotate(-selectorRoute, transedCenter[0], transedCenter[1]);
        float[] transedP=new float[]{px,py};
//        mTransformMatrix.mapPoints(transedP);
        mTransformMatrix.postScale(scaleX, scaleY, transedP[0], transedP[1]);
//        Matrix matrix=new Matrix();
//        matrix.postScale(scaleX,scaleY,px,py);
//        matrix.postRotate(-selectorRoute, transedCenter[0], transedCenter[1]);
//        matrix.mapPoints(transedCenter);
        mTransformMatrix.postRotate(selectorRoute, transedCenter[0], transedCenter[1]);
    }

    public void rotate(float degree, float px, float py) {
        mTotalDegree += degree;
        mTransformMatrix.postRotate(degree, px, py);
        mBorderTransformMatrix.postRotate(degree, px, py);
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
    public float getDeltaScaleX() {
        if (mUndoRecords == null || mUndoRecords.size() < 1) {
            return 1;
        }

        ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 2);
        return mTotalScaleX / record.scaleX;
    }
    public float getDeltaScaleY() {
        if (mUndoRecords == null || mUndoRecords.size() < 1) {
            return 1;
        }

        ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 2);
        return mTotalScaleY / record.scaleY;
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

    public Matrix getDrawingMatrixFromWhiteboard(){
        return getWhiteboard().getDrawingMatrix();
    }
}
