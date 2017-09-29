package cn.xiaojs.xma.ui.classroom.whiteboard.shape;

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
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinished;


/**
 * Created by Paul Z on 2017/5/12.
 */
public class Pentagon extends TwoDimensionalShape {

    public Pentagon(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.PENTAGON);
        setPaint(paint);
    }

    public Pentagon(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.PENTAGON);
        setDoodleId(doodleId);
        setPaint(paint);
    }

    @Override
    protected int initialCapacity() {
        return 2;
    }

    @Override
    public void computeRectByCreate(PointF point) {
        if (mPoints.isEmpty()) {
            mPoints.add(point);
        } else if (mPoints.size() == 1) {
            mPoints.add(point);
        } else if (mPoints.size() >= 2) {
            mPoints.set(1, point);
        }

        if (mPoints.size() > 1) {
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);
            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mDoodleRect.set(x1, y1, x2, y2);
        }
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
        mScreenPath.reset();
        mTransRect.set(mDoodleRect);
        mScreenPath.addRect(mTransRect, Path.Direction.CCW);
        mScreenPath.transform(mDrawingMatrix);
        mScreenPath.transform(mDisplayMatrix);
        return mScreenPath;
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mTransRect.set(mDoodleRect);
        float dx=(float)( mTransRect.width()/(2+1/Math.sin(Math.PI*18/180)));
        float dy=(float)(mTransRect.height()*Math.tan(Math.PI*36/180)/2);
        mDrawingPath.reset();
        mDrawingPath.moveTo(mTransRect.centerX(),mTransRect.top);
        mDrawingPath.lineTo(mTransRect.right,mTransRect.top+dy);
        mDrawingPath.lineTo(mTransRect.right-dx,mTransRect.bottom);
        mDrawingPath.lineTo(mTransRect.left+dx,mTransRect.bottom);
        mDrawingPath.lineTo(mTransRect.left,mTransRect.top+dy);
        mDrawingPath.lineTo(mTransRect.centerX(),mTransRect.top);


        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());

        canvas.restore();
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            return IntersectionHelper.isRectFramePressed(p.x, p.y, mTransRect, matrix);
        }

        return false;
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
            evtBegin.evt= Live.SyncEvent.PENTAGON;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.PENTAGON;
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
            syncData.startPos=new PointF(layerRect.left,layerRect.top);
            syncData.endPos=new PointF(layerRect.right,layerRect.bottom);
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
        ArrayList<PointF> dest=new ArrayList<>(6);
        float dx=(float)( mDoodleRect.width()/(2+1/Math.sin(Math.PI*18/180)));
        float dy=(float)(mDoodleRect.height()*Math.tan(Math.PI*36/180)/2);
        dest.add(new PointF(mDoodleRect.centerX(),mDoodleRect.top));
        dest.add(new PointF(mDoodleRect.right,mDoodleRect.top+dy));
        dest.add(new PointF(mDoodleRect.right-dx,mDoodleRect.bottom));
        dest.add(new PointF(mDoodleRect.left+dx,mDoodleRect.bottom));
        dest.add(new PointF(mDoodleRect.left,mDoodleRect.top+dy));
        dest.add(new PointF(mDoodleRect.centerX(),mDoodleRect.top));


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

}
