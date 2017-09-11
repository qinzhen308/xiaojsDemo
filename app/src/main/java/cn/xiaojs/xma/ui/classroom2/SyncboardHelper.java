package cn.xiaojs.xma.ui.classroom2;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by maxiaobao on 2017/9/1.
 */

public class SyncboardHelper {
    private static RectF viewportRect=new RectF(0,0,1,1);
    private static RectF unitRect=new RectF(0,0,1,1);
    private static Matrix matrixTransToUnit=new Matrix();
    private static Ctx ctx=new Ctx();

    public synchronized static void init(SyncBoardReceive syncBoard) {
        if(syncBoard==null)return;
        if(syncBoard.ctx==null)return;
        int height=syncBoard.ctx.viewport.height;
        int width=syncBoard.ctx.viewport.width;
        ctx=syncBoard.ctx;
        viewportRect.set(0,0,width,height);
        matrixTransToUnit.setRectToRect(viewportRect,unitRect, Matrix.ScaleToFit.FILL);
    }

    public synchronized static void filterBoardData(SyncBoardReceive syncBoard) {
        if(syncBoard==null)return;
        if(syncBoard.ctx==null){
            syncBoard.ctx=ctx;
        }
        if(ArrayUtil.isEmpty(syncBoard.data)){
            return;
        }
        if(syncBoard.evt== Live.SyncEvent.CLEAR||syncBoard.evt== Live.SyncEvent.ERASER){
            return;
        }
        if(syncBoard.data.get(0).layer!=null){
            matrixLayerToUnit(syncBoard.data.get(0).layer,matrixTransToUnit);
        }
        if(!ArrayUtil.isEmpty(syncBoard.data.get(0).changedLayers)){
            for(SyncLayer layer:syncBoard.data.get(0).changedLayers){
                matrixLayerToUnit(layer,matrixTransToUnit);
            }
        }
        //转换command里面的图层
        if(syncBoard.data.get(0).command!=null&&!ArrayUtil.isEmpty(syncBoard.data.get(0).command.p)){
            for(SyncLayer layer:syncBoard.data.get(0).command.p){
                matrixLayerToUnit(layer,matrixTransToUnit);
            }
        }

        if(syncBoard.evt==Live.SyncEvent.PASTE){
            for(SyncData layer:syncBoard.data){
                matrixLayerToUnitForPaste(layer);
            }
        }

    }

    public synchronized static void matrixLayerToUnit(SyncLayer layer,Matrix matrixTransToUnit){
        if(layer.shape==null)return;
        List<PointF> pointFs=layer.shape.data;
        //图层rect位移到该图层中心位置，为了表示中心坐标系标准
        RectF layerRect=new RectF(layer.shape.left+layer.shape.width/2,
                layer.shape.top+layer.shape.height/2,
                layer.shape.left+layer.shape.width*3/2,
                layer.shape.top+layer.shape.height*3/2);
        Matrix matrix=new Matrix();
        matrix.postTranslate(layerRect.left,layerRect.top);
        matrix.postConcat(matrixTransToUnit);
        float[] _p=new float[2];
        float[] p0=new float[2];
//        Logger.d("-----matrix point---start---layer:"+layer.id);
        for(PointF p:pointFs){
            if(p==null)continue;
            p0[0]=p.x;
            p0[1]=p.y;
            matrix.mapPoints(_p,p0);
            p.x=_p[0];
            p.y=_p[1];
//            Logger.d(Arrays.toString(_p));
        }
//        Logger.d("layer:"+layer.id+"-----matrix point---end---");
    }

    public synchronized static void matrixLayerToUnitForPaste(SyncData layer){
        if(layer.paste_shape==null)return;
        List<PointF> pointFs=layer.paste_shape.data;
        //图层rect位移到该图层中心位置，为了表示中心坐标系标准
        RectF layerRect=new RectF(layer.paste_shape.left+layer.paste_shape.width/2,
                layer.paste_shape.top+layer.paste_shape.height/2,
                layer.paste_shape.left+layer.paste_shape.width*3/2,
                layer.paste_shape.top+layer.paste_shape.height*3/2);
        Matrix matrix=new Matrix();
        matrix.postTranslate(layerRect.left,layerRect.top);
        matrix.postConcat(matrixTransToUnit);
        float[] _p=new float[2];
        float[] p0=new float[2];
//        Logger.d("-----matrix point---start---layer:"+layer.id);
        for(PointF p:pointFs){
            if(p==null)continue;
            p0[0]=p.x;
            p0[1]=p.y;
            matrix.mapPoints(_p,p0);
            p.x=_p[0];
            p.y=_p[1];
//            Logger.d(Arrays.toString(_p));
        }
//        Logger.d("layer:"+layer.id+"-----matrix point---end---");
    }


    public synchronized static void handleShareBoardData(ShareboardReceive syncBoard) {
        if(syncBoard==null||syncBoard.board==null||syncBoard.board.drawing==null||syncBoard.board.drawing.stylus==null)return;

        int height=syncBoard.board.drawing.height;
        int width=syncBoard.board.drawing.width;
        RectF viewportRect=new RectF(0,0,width,height);
        Matrix matrix=new Matrix();
        matrix.setRectToRect(viewportRect,unitRect, Matrix.ScaleToFit.FILL);

        if(!ArrayUtil.isEmpty(syncBoard.board.drawing.stylus.layers)){
            for(SyncLayer layer:syncBoard.board.drawing.stylus.layers){
                matrixLayerToUnit(layer,matrix);
            }
        }

        if(!ArrayUtil.isEmpty(syncBoard.board.drawing.stylus.groupLayers)){
            for(SyncLayer layer:syncBoard.board.drawing.stylus.groupLayers){
                matrixLayerToUnit(layer,matrix);
            }
        }


    }
}
