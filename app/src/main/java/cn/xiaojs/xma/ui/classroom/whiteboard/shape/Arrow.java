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
 * created by Paul Z on 2017/8/29
 * 箭头
 */
public class Arrow extends TwoDimensionalShape {
    private final float ARROW_LENGTH=20;
    private int mOrientation = 0;


    public Arrow(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.ARROW);
        setPaint(paint);
    }

    public Arrow(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.ARROW);
        setDoodleId(doodleId);
        setPaint(paint);
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
        } else if (mPoints.size() >= 2) {
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
        mDrawingPath.reset();


        PointF p1=mPoints.get(0);
        PointF p2=mPoints.get(1);
        float[] _p1=new float[2];
        float[] _p2=new float[2];
        mDrawingMatrix.mapPoints(_p1,new float[]{p1.x,p1.y});
        mDrawingMatrix.mapPoints(_p2,new float[]{p2.x,p2.y});
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH);
        float angle=(float) (180*Math.atan2((-_p2[1]+_p1[1]),(_p2[0]-_p1[0]))/Math.PI);

        Matrix matrix=new Matrix();
        RectF transRectInDrawing=new RectF();
        mDrawingMatrix.mapRect(transRectInDrawing,mTransRect);
        matrix.setRotate(angle,_p1[0],_p1[1]);
        mDrawingPath.transform(matrix);
        float[] __p2=new float[2];
        matrix.mapPoints(__p2,_p2);
        mDrawingPath.moveTo(_p1[0],_p1[1]);
        mDrawingPath.lineTo(__p2[0],__p2[1]);
        PointF ap1=new PointF(__p2[0]- adx,__p2[1]-ARROW_LENGTH/2);
        PointF ap2=new PointF(__p2[0]- adx,__p2[1]+ARROW_LENGTH/2);
        mDrawingPath.lineTo(ap1.x,ap1.y);
        mDrawingPath.lineTo(ap2.x,ap2.y);
        mDrawingPath.lineTo(__p2[0],__p2[1]);

        matrix.setRotate(-angle,_p1[0],_p1[1]);
        mDrawingPath.transform(matrix);

        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mTransformMatrix);
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
            evtBegin.evt= Live.SyncEvent.ONEWAY_ARROW;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.ONEWAY_ARROW;
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
            syncData.layer.shape.height=layerRect.height();
            syncData.layer.shape.width=layerRect.width();
            syncData.layer.shape.left=layerRect.left;
            syncData.layer.shape.top=layerRect.top;
            syncData.layer.shape.data=getRealPoints(layerRect.centerX(),layerRect.centerY(),drawingMatrix);
            syncData.layer.shape.type=Live.ShapeType.DRAW_INTERVAL;
            return evtFinished;
        }
        return null;
    }

    private ArrayList<PointF> getRealPoints(float transX, float transY,Matrix drawingMatrix){
        ArrayList<PointF> dest=new ArrayList<>(6);
        PointF p1=mPoints.get(0);
        PointF p2=mPoints.get(1);
        float[] _p1=new float[2];
        float[] _p2=new float[2];
        drawingMatrix.mapPoints(_p1,new float[]{p1.x,p1.y});
        drawingMatrix.mapPoints(_p2,new float[]{p2.x,p2.y});
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH);
        float angle=(float) (180*Math.atan2((-_p2[1]+_p1[1]),(_p2[0]-_p1[0]))/Math.PI);
        dest.add(new PointF(_p1[0],_p1[1]));
        dest.add(new PointF(_p2[0],_p2[1]));
        Matrix matrix=new Matrix();
        RectF transRectInDrawing=new RectF();
        drawingMatrix.mapRect(transRectInDrawing,mTransRect);
        matrix.setRotate(angle,_p1[0],_p1[1]);
        float[] __p2=new float[2];
        matrix.mapPoints(__p2,_p2);
        PointF ap1=new PointF(__p2[0]- adx,__p2[1]-ARROW_LENGTH/2);
        PointF ap2=new PointF(__p2[0]- adx,__p2[1]+ARROW_LENGTH/2);
        float[] _ap1=new float[2];
        float[] _ap2=new float[2];
        Matrix matrixRIn=new Matrix();
        matrix.invert(matrixRIn);
        matrixRIn.mapPoints(_ap1,new float[]{ap1.x,ap1.y});
        matrixRIn.mapPoints(_ap2,new float[]{ap2.x,ap2.y});
        dest.add(new PointF(_ap1[0],_ap1[1]));
        dest.add(null);
        dest.add(new PointF(_p2[0],_p2[1]));
        dest.add(new PointF(_ap2[0],_ap2[1]));

        float[] _p=new float[2];
        float[] p0=new float[2];
        Matrix matrixCenter=new Matrix();
        matrixCenter.postTranslate(-transX,-transY);
        matrixCenter.postConcat(mTransformMatrix);
        for(PointF p:dest){
            if(p==null)continue;
            p0[0]=p.x;
            p0[1]=p.y;
            matrixCenter.mapPoints(_p,p0);
            p.x=_p[0];
            p.y=_p[1];
        }
        return dest;
    }

}
