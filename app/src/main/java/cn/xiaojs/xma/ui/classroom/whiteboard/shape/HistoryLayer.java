package cn.xiaojs.xma.ui.classroom.whiteboard.shape;


import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
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
public class HistoryLayer extends Doodle {

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
        SyncLayer mSrcLayer=srcLayer;
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



}
