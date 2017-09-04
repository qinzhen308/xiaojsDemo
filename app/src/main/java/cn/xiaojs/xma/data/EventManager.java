package cn.xiaojs.xma.data;

import android.content.Context;
import android.text.TextUtils;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.data.api.socket.SocketListen;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.data.api.socket.SocketRequest;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.CSCurrent;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMedia;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.Feedback;
import cn.xiaojs.xma.model.socket.room.RequestShareboard;
import cn.xiaojs.xma.model.socket.room.ShareboardAck;
import cn.xiaojs.xma.model.socket.room.StopShareboard;
import cn.xiaojs.xma.model.socket.room.StreamMode;
import cn.xiaojs.xma.model.socket.room.StreamQuality;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.bean.OpenMedia;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class EventManager {

    public static <T> void onEvent(Context context,
                                   int eventCategory,
                                   int eventType,
                                   final Class<T> valueType,
                                   MessageCallback<T> callback) {

        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketListen socketListen = new SocketListen(socketManager,callback);
        socketListen.on(Su.getEventSignature(eventCategory,eventType),valueType);
    }


    /**
     * Claims to be on live before or after the scheduled class duration.
     * @param context
     * @param streamMode
     * @param callback
     */
    public static void claimStreaming(Context context,
                                      int streamMode,
                                      EventCallback<ClaimReponse> callback) {

        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<ClaimReponse> socketRequest = new SocketRequest<>(socketManager,callback);

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.CLAIM_STREAMING);
        StreamMode smode = new StreamMode();
        smode.mode = streamMode;

        socketRequest.emit(event, smode, ClaimReponse.class);

    }

    /**
     * Signals the live streaming has started and is available to play.
     * @param context
     * @param callback
     */
    public static void streamingStarted(Context context, EventCallback<EventResponse> callback) {

        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,callback);

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.STREAMING_STARTED);
        socketRequest.emit(event, null, EventResponse.class);

    }

    /**
     * Signals the live streaming has stopped.
     * @param context
     * @param csOfCurrent
     * @param callback
     */
    public static void streamingStopped(Context context,String csOfCurrent,
                                        EventCallback<StreamStoppedResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<StreamStoppedResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        CSCurrent current = null;
        if (!TextUtils.isEmpty(csOfCurrent)) {
            current = new CSCurrent();
            current.csOfCurrent = csOfCurrent;
        }

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.STREAMING_STOPPED);
        socketRequest.emit(event, current, StreamStoppedResponse.class);
    }

    /**
     * Signals the video talk streaming has started or failed to start.
     * @param context
     * @param mediaStatus
     * @param callback
     */
    public static void mediaFeedback(Context context,int mediaStatus,
                                        EventCallback<EventResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        Feedback feedback = new Feedback();
        feedback.status = mediaStatus;

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.MEDIA_FEEDBACK);
        socketRequest.emit(event, feedback, EventResponse.class);
    }


    /**
     * Signals that an established peer-to-peer video talk must be closed.
     * @param context
     * @param to
     * @param callback
     */
    public static void closeMedia(Context context,String to,
                                     EventCallback<CloseMediaResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<CloseMediaResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        CloseMedia closeMedia = null;
        if (!TextUtils.isEmpty(to)) {
            closeMedia = new CloseMedia();
            closeMedia.to = to;
        }

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.CLOSE_MEDIA);
        socketRequest.emit(event, closeMedia, CloseMediaResponse.class);
    }


    /**
     * Requests to establish peer-to-peer video talk with an attendant.
     * @param context
     * @param to
     * @param callback
     */
    public static void openMedia(Context context,String to,
                                  EventCallback<EventResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        OpenMedia openMedia = null;
        if (!TextUtils.isEmpty(to)) {
            openMedia = new OpenMedia();
            openMedia.to = to;
        }

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.OPEN_MEDIA);
        socketRequest.emit(event, openMedia, EventResponse.class);
    }

    /**
     * Refreshes the live streaming or the on-live-side networking quality.
     * @param context
     * @param quality
     * @param callback
     */
    public static void streamQuality(Context context,int quality,
                                 EventCallback<EventResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        StreamQuality streamQuality = new StreamQuality();
        streamQuality.quality = quality;

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.REFRESH_STREAMING_QUALITY);
        socketRequest.emit(event, streamQuality, EventResponse.class);
    }

    /**
     * Talks to specific attendees in the class.
     * @param context
     * @param talk
     * @param callback
     */
    public static void sendTalk(Context context,Talk talk,
                                     EventCallback<TalkResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<TalkResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.TALK);
        socketRequest.emit(event, talk, TalkResponse.class);
    }


    public static void shareboardFeedback(Context context,boolean accept,
                                          String board, EventCallback<EventResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        ShareboardAck ack = new ShareboardAck();
        ack.accepted = accept;
        ack.board = board;

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.SHARE_BOARD_ACK);
        socketRequest.emit(event, ack, EventResponse.class);
    }


    public static void requestShareboard(Context context, RequestShareboard shareboard,
                                         EventCallback<EventResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.SHARE_BOARD);
        socketRequest.emit(event, shareboard, EventResponse.class);
    }


    public static void stopShareboard(Context context, String board,
                                      String[] targetIds, EventCallback<EventResponse> callback) {
        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<EventResponse> socketRequest = new SocketRequest<>(socketManager,
                callback);

        StopShareboard stopShareboard = new StopShareboard();
        stopShareboard.board = board;
        stopShareboard.to = targetIds;

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.STOP_SHARE_BOARD);
        socketRequest.emit(event, stopShareboard, EventResponse.class);
    }

}