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
import cn.xiaojs.xma.model.socket.room.StreamMode;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class EventManager {

    public static <T> void onEvent(Context context,
                                   int eventCategory,
                                   int eventType,
                                   MessageCallback<T> callback) {

        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketListen socketListen = new SocketListen(socketManager,callback);
        socketListen.on(Su.getEventSignature(eventCategory,eventType));
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

}

   // public static class EventReceiver{

//        private SocketListen socketListen;
//
//        public EventReceiver(SocketManager socketManager) {
//            this.socketListen = new SocketListen(socketManager);
//            onBaseEvent();
//        }
//
//        private void onBaseEvent() {
//            socketListen.on(
//                    Su.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA),
//                    closeMediaObserver);
//        }
//
//        public <T> void addOnEvent(int eventCategory, int eventType, Observer<T> observer) {
//            socketListen.on(Su.getEventSignature(eventCategory,eventType), observer);
//        }
//
//        public void removeOnEvent(int eventCategory, int eventType) {
//            socketListen.off(Su.getEventSignature(eventCategory,eventType));
//        }
//
//        public void removeAllOnEvent() {
//            socketListen.off();
//        }
//
//        private Observer<CloseMediaReceive> closeMediaObserver = new Observer<CloseMediaReceive>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(@NonNull CloseMediaReceive closeMediaReceive) {
//
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//
//
//    }
//}
