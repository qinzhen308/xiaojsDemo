package cn.xiaojs.xma.ui.classroom.whiteboard.shape;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;

/**
 * created by Paul Z on 2017/8/25
 */
public class ArcLine extends TwoDimensionalShape {
    private int mOrientation = 0;

    RectF srcRect =new RectF();

    boolean firstDrawing=true;

    private ArrayList<PointF> arcPoints=new ArrayList<>();

    public ArcLine(Whiteboard whiteboard, Paint paint) {
        super(whiteboard, GeometryShape.ARC_LINE);
        setPaint(paint);
    }

    public ArcLine(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.ARC_LINE);
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
        } else if(mPoints.size() >= 2){
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
            PointF p1=mPoints.get(0);
            PointF p2=mPoints.get(1);
            float[] _p1=new float[2];
            float[] _p2=new float[2];
            mDrawingMatrix.mapPoints(_p1,new float[]{p1.x,p1.y});
            mDrawingMatrix.mapPoints(_p2,new float[]{p2.x,p2.y});

            float r= Math.abs(_p1[0]-_p2[0]);
            int angle=(int) (180*Math.atan2((_p2[1]-_p1[1]),(_p2[0]-_p1[0]))/Math.PI);
            float centerX=_p1[0];
            float centerY=_p1[1];
            float left,top,right,bottom;
            right=centerX+r;
            if(angle<0&&angle>=-90){
                left=(float)( centerX+r*Math.cos(angle*Math.PI/180));
                top=(float) (centerY+r*Math.sin(angle*Math.PI/180));
                bottom=centerY;
            }else if(angle<-90&&angle>-180){
                left=(float)( centerX+r*Math.cos(angle*Math.PI/180));
                top=centerY-r;
                bottom=centerY;
            }else if(angle<=180&&angle>90){
                left=centerX-r;
                top=centerY-r;
                bottom=(float) (centerY+r*Math.sin(angle*Math.PI/180));
            }else{
                left=centerX-r;
                top=centerY-r;
                bottom=centerY+r;
            }
            RectF realRect=new RectF(left,top , right, bottom);
            //把实际rect映射到单元rect
            RectF drawingRect=new RectF();
            mDrawingMatrix.mapRect(drawingRect,new RectF(0,0,1,1));
            Matrix matrix=new Matrix();
            matrix.setRectToRect(drawingRect,new RectF(0,0,1,1), Matrix.ScaleToFit.FILL);
            matrix.mapRect(mDoodleRect,realRect);
            srcRect.set(mDoodleRect);
//            mDoodleRect.set(left,top , right, bottom);
        }
        firstDrawing=true;
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
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mTransRect.set(mDoodleRect);

        mDrawingPath.reset();

        if(!firstDrawing){
            if(arcPoints.size()>1){
                Matrix matrix=new Matrix();
                matrix.setRectToRect(srcRect,mDoodleRect, Matrix.ScaleToFit.FILL);
                /*float[] temP=new float[2];
                PointF p=arcPoints.get(0);
                matrix.mapPoints(temP,new float[]{p.x,p.y});
                mDrawingPath.moveTo(temP[0],temP[1]);
                for(int i=1;i<arcPoints.size();i++){
                    p=arcPoints.get(i);
                    matrix.mapPoints(temP,new float[]{p.x,p.y});
                    mDrawingPath.lineTo(temP[0],temP[1]);
                }*/
                float[] temPs=new float[arcPoints.size()*2];
                for(int i=0;i<arcPoints.size();i++) {
                    PointF p=arcPoints.get(i);
                    temPs[i*2]=p.x;
                    temPs[i*2+1]=p.y;
                }
                float[] destPs=new float[arcPoints.size()*2];
                matrix.mapPoints(destPs,temPs);
                mDrawingPath.moveTo(destPs[0],destPs[1]);
                for(int i=1;i<arcPoints.size();i++) {
                    mDrawingPath.lineTo(destPs[2*i],destPs[2*i+1]);
                }
            }
            mDrawingMatrix.postConcat(mTransformMatrix);
            mDrawingPath.transform(mTransformMatrix);
            canvas.drawPath(mDrawingPath, getPaint());

            canvas.restore();
            return;
        }else {
            arcPoints.clear();
        }

        RectF drawingRect=new RectF();
        mDrawingMatrix.mapRect(drawingRect,mTransRect);

        PointF p1=mPoints.get(0);
        PointF p2=mPoints.get(1);
        float[] _p1=new float[2];
        float[] _p2=new float[2];
        mDrawingMatrix.mapPoints(_p1,new float[]{p1.x,p1.y});
        mDrawingMatrix.mapPoints(_p2,new float[]{p2.x,p2.y});


//        float r=(float) Math.sqrt(Math.pow(_p1[0]-_p2[0],2)+Math.pow(_p1[1]-_p2[1],2));
        float r= Math.abs(_p1[0]-_p2[0]);
        int angle=(int) (180*Math.atan2((_p2[1]-_p1[1]),(_p2[0]-_p1[0]))/Math.PI);
        if(angle<0){
            angle=360+angle;
        }
//        int angle=(int)(arc*180/Math.PI);
        int start=360;
        mDrawingPath.moveTo((float)( _p1[0]+r*Math.cos(start*Math.PI/180)),(float) (_p1[1]+r*Math.sin(start*Math.PI/180)));
        for(int i=start;i>=angle;i--){
            PointF p=new PointF((float)( _p1[0]+r*Math.cos(i*Math.PI/180)),(float) (_p1[1]+r*Math.sin(i*Math.PI/180)));
            arcPoints.add(p);
            mDrawingPath.lineTo(p.x,p.y);
//            mDrawingPath.lineTo((float)( _p1[0]+r*Math.cos(i*Math.PI/180)),(float) (_p1[1]+r*Math.sin(i*Math.PI/180)));
        }


        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mTransformMatrix);
        canvas.drawPath(mDrawingPath, getPaint());



        canvas.restore();
    }

    @Override
    public Path getScreenPath() {
        mScreenPath.reset();
        mTransRect.set(mDoodleRect);
        mScreenPath.addOval(mTransRect, Path.Direction.CCW);
        mScreenPath.transform(mDrawingMatrix);
        mScreenPath.transform(mDisplayMatrix);
        return mScreenPath;
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            mTransRect.set(mDoodleRect);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            return IntersectionHelper.checkOvalFramePress(p.x, p.y, mTransRect, matrix);
        }

        return false;
    }


    @Override
    public void updatePointByRect() {
        firstDrawing=false;
        ////update control points
       /* switch (mOrientation){
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
        }*/
    }
}
