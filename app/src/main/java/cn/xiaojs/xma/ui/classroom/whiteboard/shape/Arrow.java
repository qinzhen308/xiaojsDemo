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
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;


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


        RectF rectF=new RectF();
        mDrawingMatrix.mapRect(rectF,new RectF(0,0,1,1));

        PointF p1=mPoints.get(0);
        PointF p2=mPoints.get(1);
        float[] _p1=new float[2];
        float[] _p2=new float[2];
        mDrawingMatrix.mapPoints(_p1,new float[]{p1.x,p1.y});
        mDrawingMatrix.mapPoints(_p2,new float[]{p2.x,p2.y});
        float adx=(float) (Math.cos(Math.PI*30/180)*ARROW_LENGTH);


        float arrawHeight=ARROW_LENGTH/rectF.height();
        float arrawWidth=ARROW_LENGTH/rectF.width();

//        mDrawingPath.moveTo(_p1[0],_p1[1]);
//        mDrawingPath.lineTo(_p2[0],_p2[1]);
        float angle=(float) (180*Math.atan2((-_p2[1]+_p1[1]),(_p2[0]-_p1[0]))/Math.PI);
//        float ap1y=(float)(_p2[1]+Math.sin(Math.PI*(30-angle)/180)*ARROW_LENGTH);
//        float ap1x=(float)(_p2[0]-Math.cos(Math.PI*(30-angle)/180)*ARROW_LENGTH);
//        float ap2y=(float)(_p2[1]+Math.cos(Math.PI*(90-angle-30)/180)*ARROW_LENGTH);
//        float ap2x=(float)(_p2[0]-Math.sin(Math.PI*(90-angle-30)/180)*ARROW_LENGTH);
//        mDrawingPath.lineTo(ap1x,ap1y);
//        mDrawingPath.moveTo(_p2[0],_p2[1]);
//        mDrawingPath.lineTo(ap2x,ap2y);
//        mDrawingPath.lineTo(_p2[0],_p2[1]);

        Matrix matrix=new Matrix();
        RectF transRectInDrawing=new RectF();
        mDrawingMatrix.mapRect(transRectInDrawing,mTransRect);
//        matrix.setRotate(angle,transRectInDrawing.centerX(),transRectInDrawing.centerY());
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

//        matrix.setRotate(-angle,transRectInDrawing.centerX(),transRectInDrawing.centerY());
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

}
