package cn.xiaojs.xma.ui.classroom.whiteboard.shape;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;

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
 * 正弦曲线
 */
public class SineCurve extends TwoDimensionalShape {

    private final float UNIT=(float)( Math.PI*8);

    public SineCurve(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.SINE_CURVE);
        setPaint(paint);
    }

    public SineCurve(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.SINE_CURVE);
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
        mDrawingPath.reset();
//        Matrix matrix=new Matrix();
//        matrix.postTranslate(-mTransRect.left,-mTransRect.top-mTransRect.height()/2);
//        mDrawingPath.transform(matrix);

        mDrawingPath.moveTo(mTransRect.left,mTransRect.centerY());

        float amplitude=mTransRect.height()/2;
        for(float i = mTransRect.left+0.001f; i<=mTransRect.right; i+=0.001f){
            mDrawingPath.lineTo(i,(float)( mTransRect.centerY()-amplitude*Math.sin((i-mTransRect.left)*UNIT)));
        }

//        matrix.postTranslate(mTransRect.left,mTransRect.top+mTransRect.height()/2);
//        mDrawingPath.transform(matrix);

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
            evtBegin.evt= Live.SyncEvent.SINCOS_LINE;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.SINCOS_LINE;
            evtFinished.time=System.currentTimeMillis();
            evtFinished.board= getWhiteboard().getWhiteBoardId();
            evtFinished.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            SyncData syncData=new SyncData();
            syncData.layer=onBuildLayer();
            evtFinished.data=syncData;
            RectF layerRect=new RectF();
            drawingMatrix.mapRect(layerRect,mDoodleRect);
            syncData.startPos=new PointF(layerRect.left,layerRect.top);
            syncData.endPos=new PointF(layerRect.right,layerRect.bottom);
            return evtFinished;
        }
        return null;
    }

    private ArrayList<PointF> getRealPoints(float transX, float transY, Matrix drawingMatrix){
        ArrayList<PointF> dest=new ArrayList<>();
        dest.add(new PointF(mDoodleRect.left,mDoodleRect.centerY()));

        float amplitude=mDoodleRect.height()/2;
        for(float i = mDoodleRect.left+0.001f; i<=mDoodleRect.right; i+=0.001f){
            dest.add(new PointF(i,(float)( mDoodleRect.centerY()-amplitude*Math.sin((i-mDoodleRect.left)*UNIT))));
        }

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

    @Override
    public SyncLayer onBuildLayer() {
        SyncLayer layer=new SyncLayer();
        layer.lineColor=ColorUtil.getColorName(getPaint().getColor());
        layer.lineWidth=(int)getPaint().getStrokeWidth();
        layer.shape=new Shape();
        RectF layerRect=new RectF();
        getDrawingMatrixFromWhiteboard().mapRect(layerRect,mDoodleRect);
        layer.id=getDoodleId();
        layer.shape.height=layerRect.height();
        layer.shape.width=layerRect.width();
        layer.shape.left=layerRect.left;
        layer.shape.top=layerRect.top;
        layer.shape.data=getRealPoints(mDoodleRect.centerX(),mDoodleRect.centerY(),getDrawingMatrixFromWhiteboard());
        layer.shape.type=Live.ShapeType.DRAW_CONTINUOUS;
        layer.complement();
        return layer;
    }
}
