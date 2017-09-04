package cn.xiaojs.xma.ui.classroom.whiteboard.shape;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.LineSegment;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;

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
        float adx=(float) (Math.cos(Math.PI*30/180)*mTransRect.width()*ARROW_LENGTH/rectF.width());
        PointF ap1=new PointF(mTransRect.right-adx,mTransRect.centerY()-ARROW_LENGTH/rectF.width()/2);
        PointF ap2=new PointF(mTransRect.right-adx,mTransRect.centerY()+ARROW_LENGTH/rectF.width()/2);
        mDrawingPath.lineTo(ap1.x,ap1.y);
        mDrawingPath.lineTo(ap2.x,ap2.y);
        mDrawingPath.lineTo(mTransRect.right,mTransRect.centerY());


        mDrawingPath.moveTo(mTransRect.centerX(),mTransRect.bottom);
        mDrawingPath.lineTo(mTransRect.centerX(),mTransRect.top);
        float ady=(float) (Math.cos(Math.PI*30/180)*mTransRect.height()*ARROW_LENGTH/rectF.height());
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

}
