package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.StateMachine;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import cn.xiaojs.xma.model.socket.room.ReclaimedReceive;
import cn.xiaojs.xma.model.socket.room.StreamExpirationReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;

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
     * 开始播放直播秀
     */
    public void startPlayLiveShow() {
        sendMessage(CTLConstant.BaseChannel.START_PLAY_LIVE_SHOW);
    }

    /**
     * 停止播放直播秀
     */
    public void stopPlayLiveShow() {
        sendMessage(CTLConstant.BaseChannel.STOP_PLAY_LIVE_SHOW);
    }


    /**
     * 获取教室的标题
     */
    protected abstract String getTitle();



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 事件响应方法
    //

    protected void closeMedia(String event, CloseMediaReceive message){

    }

    protected void streamStopped(String event, StreamStopReceive message){
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.playUrl = null;


        if (message.streamType == Live.StreamType.INDIVIDUAL) {

            ctlSession.finishOn = 0;
            ctlSession.streamType = Live.StreamType.NONE;
            ctlSession.claimedBy = null;

            stopPlayLiveShow();
        } else if (message.streamType == Live.StreamType.LIVE) {
            //TODO 处理上课的推流
        }

        notifyEvent(event, message);
    }

    protected void streamStartted(String event, StreamStartReceive message){
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;

        ctlSession.playUrl = message.RTMPPlayUrl;
        ctlSession.finishOn = message.finishOn;
        ctlSession.streamType = message.streamType;
        ctlSession.mode = message.mode;
        ctlSession.claimedBy = message.claimedBy;

        if (message.streamType == Live.StreamType.INDIVIDUAL) {
            startPlayLiveShow();
        } else if (message.streamType == Live.StreamType.LIVE) {
            //TODO 处理上课的推流
        }

        notifyEvent(event, message);
    }

    protected void streamReclaimed(String event, ReclaimedReceive message){
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.publishUrl = null;
        ctlSession.finishOn = 0;
        ctlSession.streamType = Live.StreamType.NONE;
        ctlSession.claimedBy = null;

        stopLiveShow();

        notifyEvent(event, message);
    }

    protected void streamExpiration(String event, StreamExpirationReceive message){
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.publishUrl = null;
        ctlSession.finishOn = 0;
        ctlSession.streamType = Live.StreamType.NONE;

        if (message.streamType == Live.StreamType.INDIVIDUAL) {
            stopLiveShow();
        }else if (message.streamType == Live.StreamType.LIVE) {
            //TODO 处理上课的推流
        }

        notifyEvent(event, message);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // private methods
    //
    private void monitEvent() {
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA, cmCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED, stpCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED, staCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAM_RECLAIMED, reCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION, ssbeCallback);
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
            closeMedia(event, message);
        }
    };

    private MessageCallback<StreamStopReceive> stpCallback = new MessageCallback<StreamStopReceive>() {
        @Override
        public void onMessage(String event, StreamStopReceive message) {
            streamStopped(event, message);
        }
    };

    private MessageCallback<StreamStartReceive> staCallback = new MessageCallback<StreamStartReceive>() {
        @Override
        public void onMessage(String event, StreamStartReceive message) {
            streamStartted(event, message);
        }
    };

    private MessageCallback<ReclaimedReceive> reCallback = new MessageCallback<ReclaimedReceive>() {
        @Override
        public void onMessage(String event, ReclaimedReceive message) {
            streamReclaimed(event, message);
        }
    };

    private MessageCallback<StreamExpirationReceive> ssbeCallback = new MessageCallback<StreamExpirationReceive>() {
        @Override
        public void onMessage(String event, StreamExpirationReceive message) {
            streamExpiration(event, message);
        }
    };

}
