package cn.xiaojs.xma.ui.classroom.whiteboard.core;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/10/18
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncCollector;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncLayerBuilder;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardEvtBegin;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.model.SyncBoardFinishedDelete;

public abstract class GeometryShape extends Doodle implements SyncCollector,SyncLayerBuilder{
    public final static int BEELINE = 0;
    public final static int RECTANGLE = 1;
    public final static int OVAL = 2;
    public final static int TRIANGLE = 3;
    public final static int ARROW = 4;
    public final static int DOUBLE_ARROW = 5;
    public final static int ARC_LINE = 6;
    public final static int TRAPEZOID = 7;
    public final static int PENTAGON = 8;
    public final static int HEXAGON = 9;
    public final static int SINE_CURVE = 10;
    public final static int COORDINATE = 11;
    public final static int RECTANGULAR_COORDINATE = 12;
    public final static int DASH_LINE = 13;
    public final static int RHOMBUS = 14;

    public final static int SQUARE = 100;
    public final static int XYZ_COORDINATE = 101;

    protected int mGeometryId;

    private PointF mHorizontalVector = new PointF();
    private PointF mVerticalVector = new PointF();
    private PointF mCurrVector = new PointF();

    protected GeometryShape(Whiteboard whiteboard, int style, int geometryId) {
        super(whiteboard, style);
        mGeometryId = geometryId;
    }

    protected abstract void changeShape(float touchX, float touchY);

    protected abstract double computeArea();

    protected abstract double computeEdgeLength();

    public static boolean isTwoDimension(int geometryId) {
        switch (geometryId) {
            case ARROW:
            case DOUBLE_ARROW:
            case BEELINE:
            case RECTANGLE:
            case OVAL:
                return true;
        }

        return false;
    }

    public int getGeometryId() {
        return mGeometryId;
    }

    /*@Override
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
    }*/


    @Override
    public Object onCollect(int type) {
        return null;
    }

    @Override
    public Object onCollect(int action,int type) {
        if(type== SyncGenerator.STATE_BEGIN){

            SyncBoardEvtBegin evtBegin=new SyncBoardEvtBegin();
            Ctx ctx=new Ctx();
            ctx.lineWidth=(int)getPaint().getStrokeWidth();
            ctx.strokeStyle= ColorUtil.getColorName(getPaint().getColor());
            ctx.viewport=getWhiteboard().getViewport();
            evtBegin.ctx=ctx;
            evtBegin.stg= Live.SyncStage.BEGIN;
            evtBegin.time=System.currentTimeMillis();
            evtBegin.board= getWhiteboard().getWhiteBoardId();
            evtBegin.id=evtBegin.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
            if(action== Action.DELETE_ACTION){
                evtBegin.evt= Live.SyncEvent.ERASER;
            }
            return evtBegin;
        }else if(type== SyncGenerator.STATE_DOING){

        }else if(type== SyncGenerator.STATE_FINISHED){
            if(action== Action.DELETE_ACTION) {
                SyncBoardFinishedDelete evtFinished=new SyncBoardFinishedDelete();
                evtFinished.stg= Live.SyncStage.FINISH;
                evtFinished.evt= Live.SyncEvent.ERASER;
                evtFinished.time=System.currentTimeMillis();
                evtFinished.id=evtFinished.board= getWhiteboard().getWhiteBoardId();
                evtFinished.from= AccountDataManager.getAccountID(getWhiteboard().getContext());
                evtFinished.data=new ArrayList<SyncLayer>();
                SyncLayer layer=new SyncLayer();
                layer.id=getDoodleId();
                evtFinished.data.add(layer);
                return evtFinished;
            }else if(action== Action.SCALE_ACTION||action== Action.MOVE_ACTION||action== Action.ROTATE_ACTION
                    ||action== Action.SCALE_ROTATE_ACTION||action== Action.CHANGE_AREA_ACTION){

                return onBuildLayer();
            }
        }
        return null;
    }

}
