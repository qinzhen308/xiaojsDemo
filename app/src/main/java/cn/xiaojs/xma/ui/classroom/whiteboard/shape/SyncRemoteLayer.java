package cn.xiaojs.xma.ui.classroom.whiteboard.shape;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * created Paul Z on 2017/9/5
 */
public class SyncRemoteLayer extends Doodle {


    private PointF mHorizontalVector = new PointF();
    private PointF mVerticalVector = new PointF();
    private PointF mCurrVector = new PointF();


    public SyncRemoteLayer(Whiteboard whiteboard) {
        super(whiteboard, -1);
    }


    @Override
    public void setControlPoints(ArrayList<PointF> src) {
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

    @Override
    public void addControlPoint(PointF point) {

    }

    @Override
    public Path getScreenPath() {
        return null;
    }

    @Override
    protected void onDrawSelf(Canvas canvas) {
        List<PointF> points=mPoints;
        if(ArrayUtil.isEmpty(points)||points.size()<2){
            return;
        }
        mDrawingPath.reset();
        mTransRect.set(mDoodleRect);

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
    public void changeByEdge(float oldX, float oldY, float x, float y, int edge) {
        updateDoodleRect(oldX, oldY, x, y, edge);
        //update control points
        updatePointByRect();
    }

    @Override
    public void changeByEdge(float deltaX, float deltaY, int edge) {
        updateDoodleRect(deltaX, deltaY, edge);
        //update control points
        updatePointByRect();
    }

    private void updateDoodleRect(float oldX, float oldY, float x, float y, int edge) {
        RectF drawingBounds = getWhiteboard().getParams().drawingBounds;
        PointF p = Utils.normalizeScreenPoint(x - oldX, y - oldY, drawingBounds);
        mCurrVector.x = p.x / mTotalScale;
        mCurrVector.y = p.y / mTotalScale;
        float degree = mTotalDegree % 360;

        //使用向量变换求的delta值
        mHorizontalVector.x = mDoodleRect.right - mDoodleRect.left;
        mHorizontalVector.y = 0;

        mVerticalVector.x = 0;
        mVerticalVector.y = mDoodleRect.bottom - mDoodleRect.top;

        Utils.mapRotateVector(mHorizontalVector, degree);
        Utils.mapRotateVector(mVerticalVector, degree);

        float deltaX = (float) Utils.vectorProjection(mCurrVector, mHorizontalVector);
        float deltaY = (float) Utils.vectorProjection(mCurrVector, mVerticalVector);

        updateDoodleRect(deltaX, deltaY, edge);
    }

    private void updateDoodleRect(float deltaX, float deltaY, int edge) {
        switch (edge) {
            case IntersectionHelper.TOP_EDGE:
                float top = mDoodleRect.top + deltaY;
                if (top < mDoodleRect.bottom) {
                    mDoodleRect.set(mDoodleRect.left, top, mDoodleRect.right, mDoodleRect.bottom);
                }
                break;
            case IntersectionHelper.RIGHT_EDGE:
                float right = mDoodleRect.right + deltaX;
                if (right > mDoodleRect.left) {
                    mDoodleRect.set(mDoodleRect.left, mDoodleRect.top , right, mDoodleRect.bottom);
                }
                break;
            case IntersectionHelper.BOTTOM_EDGE:
                float bottom = mDoodleRect.bottom + deltaY;
                if (bottom > mDoodleRect.top) {
                    mDoodleRect.set(mDoodleRect.left, mDoodleRect.top, mDoodleRect.right, bottom);
                }
                break;
            case IntersectionHelper.LEFT_EDGE:
                float left = mDoodleRect.left + deltaX;
                if (left < mDoodleRect.right) {
                    mDoodleRect.set(left, mDoodleRect.top, mDoodleRect.right, mDoodleRect.bottom);
                }
                break;
        }
    }

    protected void updatePointByRect() {

    }

}
