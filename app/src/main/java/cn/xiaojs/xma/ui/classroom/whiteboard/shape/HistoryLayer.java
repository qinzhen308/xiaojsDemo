package cn.xiaojs.xma.ui.classroom.whiteboard.shape;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.whiteboard.Shape;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncLayerBuilder;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * created Paul Z on 2017/11/17
 *
 * 概述：
 * 历史图层数据，因为服务端指定了数据结构，但只能单向解析。
 * 即图层可格式化为json，json不能解析为对应图层，因此只能用新图层代替。
 *
 * 根据返回的点集(srcPoints)绘制，再次操作(matrixTransform)后，保存数据时，对srcPoints进行matrixTransform
 * 再保存。以上只是大致流程，具体绘制还要进行坐标转换，绘制流程参考Doodle.java
 *
 *
 *
 * 图解：
 * android_layer ------>json_layer_data ----/---> android_layer
 *                             |
 *                             |
 *                             |
 *                             |
 *                       HistoryLayer
 */
public class HistoryLayer extends Doodle implements SyncLayerBuilder{

    private SyncLayer mSrcLayer;


    public HistoryLayer(Whiteboard whiteboard) {
        super(whiteboard, STYLE_SYNC_LAYER);
    }

    public HistoryLayer(Whiteboard whiteboard, int style) {
        super(whiteboard, style);
    }



    @Override
    public void setControlPoints(List<PointF> src) {
        super.setControlPoints(src);
        float maxX=Float.MIN_VALUE;
        float minX=Float.MAX_VALUE;
        float maxY=Float.MIN_VALUE;
        float minY=Float.MAX_VALUE;
        if(ArrayUtil.isEmpty(src))return;
        for(PointF p:src){
            if(p==null)continue;
            if(p.x>maxX)maxX=p.x;
            if(p.x<minX)minX=p.x;
            if(p.y>maxY)maxY=p.y;
            if(p.y<minY)minY=p.y;
        }
        mDoodleRect.set(minX,minY,maxX,maxY);
    }

    public void setLayerSrc(SyncLayer srcLayer){
        mSrcLayer=srcLayer;
        setControlPoints(mSrcLayer.shape.data);
        mBorderRect.set(mDoodleRect);
    }

    @Override
    public void computeRectByCreate(PointF point) {

    }


    @Override
    public Path getScreenPath() {
        mScreenPath.reset();
        mScreenPath.set(mDrawingPath);
        mScreenPath.transform(mDisplayMatrix);
        return mScreenPath;
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y , this);
        }
        return false;
    }

    @Override
    protected void onDrawSelf(Canvas canvas) {
        List<PointF> points=mPoints;
        if(ArrayUtil.isEmpty(points)||points.size()<2){
            return;
        }
        mDrawingPath.reset();
        mTransRect.set(mDoodleRect);

        if(points.size()==2){
            PointF pc=new PointF((points.get(0).x+points.get(1).x)/2,(points.get(0).y+points.get(1).y)/2);
            points.add(1,pc);
        }

        boolean curContinue=false;
        for(int i=0;i<points.size();i++){
            PointF p1=points.get(i);
            if(p1!=null){
                if(curContinue){
                    mDrawingPath.lineTo(p1.x,p1.y);
                }else {
                    mDrawingPath.moveTo(p1.x,p1.y);
                }
                curContinue=true;
            }else {
                curContinue=false;
                continue;
            }
        }

        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath,getPaint());
    }


    @Override
    public SyncLayer onBuildLayer() {
        SyncLayer layer=new SyncLayer();
        layer.id=getDoodleId();
        layer.lineColor= ColorUtil.getColorName(getPaint().getColor());
        layer.lineWidth=(int)getPaint().getStrokeWidth();
        layer.shape=new Shape();
        RectF layerRect=new RectF();
        Matrix drawingMatrix=new Matrix();
        RectF vpRect=getWhiteboard().getViewport().buildRect();
        drawingMatrix.setRectToRect(new RectF(0,0,1,1),vpRect, Matrix.ScaleToFit.FILL);
        drawingMatrix.mapRect(layerRect,mDoodleRect);
        layer.shape.height=layerRect.height();
        layer.shape.width=layerRect.width();
        layer.shape.left=layerRect.left;
        layer.shape.top=layerRect.top;
        layer.shape.data=getRealPoints(mDoodleRect.centerX(),mDoodleRect.centerY(),drawingMatrix);
        layer.shape.type= mSrcLayer.shape.type;
        layer.complement();
        return layer;
    }

    private ArrayList<PointF> getRealPoints(float transX, float transY, Matrix drawingMatrix){
        ArrayList<PointF> dest=new ArrayList<>(mPoints.size());
        float[] _p=new float[2];
        float[] p0=new float[2];
        Matrix matrix=new Matrix();
        matrix.postTranslate(-transX,-transY);
        matrix.postConcat(drawingMatrix);
        matrix.postConcat(mTransformMatrix);
        for(PointF p:mPoints){
            p0[0]=p.x;
            p0[1]=p.y;
            matrix.mapPoints(_p,p0);
            dest.add(new PointF(_p[0],_p[1]));
        }
        return dest;
    }
}
