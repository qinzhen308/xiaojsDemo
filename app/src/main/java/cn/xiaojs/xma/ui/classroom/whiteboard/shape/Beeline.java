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
 * Date:2016/10/18
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.Shape;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.model.socket.room.whiteboard.Viewport;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.LineSegment;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtGoing;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinished;

public class Beeline extends TwoDimensionalShape {
    /**
     * 如果直线的第一个控制点坐标值比第二个控制点坐标值小，则表示该直线是正向
     */

    private LineSegment mLineSegment;
    private int mOrientation = 0;

    private Beeline(Whiteboard whiteboard) {
        super(whiteboard, GeometryShape.BEELINE);
    }

    public Beeline(Whiteboard whiteboard, Paint paint) {
        this(whiteboard);
        setPaint(paint);

        init();
    }

    public Beeline(Whiteboard whiteboard, Paint paint, String doodleId) {
        this(whiteboard);
        setDoodleId(doodleId);
        setPaint(paint);

        init();
    }

    private void init() {
        mLineSegment = new LineSegment();
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
            if( mPoints.get(1).x >=  mPoints.get(0).x){
                if(mPoints.get(1).y >=  mPoints.get(0).y){
                    mOrientation=1;
                }else {
                    mOrientation=4;
                }
            }else {
                if(mPoints.get(1).y >=  mPoints.get(0).y){
                    mOrientation=2;
                }else {
                    mOrientation=3;
                }
            }
            mDoodleRect.set(x1, y1, x2, y2);
        }
    }

    @Override
    public Path getScreenPath() {
        return null;
    }

    public LineSegment getLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), mPoints.get(1), matrix, mLineSegment);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mDrawingPath.reset();

        mDrawingPath.moveTo(mPoints.get(0).x, mPoints.get(0).y);
        mDrawingPath.lineTo(mPoints.get(1).x, mPoints.get(1).y);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);

        canvas.drawPath(mDrawingPath, getPaint());

        canvas.restore();
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
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y , this);
        }

        return false;

    }

    @Override
    public void updatePointByRect() {
        ////update control points
        switch (mOrientation){
            case 1://屏幕坐标系一象限，即右下
                mPoints.get(0).set(mDoodleRect.left, mDoodleRect.top);
                mPoints.get(1).set(mDoodleRect.right, mDoodleRect.bottom);
                break;
            case 2://屏幕坐标系二象限，即左下
                mPoints.get(0).set(mDoodleRect.right, mDoodleRect.top);
                mPoints.get(1).set(mDoodleRect.left, mDoodleRect.bottom);
                break;
            case 3://屏幕坐标系三象限，即左上
                mPoints.get(0).set(mDoodleRect.right, mDoodleRect.bottom);
                mPoints.get(1).set(mDoodleRect.left, mDoodleRect.top);
                break;
            case 4://屏幕坐标系四象限，即右上
                mPoints.get(0).set(mDoodleRect.left, mDoodleRect.bottom);
                mPoints.get(1).set(mDoodleRect.right, mDoodleRect.top);
                break;
        }
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
            evtBegin.evt= Live.SyncEvent.LINE;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.LINE;
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
            mDisplayMatrix.mapRect(layerRect,mDoodleRect);
            syncData.layer.id=getDoodleId();
            syncData.layer.shape.height=layerRect.height();
            syncData.layer.shape.width=layerRect.width();
            syncData.layer.shape.left=layerRect.left;
            syncData.layer.shape.top=layerRect.top;
            syncData.layer.shape.data=getRealPoints(mDoodleRect.centerX(),mDoodleRect.centerY());
            syncData.layer.shape.type=Live.ShapeType.DRAW_CONTINUOUS;
            return evtFinished;
        }
        return null;
    }

    private ArrayList<PointF> getRealPoints(float transX,float transY){
        ArrayList<PointF> dest=new ArrayList<>(mPoints.size());
        float[] _p=new float[2];
        float[] p0=new float[2];
        Matrix matrix=new Matrix();
        matrix.postTranslate(-transX,-transY);
        matrix.postConcat(mTransformMatrix);
        matrix.postConcat(mDrawingMatrix);
        for(PointF p:mPoints){
            p0[0]=p.x;
            p0[1]=p.y;
            matrix.mapPoints(_p,p0);
            dest.add(new PointF(_p[0],_p[1]));
        }
        return dest;
    }


}
