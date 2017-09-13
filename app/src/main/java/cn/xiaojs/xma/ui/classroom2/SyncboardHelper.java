package cn.xiaojs.xma.ui.classroom2;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
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
    private static HashMap<String,SyncboardHelperInstance> map=new HashMap();


    public static class SyncboardHelperInstance{
        private String id;
        private RectF viewportRect=new RectF(0,0,1,1);
        private RectF unitRect=new RectF(0,0,1,1);
        private Matrix matrixTransToUnit=new Matrix();
        private Ctx ctx=new Ctx();

        public SyncboardHelperInstance(String id){
            this.id=id;
        }

        public void begin(SyncBoardReceive syncBoard) {
            if(syncBoard==null)return;
            if(syncBoard.ctx==null)return;
            int height=syncBoard.ctx.viewport.height;
            int width=syncBoard.ctx.viewport.width;
            ctx=syncBoard.ctx;
            viewportRect.set(0,0,width,height);
            matrixTransToUnit.setRectToRect(viewportRect,unitRect, Matrix.ScaleToFit.FILL);
        }

        public void ongoing(SyncBoardReceive syncBoard){
            if(syncBoard==null||ArrayUtil.isEmpty(syncBoard.data))return;
            float[] _p=new float[2];
            float[] p0=new float[2];
            p0[0]=syncBoard.data.get(0).startPos.x;
            p0[1]=syncBoard.data.get(0).startPos.y;
            matrixTransToUnit.mapPoints(_p,p0);
            syncBoard.data.get(0).startPos.x=_p[0];
            syncBoard.data.get(0).startPos.y=_p[1];

            p0[0]=syncBoard.data.get(0).endPos.x;
            p0[1]=syncBoard.data.get(0).endPos.y;
            matrixTransToUnit.mapPoints(_p,p0);
            syncBoard.data.get(0).endPos.x=_p[0];
            syncBoard.data.get(0).endPos.y=_p[1];
        }

        public void finished(SyncBoardReceive syncBoard) {
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

        public void matrixLayerToUnit(SyncLayer layer,Matrix matrixTransToUnit){
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

        public void matrixLayerToUnitForPaste(SyncData layer){
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


        public void handleShareBoardData(ShareboardReceive syncBoard) {
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


    private synchronized static SyncboardHelperInstance addInstance(String tag){
        SyncboardHelperInstance instance=new SyncboardHelperInstance(tag);
        map.put(tag,instance );
        return instance;
    }

    private synchronized static boolean contain(String tag){
        return map.containsKey(tag);
    }

    private static SyncboardHelperInstance get(String tag){
        return map.get(tag);
    }

    private static SyncboardHelperInstance remove(String tag){
        return map.remove(tag);
    }

    private synchronized static void begin(SyncBoardReceive syncBoard) {
        if(syncBoard==null)return;
        if(syncBoard.stg!=Live.SyncStage.BEGIN){
            return;
        }
        if(contain(syncBoard.getId())){
            get(syncBoard.getId()).begin(syncBoard);
        }else {
            addInstance(syncBoard.getId()).begin(syncBoard);
        }
    }

    private synchronized static void going(SyncBoardReceive syncBoard) {
        if(syncBoard==null)return;
        if(syncBoard.stg!=Live.SyncStage.ONGOING){
            return;
        }
        if(contain(syncBoard.getId())){
            get(syncBoard.getId()).ongoing(syncBoard);
        }
    }

    private synchronized static void finished(SyncBoardReceive syncBoard) {
        if(syncBoard==null)return;
        if(contain(syncBoard.getId())){
            get(syncBoard.getId()).finished(syncBoard);
            remove(syncBoard.getId());
        }
    }


    public synchronized static void handleShareBoardData(ShareboardReceive syncBoard) {
        new SyncboardHelperInstance(syncBoard.from).handleShareBoardData(syncBoard);
    }

    public static boolean handleSyncEvent(SyncBoardReceive syncBoard){
        if(syncBoard==null)return false;
        if (syncBoard.stg != Live.SyncStage.FINISH){
            if(syncBoard.stg==Live.SyncStage.BEGIN){
                SyncboardHelper.begin(syncBoard);
                if(syncBoard.evt==Live.SyncEvent.PEN){
                    return true;
                }
            }
            if(syncBoard.stg==Live.SyncStage.ONGOING&&syncBoard.evt==Live.SyncEvent.PEN){
                SyncboardHelper.going(syncBoard);
                return true;
            }
            if (XiaojsConfig.DEBUG) {
                Logger.d("the stg is %d and not equal FINISH, so do not notify event", syncBoard.stg);
            }
            return false;
        }

        finished(syncBoard);

        return true;
    }

}
