package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.model.live.Attendee;
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
import cn.xiaojs.xma.model.socket.room.ReclaimedReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardAckReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StopShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StreamExpirationReceive;
import cn.xiaojs.xma.model.socket.room.StreamQualityChangedReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * Created by maxiaobao on 2017/9/12.
 */

public class EventObservable extends Observable {

    private Context context;
    private ClassroomStateMachine stateMachine;

    public EventObservable(Context context, ClassroomStateMachine stateMachine) {
        this.context = context;
        this.stateMachine = stateMachine;

        initEvent();
    }


    @Override
    protected void subscribeActual(Observer observer) {

    }



    private void initEvent() {

        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA, CloseMediaReceive.class, cmCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED, StreamStopReceive.class, stpCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED, StreamStartReceive.class, staCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STREAM_RECLAIMED, ReclaimedReceive.class, reCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION, StreamExpirationReceive.class, ssbeCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLASS_MODE_SWITCH, ModeSwitchReceive.class, modeSwitchCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.CLOSE_PREVIEW_BY_CLASS_OVER, ClosePreviewReceive.class, closePreviewCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.REMIND_FINALIZATION, EmptyReceive.class, remindFinalCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.JOIN, Attendee.class, joinCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.LEAVE, Attendee.class, leaveCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.KICK_OUT_BY_CONSTRAINT, ConstraintKickoutReceive.class, cKickoutCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.KICK_OUT_BY_LOGOUT, LogoutKickoutReceive.class, lKickoutCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED, MediaAbortedReceive.class, mediaAbortedCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.MEDIA_DEVICES_REFRESHED, MediaDeviceRefreshReceive.class, mediaDeviceRefreshCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK, MediaFeedbackReceive.class, mediaFeedbackCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA, OpenMediaReceive.class, openMediaCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.REFRESH_STREAMING_QUALITY, StreamQualityChangedReceive.class, streamQualityChangedCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SYNC_CLASS_STATE, SyncClassStateReceive.class, syncClassStateCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SYNC_STATE, SyncStateReceive.class, syncStateCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.TALK, Talk.class, talkCallback);

        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SHARE_BOARD, ShareboardReceive.class, sbrCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SHARE_BOARD_ACK, ShareboardAckReceive.class, sbarCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.STOP_SHARE_BOARD, StopShareboardReceive.class, ssbrCallback);
        EventManager.onEvent(context,
                Su.EventCategory.LIVE, Su.EventType.SYNC_BOARD, SyncBoardReceive.class, syncbrCallback);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 定义接受事件回调
    //

    private MessageCallback<SyncBoardReceive> syncbrCallback = new MessageCallback<SyncBoardReceive>() {
        @Override
        public void onMessage(String event, SyncBoardReceive message) {
            syncBoardReceived(event, message);


        }
    };

    private MessageCallback<StopShareboardReceive> ssbrCallback = new MessageCallback<StopShareboardReceive>() {
        @Override
        public void onMessage(String event, StopShareboardReceive message) {
            stopShareboardReceived(event, message);
        }
    };


    private MessageCallback<ShareboardAckReceive> sbarCallback = new MessageCallback<ShareboardAckReceive>() {
        @Override
        public void onMessage(String event, ShareboardAckReceive message) {
            shareboardAckReceived(event, message);
        }
    };

    private MessageCallback<ShareboardReceive> sbrCallback = new MessageCallback<ShareboardReceive>() {
        @Override
        public void onMessage(String event, ShareboardReceive message) {
            shareboardReceived(event, message);
        }
    };


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
}
