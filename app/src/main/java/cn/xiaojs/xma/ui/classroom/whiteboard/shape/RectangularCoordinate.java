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

}
