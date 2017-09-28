package cn.xiaojs.xma.ui.classroom.whiteboard.shape;
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
 * Date:2016/12/7
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.Shape;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Action;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.LineSegment;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinished;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinishedDelete;

import java.util.ArrayList;
import java.util.Vector;

public class Triangle extends TwoDimensionalShape {
    private Vector<PointF> mTriangleCoordinates;
    private LineSegment[] mLineSegments;

    private Triangle(Whiteboard whiteboard) {
        super(whiteboard, GeometryShape.TRIANGLE);
    }

    public Triangle(Whiteboard whiteboard, Paint paint) {
        this(whiteboard);
        setPaint(paint);

        init();
    }

    public Triangle(Whiteboard whiteboard, Paint paint, String doodleId) {
        this(whiteboard);
        setDoodleId(doodleId);
        setPaint(paint);

        init();
    }

    private void init() {
        mTriangleCoordinates = new Vector<PointF>(3);
        mLineSegments = new LineSegment[3];

        //fill empty lineSegment
        mLineSegments[0] = new LineSegment();
        mLineSegments[1] = new LineSegment();
        mLineSegments[2] = new LineSegment();
    }

    @Override
    protected int initialCapacity() {
        return 2;
    }

    @Override
    public void addControlPoint(PointF point) {
        if (mPoints.isEmpty()) {
            mPoints.add(point);
        } else if (mPoints.size() == 1) {
            mPoints.add(point);

        } else if(mPoints.size() >= 2){
            mPoints.set(1, point);
        }

        if (mPoints.size() > 1) {
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);
            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mDoodleRect.set(x1, y1, x2, y2);
        }

