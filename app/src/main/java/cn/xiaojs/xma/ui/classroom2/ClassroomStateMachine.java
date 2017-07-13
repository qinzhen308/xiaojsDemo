package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;
import android.os.Message;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.StateMachine;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.model.ctl.FinishClassResponse;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import cn.xiaojs.xma.model.socket.room.ClosePreviewReceive;
import cn.xiaojs.xma.model.socket.room.ConstraintKickoutReceive;
import cn.xiaojs.xma.model.socket.room.EmptyReceive;
import cn.xiaojs.xma.model.socket.room.LogoutKickoutReceive;
import cn.xiaojs.xma.model.socket.room.MediaAbortedReceive;
import cn.xiaojs.xma.model.socket.room.MediaDeviceRefreshReceive;
import cn.xiaojs.xma.model.socket.room.MediaFeedbackReceive;
import cn.xiaojs.xma.model.socket.room.ModeSwitchReceive;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.PlaybackSavedReceive;
import cn.xiaojs.xma.model.socket.room.ReclaimedReceive;
import cn.xiaojs.xma.model.socket.room.StreamExpirationReceive;
import cn.xiaojs.xma.model.socket.room.StreamQualityChangedReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.main.Constants;

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

    protected Context getContext() {
        return context;
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

    protected CTLConstant.UserIdentity getUserIdentity(String psType) {

        if ("LeadSession".equals(psType)) {
            return CTLConstant.UserIdentity.LEAD;
        } else if ("TeacherSession".equals(psType)) {
            return CTLConstant.UserIdentity.TEACHER2;
        } else if ("AssistantSession".equals(psType)) {
            return CTLConstant.UserIdentity.ASSISTANT;
        } else if ("RemoteAssistantSession".equals(psType)) {
            return CTLConstant.UserIdentity.REMOTE_ASSISTANT;
        } else if ("StudentSession".equals(psType)) {
            return CTLConstant.UserIdentity.STUDENT;
        } else if ("ManagerSession".equals(psType)) {
            return CTLConstant.UserIdentity.MANAGER;
        } else if ("AuditorSession".equals(psType)) {
            return CTLConstant.UserIdentity.AUDITOR;
        } else if ("AuditorSession".equals(psType)) {
            return CTLConstant.UserIdentity.ADMINISTRATOR;
        } else if ("AdviserSession".equals(psType)) {
            return CTLConstant.UserIdentity.ADVISER;
        }

        return CTLConstant.UserIdentity.NONE;
    }


    /**
     * 开始上课
     */
    public void startLesson(ClassResponse response) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.START_LESSON;
        message.obj = response;
        sendMessage(message);
    }

    /**
     * 开始课间休息
     */
    public void pauseLesson() {
        sendMessage(CTLConstant.StandloneChannel.PAUSE_LESSON);
    }

    /**
     * 恢复上课
     * @param response
     */
    public void resumeLesson(ClassResponse response) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.RESUME_LESSON;
        message.obj = response;
        sendMessage(message);
    }

    /**
     * 下课
     */
    public void finishLesson(FinishClassResponse response) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.FINISH_LESSON;
        message.obj = response;
        sendMessage(message);
    }

    /**
     * 开始直播秀
     */
    public void startLiveShow(ClaimReponse claimReponse) {
        if (claimReponse != null && claimReponse.result) {
            Message message = new Message();
            message.what = CTLConstant.BaseChannel.START_LIVE_SHOW;
            message.obj = claimReponse;
            sendMessage(message);
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
//    public void startPlayLiveShow() {
//        sendMessage(CTLConstant.BaseChannel.START_PLAY_LIVE_SHOW);
//    }

    /**
     * 停止播放直播秀
     */
//    public void stopPlayLiveShow() {
//        sendMessage(CTLConstant.BaseChannel.STOP_PLAY_LIVE_SHOW);
//    }

    protected void switchStateWhenReceiveSyncState(String state) {

        if(Live.LiveSessionState.FINISHED.equals(state)) {
            getSession().ctlSession.publishUrl = null;
            getSession().ctlSession.playUrl = null;
            getSession().ctlSession.streamType = Live.StreamType.NONE;
        }


    }

    protected void dealReceiveSyncClassState(SyncClassStateReceive message) {

    }

    /**
     * 获取教室的标题
     */
    protected abstract String getTitle();

    protected abstract CTLConstant.UserIdentity getIdentity();

    protected abstract CTLConstant.UserIdentity getIdentityInLesson();


    /**
     * 是否能进行强制推流
     * @return
     */
    protected abstract boolean canForceIndividual();

    /**
     * 是否可以直播秀状态
     * @return
     */
    protected abstract boolean canIndividualByState();

    /**
     * 是否有教学能力
     * @return
     */
    protected abstract boolean hasTeachingAbility();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 事件响应方法
    //

    protected void closeMedia(String event, CloseMediaReceive message){
        getSession().one2one = false;
        notifyEvent(event,message);
    }

    protected void streamStopped(String event, StreamStopReceive message){
        Logger.d("receive streamStopped*********************************");
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.playUrl = null;

        if (message.streamType == Live.StreamType.INDIVIDUAL) {

            ctlSession.finishOn = 0;
            ctlSession.streamType = Live.StreamType.NONE;
            ctlSession.claimedBy = null;

        } else if (message.streamType == Live.StreamType.LIVE) {

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

            roomSession.individualStreamDuration = message.finishOn;

        } else if (message.streamType == Live.StreamType.LIVE) {

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
            finishLesson(null);
        }

        notifyEvent(event, message);
    }

    protected void modeSwitch(String event, ModeSwitchReceive message){
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.mode = message.to;

        notifyEvent(event, message);
    }

    protected void closePreviewByClassover(String event, ClosePreviewReceive message){
        if (message == null) {
            return;
        }
        //TODO
        notifyEvent(event, message);
    }

    protected void remindFinal(String event, EmptyReceive message){
        if (message == null) {
            return;
        }
        notifyEvent(event, message);
    }

    protected void join(String event, Attendee message){
        if (message == null) {
            return;
        }
        notifyEvent(event, message);
    }

    protected void leave(String event, Attendee message){
        if (message == null) {
            return;
        }
        notifyEvent(event, message);
    }

    protected void constraintKickout(String event, ConstraintKickoutReceive message){
        if (message == null) {
            return;
        }

        //TODO

        notifyEvent(event, message);
    }

    protected void logoutKickout(String event, LogoutKickoutReceive message){
        if (message == null) {
            return;
        }
        //TODO

        notifyEvent(event, message);
    }

    protected void mediaAborted(String event, MediaAbortedReceive message){
        if (message == null) {
            return;
        }
        //TODO
        notifyEvent(event, message);
    }

    protected void mediaDeviceRefresh(String event, MediaDeviceRefreshReceive message){
        if (message == null) {
            return;
        }
        //TODO
        notifyEvent(event, message);
    }

    protected void mediaFeedback(String event, MediaFeedbackReceive message){
        if (message == null) {
            return;
        }

        if (message.status == Live.MediaStatus.READY) {
            getSession().ctlSession.playUrl = message.playUrl;
            getSession().one2one = true;
        }else {
            getSession().ctlSession.playUrl = null;
            getSession().one2one = false;
        }

        notifyEvent(event, message);
    }

    protected void openMeidaRecevied(String event, OpenMediaReceive message){
        if (message == null) {
            return;
        }

        getSession().ctlSession.publishUrl = message.publishUrl;
        getSession().one2one = true;
        notifyEvent(event, message);
    }

    protected void streamQualityChangedRecevied(String event, StreamQualityChangedReceive message){
        if (message == null) {
            return;
        }
        notifyEvent(event, message);
    }

    protected void syncClassStateRecevied(String event, SyncClassStateReceive message){
        if (message == null) {
            return;
        }

        dealReceiveSyncClassState(message);

        notifyEvent(event, message);
    }

    protected void syncStateRecevied(String event, SyncStateReceive message){
        if (message == null) {
            return;
        }

        getSession().ctlSession.state = message.to;
        switchStateWhenReceiveSyncState(message.to);
        notifyEvent(event, message);
    }

    protected void talkRecevied(String event, Talk message){
        if (message == null) {
            return;
        }

        //TODO
        notifyEvent(event, message);
    }





//    protected void playbackSavedRecevied(String event, PlaybackSavedReceive message){
//        if (message == null) {
//            return;
//        }
//        //TODO
//        notifyEvent(event, message);
//    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // private methods
    //

    private void clearEventListeners() {
        if (eventListeners != null) {
            eventListeners.clear();
            eventListeners = null;
        }
    }

    private void monitEvent() {
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA, CloseMediaReceive.class,cmCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED, StreamStopReceive.class,stpCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED, StreamStartReceive.class,staCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAM_RECLAIMED, ReclaimedReceive.class,reCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION, StreamExpirationReceive.class,ssbeCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLASS_MODE_SWITCH, ModeSwitchReceive.class,modeSwitchCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLOSE_PREVIEW_BY_CLASS_OVER, ClosePreviewReceive.class,closePreviewCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.REMIND_FINALIZATION, EmptyReceive.class,remindFinalCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.JOIN, Attendee.class,joinCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.LEAVE, Attendee.class,leaveCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.KICK_OUT_BY_CONSTRAINT, ConstraintKickoutReceive.class,cKickoutCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.KICK_OUT_BY_LOGOUT, LogoutKickoutReceive.class,lKickoutCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED, MediaAbortedReceive.class,mediaAbortedCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.MEDIA_DEVICES_REFRESHED, MediaDeviceRefreshReceive.class,mediaDeviceRefreshCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK, MediaFeedbackReceive.class,mediaFeedbackCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA, OpenMediaReceive.class,openMediaCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.REFRESH_STREAMING_QUALITY, StreamQualityChangedReceive.class,streamQualityChangedCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SYNC_CLASS_STATE, SyncClassStateReceive.class,syncClassStateCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SYNC_STATE, SyncStateReceive.class,syncStateCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.TALK, Talk.class,talkCallback);
//        EventManager.onEvent(context,
//                Su.EventCategory.LIVE, Su.EventType.PLAYBACK_SAVED, PlaybackSavedReceive.class,playbackSavedCallback);
//        EventManager.onEvent(context,
//                Su.EventCategory.LIVE, Su.EventType.PRIMARY_SET,,);
        //X2C_RECEIVE_PROVISION_RESULT
        //X2C_RECEIVE_SYNC_DRAW
        //X2C_RECEIVE_SYNC_NEXT
        //X2C_RECEIVE_TIMELINE_CHANGED_ON_CURRENT



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

    private MessageCallback<ModeSwitchReceive> modeSwitchCallback = new MessageCallback<ModeSwitchReceive>() {
        @Override
        public void onMessage(String event, ModeSwitchReceive message) {
            modeSwitch(event, message);
        }
    };

    private MessageCallback<ClosePreviewReceive> closePreviewCallback = new MessageCallback<ClosePreviewReceive>() {
        @Override
        public void onMessage(String event, ClosePreviewReceive message) {
            closePreviewByClassover(event, message);
        }
    };

    private MessageCallback<EmptyReceive> remindFinalCallback = new MessageCallback<EmptyReceive>() {
        @Override
        public void onMessage(String event, EmptyReceive message) {
            remindFinal(event, message);
        }
    };

    private MessageCallback<Attendee> joinCallback = new MessageCallback<Attendee>() {
        @Override
        public void onMessage(String event, Attendee message) {
            join(event, message);
        }
    };

    private MessageCallback<Attendee> leaveCallback = new MessageCallback<Attendee>() {
        @Override
        public void onMessage(String event, Attendee message) {
            leave(event, message);
        }
    };

    private MessageCallback<ConstraintKickoutReceive> cKickoutCallback = new MessageCallback<ConstraintKickoutReceive>() {
        @Override
        public void onMessage(String event, ConstraintKickoutReceive message) {
            constraintKickout(event, message);
        }
    };

    private MessageCallback<LogoutKickoutReceive> lKickoutCallback = new MessageCallback<LogoutKickoutReceive>() {
        @Override
        public void onMessage(String event, LogoutKickoutReceive message) {
            logoutKickout(event, message);
        }
    };

    private MessageCallback<MediaAbortedReceive> mediaAbortedCallback = new MessageCallback<MediaAbortedReceive>() {
        @Override
        public void onMessage(String event, MediaAbortedReceive message) {
            mediaAborted(event, message);
        }
    };

    private MessageCallback<MediaDeviceRefreshReceive> mediaDeviceRefreshCallback = new MessageCallback<MediaDeviceRefreshReceive>() {
        @Override
        public void onMessage(String event, MediaDeviceRefreshReceive message) {
            mediaDeviceRefresh(event, message);
        }
    };

    private MessageCallback<MediaFeedbackReceive> mediaFeedbackCallback = new MessageCallback<MediaFeedbackReceive>() {
        @Override
        public void onMessage(String event, MediaFeedbackReceive message) {
            mediaFeedback(event, message);
        }
    };

    private MessageCallback<OpenMediaReceive> openMediaCallback = new MessageCallback<OpenMediaReceive>() {
        @Override
        public void onMessage(String event, OpenMediaReceive message) {
            openMeidaRecevied(event, message);
        }
    };

    private MessageCallback<StreamQualityChangedReceive> streamQualityChangedCallback = new MessageCallback<StreamQualityChangedReceive>() {
        @Override
        public void onMessage(String event, StreamQualityChangedReceive message) {
            streamQualityChangedRecevied(event, message);
        }
    };

    private MessageCallback<SyncClassStateReceive> syncClassStateCallback = new MessageCallback<SyncClassStateReceive>() {
        @Override
        public void onMessage(String event, SyncClassStateReceive message) {
            syncClassStateRecevied(event, message);
        }
    };

    private MessageCallback<SyncStateReceive> syncStateCallback = new MessageCallback<SyncStateReceive>() {
        @Override
        public void onMessage(String event, SyncStateReceive message) {
            syncStateRecevied(event, message);
        }
    };

    private MessageCallback<Talk> talkCallback = new MessageCallback<Talk>() {
        @Override
        public void onMessage(String event, Talk message) {
            talkRecevied(event, message);
        }
    };

//    private MessageCallback<PlaybackSavedReceive> playbackSavedCallback = new MessageCallback<PlaybackSavedReceive>() {
//        @Override
//        public void onMessage(String event, PlaybackSavedReceive message) {
//            playbackSavedRecevied(event, message);
//        }
//    };

}
