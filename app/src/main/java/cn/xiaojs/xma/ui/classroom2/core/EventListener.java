package cn.xiaojs.xma.ui.classroom2.core;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.data.api.socket.SocketListen;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import cn.xiaojs.xma.model.socket.room.ClosePreviewReceive;
import cn.xiaojs.xma.model.socket.room.ConstraintKickoutReceive;
import cn.xiaojs.xma.model.socket.room.EmptyReceive;
import cn.xiaojs.xma.model.socket.room.EventReceived;
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
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class EventListener extends MainThreadDisposable implements MessageCallback {

    protected Context context;
    protected Observer<? super EventReceived> observer;
    protected final List<SocketListen> socketListeners;


    public EventListener(Context context) {
        this.context = context;
        socketListeners = new ArrayList<>();
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    protected boolean checkSignatures() {
        return true;
    }

    protected boolean send(EventReceived eventReceived){
        return true;
    }

    @Override
    public void onMessage(int eventCategory, int eventType, Object message) {

        if (!checkSignatures()) {
            return;
        }

        EventReceived eventReceived = new EventReceived(eventCategory, eventType, message);

        if (send(eventReceived) && observer != null) {
            observer.onNext(eventReceived);
        }

    }

    @Override
    protected void onDispose() {
        offEvent();
    }


    public void onEvent() {

        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.CLOSE_MEDIA, CloseMediaReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.STREAMING_STOPPED, StreamStopReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.STREAMING_STARTED, StreamStartReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.STREAM_RECLAIMED, ReclaimedReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.STOP_STREAM_BY_EXPIRATION, StreamExpirationReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.CLASS_MODE_SWITCH, ModeSwitchReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.CLOSE_PREVIEW_BY_CLASS_OVER, ClosePreviewReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.REMIND_FINALIZATION, EmptyReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.JOIN, Attendee.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.LEAVE, Attendee.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.KICK_OUT_BY_CONSTRAINT, ConstraintKickoutReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.KICK_OUT_BY_LOGOUT, LogoutKickoutReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.MEDIA_ABORTED, MediaAbortedReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.MEDIA_DEVICES_REFRESHED, MediaDeviceRefreshReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.MEDIA_FEEDBACK, MediaFeedbackReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.OPEN_MEDIA, OpenMediaReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.REFRESH_STREAMING_QUALITY, StreamQualityChangedReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.SYNC_CLASS_STATE, SyncClassStateReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.SYNC_STATE, SyncStateReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.TALK, Talk.class, this));

        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.SHARE_BOARD_ACK, ShareboardAckReceive.class, this));
        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.STOP_SHARE_BOARD, StopShareboardReceive.class, this));


        socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                Su.EventType.SHARE_BOARD, ShareboardReceive.class, this));

    }

    private void offEvent() {
        if (socketListeners == null) {
            return;
        }

        for (SocketListen listen : socketListeners) {
            listen.offCurrentEventAndCallback();
        }
        socketListeners.clear();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Defined special listener
    //

    public static class Syncboard extends EventListener {

        public Syncboard(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context,
                    Su.EventCategory.LIVE, Su.EventType.SYNC_BOARD, SyncBoardReceive.class, this));
        }


        @Override
        protected boolean send(EventReceived eventReceived) {
            //TODO after
            return true;
        }
    }

    public static class ELPlaylive extends EventListener {

        public ELPlaylive(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.REMIND_FINALIZATION, EmptyReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.CLOSE_MEDIA, CloseMediaReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.MEDIA_ABORTED, MediaAbortedReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.OPEN_MEDIA, OpenMediaReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.REFRESH_STREAMING_QUALITY, StreamQualityChangedReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.SYNC_STATE, SyncStateReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.SYNC_CLASS_STATE, SyncClassStateReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.SHARE_BOARD, ShareboardReceive.class, this));
        }

    }

    public static class ELLiving extends EventListener {

        public ELLiving(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.REMIND_FINALIZATION, EmptyReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.CLOSE_MEDIA, CloseMediaReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.MEDIA_ABORTED, MediaAbortedReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.OPEN_MEDIA, OpenMediaReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.SYNC_STATE, SyncStateReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.SYNC_CLASS_STATE, SyncClassStateReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.SHARE_BOARD, ShareboardReceive.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.SHARE_BOARD_ACK, ShareboardAckReceive.class, this));
        }
    }

    public static class ELPVideoControl extends EventListener {

        public ELPVideoControl(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.STREAMING_STOPPED, StreamStopReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.STREAMING_STARTED, StreamStartReceive.class, this));
            socketListeners.add(EventManager.onEvent(context,Su.EventCategory.LIVE,
                    Su.EventType.STREAM_RECLAIMED, ReclaimedReceive.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.STOP_STREAM_BY_EXPIRATION, StreamExpirationReceive.class, this));
        }

    }

    public static class ELLiveControl extends EventListener {

        public ELLiveControl(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.STREAMING_STOPPED, StreamStopReceive.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.STREAMING_STARTED, StreamStartReceive.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.STREAM_RECLAIMED, ReclaimedReceive.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.STOP_STREAM_BY_EXPIRATION, StreamExpirationReceive.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.MEDIA_FEEDBACK, MediaFeedbackReceive.class, this));
        }
    }

    public static class ELTalk extends EventListener {

        public ELTalk(Context context) {
            super(context);
        }


        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context,
                    Su.EventCategory.LIVE, Su.EventType.TALK, Talk.class, this));


        }
    }

    public static class ELContact extends EventListener {

        public ELContact(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.JOIN, Attendee.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.LEAVE, Attendee.class, this));
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.SYNC_CLASS_STATE, SyncClassStateReceive.class, this));
        }
    }

    public static class ELRoom extends EventListener {

        public ELRoom(Context context) {
            super(context);
        }

        @Override
        public void onEvent() {
            socketListeners.add(EventManager.onEvent(context, Su.EventCategory.LIVE,
                    Su.EventType.KICK_OUT_BY_CONSTRAINT, ConstraintKickoutReceive.class, this));
        }
    }

}
