package cn.xiaojs.xma.ui.classroom.whiteboard.sync;



import cn.xiaojs.xma.ui.classroom.whiteboard.core.Action;

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

    private int action= Action.NO_ACTION;

    public void onActionDown(int action){
        this.action=action;
        state=STATE_READY;
        tempCollector=null;
    }

    public void onActionMove(SyncCollector collector){
        if(state==STATE_READY){
            if(syncDrawingListener!=null){
                Object data=null;
                switch (action){
                    case Action.ADD_ACTION:
                        data=collector.onCollect(STATE_BEGIN);
                        break;
                    default:
                        data=collector.onCollect(action,STATE_BEGIN);
                        break;
                }
                if(data!=null){
                    syncDrawingListener.onBegin(data.toString());
                }
            }
            state=STATE_BEGIN;
        }else {
            if(syncDrawingListener!=null){
                Object data=null;
                switch (action){
                    case Action.ADD_ACTION:
                        data=collector.onCollect(STATE_DOING);
                        break;
                    default:
                        data=collector.onCollect(action,STATE_DOING);
                        break;
                }
                if(data!=null){
                    syncDrawingListener.onGoing(data.toString());
                }
            }
            state=STATE_DOING;
        }
        tempCollector=collector;
    }

    public void onActionUp(){
        if(syncDrawingListener!=null&&tempCollector!=null){
            Object data=null;
            switch (action){
                case Action.ADD_ACTION:
                    data=tempCollector.onCollect(STATE_FINISHED);
                    break;
                default:
                    data=tempCollector.onCollect(action,STATE_FINISHED);
                    break;
            }
            if(data!=null){
                syncDrawingListener.onFinished(data.toString());
            }
        }
        state=STATE_FINISHED;
        tempCollector=null;
    }

    public void updateAction(int action){
        this.action=action;
    }


    public int getState(){
        return state;
    }

    public void setListener(SyncDrawingListener listener){
        syncDrawingListener=listener;
    }
}
