package cn.xiaojs.xma.ui.classroom.whiteboard.shape;


import android.graphics.Canvas;
import android.graphics.DashPathEffect;
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
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinished;

/**
 * created by Paul Z on 2017/8/30
 * 虚线
 */
public class Dashline extends TwoDimensionalShape {
    /**
     * 如果直线的第一个控制点坐标值比第二个控制点坐标值小，则表示该直线是正向
     */
    private static final int FORWARD = 0;
    private static final int REVERSE = 1;

    private LineSegment mLineSegment;
    private int mOrientation = FORWARD;

    private Dashline(Whiteboard whiteboard) {
        super(whiteboard, GeometryShape.DASH_LINE);
    }

    public Dashline(Whiteboard whiteboard, Paint paint) {
        this(whiteboard);
        setPaint(paint);
        init();
    }

    public Dashline(Whiteboard whiteboard, Paint paint, String doodleId) {
        this(whiteboard);
        setDoodleId(doodleId);
        setPaint(paint);

        init();
    }

    private void init() {
        mLineSegment = new LineSegment();
        getPaint().setPathEffect(new DashPathEffect(new float[]{WhiteboardConfigs.BORDER_DASH_WIDTH, WhiteboardConfigs.BORDER_DASH_WIDTH}, 0));
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
        } else if(mPoints.size() >= 2){
            mPoints.set(1, point);
        }

        if (mPoints.size() > 1) {
            float x1 = Math.min(mPoints.get(0).x, mPoints.get(1).x);
            float x2 = Math.max(mPoints.get(0).x, mPoints.get(1).x);
            float y1 = Math.min(mPoints.get(0).y, mPoints.get(1).y);
            float y2 = Math.max(mPoints.get(0).y, mPoints.get(1).y);
            mOrientation = mPoints.get(1).x >=  mPoints.get(0).x && mPoints.get(1).y >=  mPoints.get(0).y ? FORWARD : REVERSE;
            mDoodleRect.set(x1, y1, x2, y2);
        }
    }

    @Override
    public Path getScreenPath() {
        return null;
    }

    public LineSegment getLineSegment() {
        Matrix matrix = Utils.transformScreenMatrix(mDrawingMatrix, mDisplayMatrix);
        return Utils.getLineSegment(mPoints.get(0), mPoints.get(1), matrix, mLineSegment);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {
        if (mPoints.size() < 2) {
            return;
        }

        canvas.save();

        mDrawingPath.reset();

        mDrawingPath.moveTo(mPoints.get(0).x, mPoints.get(0).y);
        mDrawingPath.lineTo(mPoints.get(1).x, mPoints.get(1).y);

        mDrawingMatrix.postConcat(mTransformMatrix);
        mDrawingPath.transform(mDrawingMatrix);

        canvas.drawPath(mDrawingPath, getPaint());

        canvas.restore();
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
    public boolean onCheckSelected(float x, float y) {
        if (mPoints.size() > 1) {
            return IntersectionHelper.intersect(x, y , this);
        }

        return false;

    }



    public boolean isForward() {
        return mOrientation == FORWARD;
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
            evtBegin.evt= Live.SyncEvent.DASHEDLINE;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            Matrix drawingMatrix=new Matrix(getDrawingMatrixFromWhiteboard());
            SyncBoardFinished evtFinished=new SyncBoardFinished();
            evtFinished.stg= Live.SyncStage.FINISH;
            evtFinished.evt= Live.SyncEvent.DASHEDLINE;
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

    //dash line length
    static final float DASH_LINE_LENGTH=10;

    private ArrayList<PointF> getRealPoints(float transX, float transY,Matrix drawingMatrix){
        //取点流程：矩阵变换映射出实际的两端点---->围绕一个端点旋转到这个直线水平--->
        // 根据虚线的分割长度计算出每段两端点的坐标（即只计算x坐标）--->再旋转回原坐标
        ArrayList<PointF> dest=new ArrayList<>();
        float[] _p=new float[2];
        float[] p0=new float[2];
        Matrix matrix=new Matrix();
        matrix.postTranslate(-transX,-transY);
        matrix.postConcat(mTransformMatrix);
        matrix.postConcat(drawingMatrix);
        for(PointF p:mPoints){
            p0[0]=p.x;
            p0[1]=p.y;
            matrix.mapPoints(_p,p0);
            dest.add(new PointF(_p[0],_p[1]));
        }

        PointF p1=dest.get(0);
        PointF p2=dest.get(1);
        float angle=(float) (180*Math.atan2((-p2.y+p1.y),(p2.x-p1.x))/Math.PI);
        matrix.reset();
        matrix.setRotate(angle,p1.x,p1.y);

        //围绕第一个点旋转angle，计算第二个点旋转后的坐标
        p0[0]=p2.x;
        p0[1]=p2.y;
        matrix.mapPoints(_p,p0);

        float startX=p1.x;
        float endX=_p[0];
        int index=1;
        while (startX+DASH_LINE_LENGTH<endX){
            if(index%3==2){
                dest.add(index,null);
            }else {
                startX+=DASH_LINE_LENGTH;
                dest.add(index,new PointF(startX,p1.y));
            }
            index++;
        }
        matrix.setRotate(-angle,p1.x,p1.y);
        for(int i=1;i<dest.size()-1;i++){
            p1=dest.get(i);
            if(p1==null)continue;
            p0[0]=p1.x;
            p0[1]=p1.y;
            matrix.mapPoints(_p,p0);
            p1.x=_p[0];
            p1.y=_p[1];
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
        layer.shape.type=Live.ShapeType.DRAW_INTERVAL;
        return layer;
    }
}
