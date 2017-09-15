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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.Shape;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncCollector;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtGoing;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinished;

public class HandWriting extends Doodle implements SyncCollector{
    private Path mNormalizedPath;

    private HandWriting(Whiteboard whiteboard) {
        super(whiteboard, Doodle.STYLE_HAND_WRITING);
    }

    public HandWriting(Whiteboard whiteboard, Paint paint) {
        this(whiteboard);
        setPaint(paint);
        init();
    }

    public HandWriting(Whiteboard whiteboard, Paint paint, String doodleId) {
        this(whiteboard);
        setDoodleId(doodleId);
        setPaint(paint);
        init();
    }

    private void init() {
        mNormalizedPath = new Path();
    }

    private void setFirstPoint(float x, float y) {
        addControlPoint(x, y);
        mNormalizedPath.moveTo(x, y);
    }

    /**
     * Path.quadTo(): If no moveTo() call has been made for this contour, the first point is
     * automatically set to (0,0)
     */
    @Override
    public void addControlPoint(PointF point) {
        if (!mPoints.isEmpty()) {
            PointF last = mPoints.get(mPoints.size() - 1);
            mNormalizedPath.quadTo(last.x, last.y, (last.x + point.x) / 2, (last.y + point.y) / 2);
            mNormalizedPath.computeBounds(mDoodleRect, true);
        } else {
            mNormalizedPath.moveTo(point.x, point.y);
        }
        mPoints.add(point);
    }

    @Override
    public void setDrawingMatrix(Matrix matrix) {
        super.setDrawingMatrix(matrix);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        canvas.save();
        mDrawingPath.set(mNormalizedPath);
        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());
        canvas.restore();
    }

    @Override
    public Path getScreenPath() {
        mScreenPath.reset();
        mScreenPath.set(mNormalizedPath);
        mScreenPath.transform(mDrawingMatrix);
        mScreenPath.transform(mDisplayMatrix);
        return mScreenPath;
    }

    @Override
    public int onCheckPressedRegion(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mTransRect.set(mDoodleRect);
            int corner = IntersectionHelper.whichCornerPressed(p.x, p.y, mTransRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            long s = System.currentTimeMillis();
            boolean intersect = IntersectionHelper.intersect(x, y, this);
            return intersect;
        }

        return false;
    }

    @Override
    public RectF getDoodleScreenRect() {
        mDrawingPath.computeBounds(mTransRect, true);
        mDisplayMatrix.mapRect(mTransRect);
        return mTransRect;
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
            evtBegin.evt= Live.SyncEvent.PEN;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){
            if(mPoints==null||mPoints.size()<2)return null;
            SyncBoardEvtGoing evtGoing=new SyncBoardEvtGoing();
            evtGoing.stg= Live.SyncStage.ONGOING;
            evtGoing.evt= Live.SyncEvent.PEN;
            evtGoing.time=System.currentTimeMillis();
            evtGoing.board= getWhiteboard().getWhiteBoardId();
            evtGoing.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            evtGoing.data=new SyncBoardEvtGoing.GoingPoints();
            evtGoing.data.startPos=matrixToRealP(mPoints.get(mPoints.size()-1));
            evtGoing.data.endPos=matrixToRealP(mPoints.get(mPoints.size()-2));
            return evtGoing;
        }else if(type== SyncGenerator.STATE_FINISHED){
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.PEN;
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
            mDrawingMatrix.mapRect(layerRect,mDoodleRect);
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

    private PointF matrixToRealP(PointF p){
        float[] _p=new float[2];
        float[] p0=new float[2];
        p0[0]=p.x;
        p0[1]=p.y;
        mDrawingMatrix.mapPoints(_p,p0);
        PointF dest=new PointF(_p[0],_p[1]);
        return dest;
    }
}
