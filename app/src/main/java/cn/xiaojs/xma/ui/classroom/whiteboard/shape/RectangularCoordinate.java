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
 * 直角坐标系
 */
public class RectangularCoordinate extends TwoDimensionalShape {
    LineSegment xLineSegment;
    LineSegment yLineSegment;

    private final float ARROW_LENGTH=20;
    private final float UNIT_SCALE=100;//px
    private final float UNIT_SCALE_LENGTH=20;//px


    private int mOrientation = 0;


    public RectangularCoordinate(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.RECTANGULAR_COORDINATE);
        setPaint(paint);
        init();
    }

    public RectangularCoordinate(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.RECTANGULAR_COORDINATE);
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
        return null;
    }


    public LineSegment getXLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), new PointF(mPoints.get(1).x,mPoints.get(0).y), matrix, xLineSegment);
    }
    public LineSegment getYLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), new PointF(mPoints.get(0).x,mPoints.get(1).y), matrix, yLineSegment);
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

        PointF p1=null;
        PointF p2=null;
        p1=mPoints.get(0);
        p2=mPoints.get(1);


        mDrawingPath.moveTo(p1.x,p1.y);
        mDrawingPath.lineTo(p2.x,p1.y);
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH/rectF.width())*(p1.x-p2.x<=0?1:-1);
        PointF ap1=new PointF(p2.x-adx,p1.y-ARROW_LENGTH/rectF.width()/2);
        PointF ap2=new PointF(p2.x-adx,p1.y+ARROW_LENGTH/rectF.width()/2);
        mDrawingPath.lineTo(ap1.x,ap1.y);
        mDrawingPath.lineTo(ap2.x,ap2.y);
        mDrawingPath.lineTo(p2.x,p1.y);


        //y轴
        mDrawingPath.moveTo(p1.x,p1.y);
        mDrawingPath.lineTo(p1.x,p2.y);
        float ady=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH/rectF.height())*(p1.y-p2.y<=0?-1:1);
        ap1=new PointF(p1.x-ARROW_LENGTH/rectF.width()/2,p2.y+ady);
        ap2=new PointF(p1.x+ARROW_LENGTH/rectF.width()/2,p2.y+ady);
        mDrawingPath.lineTo(ap1.x,ap1.y);
        mDrawingPath.lineTo(ap2.x,ap2.y);
        mDrawingPath.lineTo(p1.x,p2.y);


        float scaleH=UNIT_SCALE/rectF.height();
        float scaleW=UNIT_SCALE/rectF.width();
        float scaleHL=UNIT_SCALE_LENGTH/rectF.height();
        float scaleWL=UNIT_SCALE_LENGTH/rectF.width();
        float dx=scaleW;
        float dy=scaleH;
        int xOri=p1.x-p2.x<=0?1:-1;
        int yOri=p1.y-p2.y<=0?1:-1;
        while (dx<mTransRect.width()){
            mDrawingPath.moveTo(p1.x+xOri*dx,p1.y);
            mDrawingPath.lineTo(p1.x+xOri*dx,p1.y+yOri*scaleHL);
            dx+=scaleW;
        }
        while (dy<mTransRect.height()){
            mDrawingPath.moveTo(p1.x,p1.y+yOri*dy);
            mDrawingPath.lineTo(p1.x+xOri*scaleWL,p1.y+yOri*dy);
            dy+=scaleH;
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

    /*@Override
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
    }*/



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
            evtBegin.evt= Live.SyncEvent.RECTANGULAR_COORDINATES;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.RECTANGULAR_COORDINATES;
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
        int orienX=mOrientation==1||mOrientation==4?-1:1;
        int orienY=mOrientation==1||mOrientation==2?-1:1;
        dest.add(new PointF(orienX*layerRect.width()/2,orienY*layerRect.height()/2));
        dest.add(new PointF(-orienX*layerRect.width()/2,orienY*layerRect.height()/2));
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH*orienX);
        PointF ap1=new PointF(-orienX*layerRect.width()/2+adx,orienY*layerRect.height()/2-ARROW_LENGTH/2);
        PointF ap2=new PointF(-orienX*layerRect.width()/2+adx,orienY*layerRect.height()/2+ARROW_LENGTH/2);
        dest.add(ap1);
        dest.add(ap2);
        dest.add(new PointF(-orienX*layerRect.width()/2,orienY*layerRect.height()/2));
        dest.add(null);

        //y轴
        dest.add(new PointF(orienX*layerRect.width()/2,orienY*layerRect.height()/2));
        dest.add(new PointF(orienX*layerRect.width()/2,-orienY*layerRect.height()/2));
        float ady=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH*orienY);
        ap1=new PointF(orienX*layerRect.width()/2-ARROW_LENGTH/2,-orienY*layerRect.height()/2+ady);
        ap2=new PointF(orienX*layerRect.width()/2+ARROW_LENGTH/2,-orienY*layerRect.height()/2+ady);
        dest.add(ap1);
        dest.add(ap2);
        dest.add(new PointF(orienX*layerRect.width()/2,-orienY*layerRect.height()/2));
        dest.add(null);



        PointF p1=new PointF();
        PointF p2=new PointF();

        switch (mOrientation){
            case 1://屏幕坐标系一象限，即右下
                p1.set(-layerRect.width()/2, layerRect.height()/2);
                p2.set(layerRect.width()/2, -layerRect.height()/2);
                break;
            case 2://屏幕坐标系二象限，即左下
                p1.set(layerRect.width()/2, layerRect.height()/2);
                p2.set(-layerRect.width()/2, -layerRect.height()/2);
                break;
            case 3://屏幕坐标系三象限，即左上
                p1.set(layerRect.width()/2, -layerRect.height()/2);
                p2.set(-layerRect.width()/2, layerRect.height()/2);
                break;
            case 4://屏幕坐标系四象限，即右上
                p1.set(-layerRect.width()/2, -layerRect.height()/2);
                p2.set(layerRect.width()/2, layerRect.height()/2);
                break;
        }

        float scaleH=UNIT_SCALE;
        float scaleW=UNIT_SCALE;
        float scaleHL=UNIT_SCALE_LENGTH;
        float scaleWL=UNIT_SCALE_LENGTH;
        float dx=scaleW;
        float dy=scaleH;
        int xOri=p1.x-p2.x<=0?1:-1;
        int yOri=p1.y-p2.y<=0?1:-1;
        while (dx<layerRect.width()){
            dest.add(new PointF(p1.x+xOri*dx,-p1.y));
            dest.add(new PointF(p1.x+xOri*dx,-p1.y-yOri*scaleHL));
            dest.add(null);
            dx+=scaleW;
        }
        while (dy<layerRect.height()){
            dest.add(new PointF(p1.x,-p1.y-yOri*dy));
            dest.add(new PointF(p1.x+xOri*scaleWL,-p1.y-yOri*dy));
            dest.add(null);
            dy+=scaleH;
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
