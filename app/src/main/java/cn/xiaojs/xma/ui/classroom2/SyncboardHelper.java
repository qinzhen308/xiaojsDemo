package cn.xiaojs.xma.ui.classroom2;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import com.facebook.stetho.common.LogUtil;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.List;

import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
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
        if(syncBoard.data==null){
            return;
        }
        if(syncBoard.data.layer!=null){
            matrixLayerToUnit(syncBoard.data.layer);
        }
        if(!ArrayUtil.isEmpty(syncBoard.data.changedLayers)){
            for(SyncLayer layer:syncBoard.data.changedLayers){
                matrixLayerToUnit(layer);
            }
        }

    }

    public synchronized static void matrixLayerToUnit(SyncLayer layer){
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
}
