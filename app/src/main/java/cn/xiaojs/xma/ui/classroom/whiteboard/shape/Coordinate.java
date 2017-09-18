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
import cn.xiaojs.xma.ui.classroom.whiteboard.core.LineSegment;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinished;

/**
 * created by Paul Z on 2017/8/30
 * 二维坐标系
 */
public class Coordinate extends TwoDimensionalShape {
    LineSegment xLineSegment;
    LineSegment yLineSegment;

    private final float ARROW_LENGTH=20;
    private final float UNIT_SCALE=100;//px
    private final float UNIT_SCALE_LENGTH=20;//px

    public Coordinate(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.COORDINATE);
        setPaint(paint);
        init();
    }

    public Coordinate(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.COORDINATE);
        setDoodleId(doodleId);
        setPaint(paint);
        init();
    }

    private void init() {
        xLineSegment = new LineSegment();
        yLineSegment = new LineSegment();
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
        return null;
    }


    public LineSegment getXLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), mPoints.get(1), matrix, xLineSegment);
    }
    public LineSegment getYLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), mPoints.get(1), matrix, yLineSegment);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mTransRect.set(mDoodleRect);
        mDrawingPath.reset();

        RectF rectF=new RectF();
        mDrawingMatrix.mapRect(rectF,new RectF(0,0,1,1));

        mDrawingPath.moveTo(mTransRect.left,mTransRect.centerY());
        mDrawingPath.lineTo(mTransRect.right,mTransRect.centerY());
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH/rectF.width());
        PointF ap1=new PointF(mTransRect.right-adx,mTransRect.centerY()-ARROW_LENGTH/rectF.width()/2);
        PointF ap2=new PointF(mTransRect.right-adx,mTransRect.centerY()+ARROW_LENGTH/rectF.width()/2);
        mDrawingPath.lineTo(ap1.x,ap1.y);
        mDrawingPath.lineTo(ap2.x,ap2.y);
        mDrawingPath.lineTo(mTransRect.right,mTransRect.centerY());


        mDrawingPath.moveTo(mTransRect.centerX(),mTransRect.bottom);
        mDrawingPath.lineTo(mTransRect.centerX(),mTransRect.top);
        float ady=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH/rectF.height());
        ap1=new PointF(mTransRect.centerX()-ARROW_LENGTH/rectF.width()/2,mTransRect.top+ady);
        ap2=new PointF(mTransRect.centerX()+ARROW_LENGTH/rectF.width()/2,mTransRect.top+ady);
        mDrawingPath.lineTo(ap1.x,ap1.y);
        mDrawingPath.lineTo(ap2.x,ap2.y);
        mDrawingPath.lineTo(mTransRect.centerX(),mTransRect.top);


        float scaleH=UNIT_SCALE/rectF.height();
        float scaleW=UNIT_SCALE/rectF.width();
        float scaleHL=UNIT_SCALE_LENGTH/rectF.height();
        float scaleWL=UNIT_SCALE_LENGTH/rectF.width();
        float px=mTransRect.centerX()+scaleW;
        float py=mTransRect.centerY();
        while (px<mTransRect.right){
            mDrawingPath.moveTo(px,py);
            mDrawingPath.lineTo(px,py-scaleHL);
            mDrawingPath.moveTo(2*mTransRect.centerX()-px,py);
            mDrawingPath.lineTo(2*mTransRect.centerX()-px,py-scaleHL);
            px+=scaleW;
        }
        px=mTransRect.centerX();
        py=mTransRect.centerY()+scaleH;
        while (py<mTransRect.bottom){
            mDrawingPath.moveTo(px,py);
            mDrawingPath.lineTo(px+scaleWL,py);
            mDrawingPath.moveTo(px,2*mTransRect.centerY()-py);
            mDrawingPath.lineTo(px+scaleWL,2*mTransRect.centerY()-py);
            py+=scaleH;
        }


        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());

        canvas.restore();
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y , this);
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
            evtBegin.evt= Live.SyncEvent.COORDINATE;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.COORDINATE;
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
            syncData.layer.shape.data=getRealPoints(layerRect);
            syncData.layer.shape.type=Live.ShapeType.DRAW_INTERVAL;
            return evtFinished;
        }
        return null;
    }

    private ArrayList<PointF> getRealPoints(RectF layerRect){
        ArrayList<PointF> dest=new ArrayList<>();
        //x轴
        dest.add(new PointF(-layerRect.width()/2,0));
        dest.add(new PointF(layerRect.width()/2,0));
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH);
        PointF ap1=new PointF(layerRect.width()/2-adx,-ARROW_LENGTH/2);
        PointF ap2=new PointF(layerRect.width()/2-adx,ARROW_LENGTH/2);
        dest.add(ap1);
        dest.add(ap2);
        dest.add(new PointF(layerRect.width()/2,0));
        dest.add(null);

        //y轴
        dest.add(new PointF(0,layerRect.height()/2));
        dest.add(new PointF(0,-layerRect.height()/2));
        float ady=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH);
        ap1=new PointF(-ARROW_LENGTH/2,-layerRect.height()/2+ady);
        ap2=new PointF(ARROW_LENGTH/2,-layerRect.height()/2+ady);
        dest.add(ap1);
        dest.add(ap2);
        dest.add(new PointF(0,-layerRect.height()/2));
        dest.add(null);

        float scaleH=UNIT_SCALE;
        float scaleW=UNIT_SCALE;
        float scaleHL=UNIT_SCALE_LENGTH;
        float scaleWL=UNIT_SCALE_LENGTH;
        float px=scaleW;
        float py=0;
        while (px<layerRect.width()/2){
            dest.add(new PointF(px,py));
            dest.add(new PointF(px,py-scaleHL));
            dest.add(null);
            dest.add(new PointF(-px,py));
            dest.add(new PointF(-px,py-scaleHL));
            dest.add(null);
            px+=scaleW;
        }
        px=0;
        py=scaleH;
        while (py<layerRect.height()/2){
            dest.add(new PointF(px,py));
            dest.add(new PointF(px+scaleWL,py));
            dest.add(null);
            dest.add(new PointF(px,-py));
            dest.add(new PointF(px+scaleWL,-py));
            dest.add(null);
            py+=scaleH;
        }
        if(dest.get(dest.size()-1)==null){
            dest.remove(dest.size()-1);
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


}