        updateTriangleCoordinates();
    }

    @Override
    protected void changeShape(float touchX, float touchY) {

    }

    @Override
    protected double computeArea() {
        return 0;
    }

    @Override
    protected double computeEdgeLength() {
        return 0;
    }

    @Override
    public Path getScreenPath() {
        return null;
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        if (mTriangleCoordinates.size() == 3) {
            mDrawingPath.reset();

            mDrawingPath.moveTo(mTriangleCoordinates.get(0).x, mTriangleCoordinates.get(0).y);
            mDrawingPath.lineTo(mTriangleCoordinates.get(1).x, mTriangleCoordinates.get(1).y);
            mDrawingPath.lineTo(mTriangleCoordinates.get(2).x, mTriangleCoordinates.get(2).y);
            mDrawingPath.lineTo(mTriangleCoordinates.get(0).x, mTriangleCoordinates.get(0).y);
            mDrawingMatrix.postConcat(mTransformMatrix);
            mDrawingPath.transform(mDrawingMatrix);

            canvas.drawPath(mDrawingPath, getPaint());
        }
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y, this);
        }

        return false;
    }

    public synchronized void updateTriangleCoordinates() {
        try {
            if (mPoints.size() >= 2) {
                mTriangleCoordinates.clear();
                //等腰
//                mTriangleCoordinates.add(new PointF((mDoodleRect.left + mDoodleRect.right) / 2.0F, mDoodleRect.top));
//                mTriangleCoordinates.add(new PointF(mDoodleRect.left, mDoodleRect.bottom));
//                mTriangleCoordinates.add(new PointF(mDoodleRect.right, mDoodleRect.bottom));
                //直角
                PointF p1=mPoints.get(0);
                PointF p2=mPoints.get(1);
                mTriangleCoordinates.add(new PointF(p1.x,p1.y));
                mTriangleCoordinates.add(new PointF(p1.x,p2.y));
                mTriangleCoordinates.add(new PointF(p2.x,p1.y));
            }
        } catch (Exception e) {

        }
    }

    public LineSegment[] getLineSegments() {
        float stx;
        float sty;
        float edx;
        float edy;

        int size = mTriangleCoordinates.size();
        for (int i = 0; i < size; i++) {
            stx = mTriangleCoordinates.get(i).x;
            sty = mTriangleCoordinates.get(i).y;
            if (i == size - 1) {
                //last point,
                edx = mTriangleCoordinates.get(0).x;
                edy = mTriangleCoordinates.get(0).y;
            } else {
                edx = mTriangleCoordinates.get(i + 1).x;
                edy = mTriangleCoordinates.get(i + 1).y;
            }
            Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
            Utils.getLineSegment(stx, sty, edx, edy, matrix, mLineSegments[i]);
        }

        return mLineSegments;
    }

    @Override
    public void changeByEdge(float oldX, float oldY, float x, float y, int edge) {
        super.changeByEdge(oldX, oldY, x, y, edge);

        //update triangle points
        updateTriangleCoordinates();
    }

    @Override
    public Object onCollect(int type) {
        if(type== SyncGenerator.STATE_BEGIN){
            SyncBoardEvtBegin evtBegin=new SyncBoardEvtBegin();
            Ctx ctx=new Ctx();
            ctx.lineWidth=(int)getPaint().getStrokeWidth();
            ctx.strokeStyle= ColorUtil.getColorName(getPaint().getColor());
            ctx.viewport=getWhiteboard().getViewport();
            evtBegin.ctx=ctx;
            evtBegin.stg= Live.SyncStage.BEGIN;
            evtBegin.evt= Live.SyncEvent.TRIANGLE;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.TRIANGLE;
            evtFinished.time=System.currentTimeMillis();
            evtFinished.board= getWhiteboard().getWhiteBoardId();
            evtFinished.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            SyncData syncData=new SyncData();
            syncData.layer=new SyncLayer();
            evtFinished.data=syncData;
            syncData.layer.lineColor=ColorUtil.getColorName(getPaint().getColor());
            syncData.layer.lineWidth=(int)getPaint().getStrokeWidth();
            syncData.layer.shape=new Shape();
            RectF layerRect=new RectF();
            drawingMatrix.mapRect(layerRect,mDoodleRect);
            syncData.layer.id=getDoodleId();
            calculatePosition(syncData,drawingMatrix);
            syncData.layer.shape.height=layerRect.height();
            syncData.layer.shape.width=layerRect.width();
            syncData.layer.shape.left=layerRect.left;
            syncData.layer.shape.top=layerRect.top;
            syncData.layer.shape.data=getRealPoints(mDoodleRect.centerX(),mDoodleRect.centerY(),drawingMatrix);
            syncData.layer.shape.type=Live.ShapeType.DRAW_CONTINUOUS;
            return evtFinished;
        }
        return null;
    }

    private ArrayList<PointF> getRealPoints(float transX, float transY, Matrix drawingMatrix){
        ArrayList<PointF> dest=new ArrayList<>(4);
        dest.add(new PointF(mTriangleCoordinates.get(0).x, mTriangleCoordinates.get(0).y));
        dest.add(new PointF(mTriangleCoordinates.get(1).x, mTriangleCoordinates.get(1).y));
        dest.add(new PointF(mTriangleCoordinates.get(2).x, mTriangleCoordinates.get(2).y));
        dest.add(new PointF(mTriangleCoordinates.get(0).x, mTriangleCoordinates.get(0).y));

        float[] _p=new float[2];
        float[] p0=new float[2];
        Matrix matrix=new Matrix();
        matrix.postTranslate(-transX,-transY);
        matrix.postConcat(mTransformMatrix);
        matrix.postConcat(drawingMatrix);
        for(PointF p:dest){
            p0[0]=p.x;
            p0[1]=p.y;
            matrix.mapPoints(_p,p0);
            p.x=_p[0];
            p.y=_p[1];
        }

        return dest;
    }

    private void calculatePosition(SyncData syncData,Matrix matrix){
        float[] _p=new float[2];
        matrix.mapPoints(_p,new float[]{mPoints.get(0).x,mPoints.get(0).y});
        syncData.startPos=new PointF(_p[0],_p[1]);
        matrix.mapPoints(_p,new float[]{mPoints.get(1).x,mPoints.get(1).y});
        syncData.endPos=new PointF(_p[0],_p[1]);
    }

    @Override
    public Object onCollect(int action,int type) {
        if(type== SyncGenerator.STATE_BEGIN){

            SyncBoardEvtBegin evtBegin=new SyncBoardEvtBegin();
            Ctx ctx=new Ctx();
            ctx.lineWidth=(int)getPaint().getStrokeWidth();
            ctx.strokeStyle= ColorUtil.getColorName(getPaint().getColor());
            ctx.viewport=getWhiteboard().getViewport();
            evtBegin.ctx=ctx;
            evtBegin.stg= Live.SyncStage.BEGIN;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            if(action== Action.DELETE_ACTION){
                evtBegin.evt= Live.SyncEvent.ERASER;
            }
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            if(action== Action.DELETE_ACTION) {
                SyncBoardFinishedDelete evtFinished=new SyncBoardFinishedDelete();
                evtFinished.stg= Live.SyncStage.FINISH;
                evtFinished.evt= Live.SyncEvent.ERASER;
                evtFinished.time=System.currentTimeMillis();
                evtFinished.board= getWhiteboard().getWhiteBoardId();
                evtFinished.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
                evtFinished.data=new ArrayList<SyncLayer>();
                SyncLayer layer=new SyncLayer();
                layer.id=getDoodleId();
                evtFinished.data.add(layer);
                return evtFinished;
            }
        }
        return null;
    }

}
