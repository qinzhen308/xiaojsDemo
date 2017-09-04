package cn.xiaojs.xma.ui.classroom.whiteboard.shape;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.RectF;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.ThreeDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.TwoDimensionalShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;
/**
 * created by Paul Z on 2017/8/25
 */

public class Square extends ThreeDimensionalShape {

    public Square(Whiteboard whiteboard , Paint paint) {
        super(whiteboard, GeometryShape.SQUARE);
        setPaint(paint);
    }

    public Square(Whiteboard whiteboard, Paint paint, String doodleId) {
        super(whiteboard, GeometryShape.SQUARE);
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

        Paint insidePaint=new Paint(getPaint());
        insidePaint.setPathEffect(new DashPathEffect(new float[]{WhiteboardConfigs.BORDER_DASH_WIDTH, WhiteboardConfigs.BORDER_DASH_WIDTH}, 0));

        RectF outsideRect=new RectF();
        RectF insideRect=new RectF();
        float dx=mTransRect.width()*0.147f;
        float dy=mTransRect.height()*0.147f;
        Matrix matrix=new Matrix();
        matrix.setScale(0.853f,0.853f,mTransRect.left,mTransRect.bottom);
        matrix.mapRect(outsideRect,mTransRect);
        matrix.reset();
        matrix.setTranslate(dx,-dy);
        matrix.mapRect(insideRect,outsideRect);
        mDrawingPath.addRect(outsideRect, Path.Direction.CCW);

        mDrawingPath.moveTo(insideRect.left,insideRect.top);
        mDrawingPath.lineTo(insideRect.right,insideRect.top);
        mDrawingPath.lineTo(insideRect.right,insideRect.bottom);

        mDrawingPath.moveTo(outsideRect.left,outsideRect.top);
        mDrawingPath.lineTo(insideRect.left,insideRect.top);
        mDrawingPath.moveTo(outsideRect.right,outsideRect.top);
        mDrawingPath.lineTo(insideRect.right,insideRect.top);
        mDrawingPath.moveTo(outsideRect.right,outsideRect.bottom);
        mDrawingPath.lineTo(insideRect.right,insideRect.bottom);


        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, getPaint());

        mDrawingPath.reset();
        mDrawingPath.moveTo(insideRect.right,insideRect.bottom);
        mDrawingPath.lineTo(insideRect.left,insideRect.bottom);
        mDrawingPath.lineTo(insideRect.left,insideRect.top);

        mDrawingPath.moveTo(outsideRect.left,outsideRect.bottom);
        mDrawingPath.lineTo(insideRect.left,insideRect.bottom);
        mDrawingPath.transform(mDrawingMatrix);
        canvas.drawPath(mDrawingPath, insidePaint);

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
    protected double computeVolume() {
        return 0;
    }
}
