package cn.xiaojs.xma.ui.classroom.whiteboard.sync;


import java.util.Date;

/**
 * Created by Paul Z on 2017/9/13.
 */

public class SyncGenerator {
    SyncDrawingListener syncDrawingListener;
    public static final int STATE_READY=0;
    public static final int STATE_BEGIN=1;
    public static final int STATE_DOING=2;
    public static final int STATE_FINISHED=3;
    private int state=STATE_READY;


    private SyncCollector tempCollector;

    public void onActionDown(){

        state=STATE_READY;
        tempCollector=null;
    }

    public void onActionMove(SyncCollector collector){
        if(state==STATE_READY){
            if(syncDrawingListener!=null){
                Object data=collector.onCollect(STATE_BEGIN);
                if(data!=null){
                    syncDrawingListener.onBegin(data);
                }
            }
            state=STATE_BEGIN;
        }else {
            if(syncDrawingListener!=null){
                Object data=collector.onCollect(STATE_DOING);
                if(data!=null){
                    syncDrawingListener.onGoing(data);
                }
            }
            state=STATE_DOING;
        }
        tempCollector=collector;
    }

    public void onActionUp(){
        if(syncDrawingListener!=null&&tempCollector!=null){
            Object data=tempCollector.onCollect(STATE_FINISHED);
            if(data!=null){
                syncDrawingListener.onFinished(data);
            }
        }
        state=STATE_FINISHED;
        tempCollector=null;
    }



    public int getState(){
        return state;
    }

    public void setListener(SyncDrawingListener listener){
        syncDrawingListener=listener;
    }
}
