package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.StateMachine;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public abstract class ClassroomStateMachine extends StateMachine {


    private Context context;
    private RoomSession roomSession;
    private List<EventListener> eventListeners;


    public ClassroomStateMachine(Context context, String name, RoomSession session) {
        super(name);
        this.context = context.getApplicationContext();
        this.roomSession = session;
        setDbg(XiaojsConfig.DEBUG);
        monitEvent();
    }


    public void destoryAndQuitNow() {
        clearEventListeners();
        quitNow();
    }

    public RoomSession getSession() {
        return this.roomSession;
    }


    public void addEventListener(EventListener listener) {
        if (this.eventListeners == null) {
            eventListeners = new ArrayList<>();
        }

        eventListeners.add(listener);

    }

    public void removeEventListener(EventListener listener) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    protected void notifyEvent(String event, Object data) {
        if (eventListeners != null && eventListeners.size() > 0) {
            for (EventListener listener : eventListeners) {
                listener.receivedEvent(event, data);
            }
        }
    }

    public abstract String getLiveState();

    public String getPublishUrl() {
        return roomSession.ctlSession.publishUrl;
    }

    public String getPlayUrl() {
        return roomSession.ctlSession.playUrl;
    }

    public int getLiveMode() {
        return roomSession.ctlSession.mode;
    }

    /**
     * 开始上课
     */
    public void startLesson() {
        sendMessage(CTLConstant.BaseChannel.START_LESSON);
    }

    /**
     * 下课
     */
    public void finishLesson() {
        sendMessage(CTLConstant.BaseChannel.FINISH_LESSON);
    }

    /**
     * 开始直播秀
     */
    public void startLiveShow(ClaimReponse claimReponse) {
        if (claimReponse != null && claimReponse.result) {
            roomSession.ctlSession.publishUrl = claimReponse.publishUrl;
            roomSession.ctlSession.finishOn = claimReponse.finishOn;
            sendMessage(CTLConstant.BaseChannel.START_LIVE_SHOW);
        }
    }

    /**
     * 结束直播秀
     */
    public void stopLiveShow() {
        sendMessage(CTLConstant.BaseChannel.STOP_LIVE_SHOW);
    }


    /**
     * 处理相应事件
     */
    protected abstract void closeMedia(CloseMediaReceive message);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // private methods
    //
    private void monitEvent() {
        EventManager.onEvent(context,
                Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA, cmCallback);
    }

    private void clearEventListeners() {
        if (eventListeners != null) {
            eventListeners.clear();
            eventListeners = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 定义接受事件回调
    //
    private MessageCallback<CloseMediaReceive> cmCallback = new MessageCallback<CloseMediaReceive>() {
        @Override
        public void onMessage(String event, CloseMediaReceive message) {
            closeMedia(message);
        }
    };


}
