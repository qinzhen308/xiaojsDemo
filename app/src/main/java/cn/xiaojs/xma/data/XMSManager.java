package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.data.api.socket.SocketListen;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.data.api.socket.SocketRequest;
import cn.xiaojs.xma.data.api.socket.xms.XMSSocketManager;
import cn.xiaojs.xma.data.api.socket.xms.XMSSocketRequest;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class XMSManager {

//    public static <T> SocketListen onEvent(Context context,
//                                           int eventCategory,
//                                           int eventType,
//                                           final Class<T> valueType,
//                                           MessageCallback<T> callback) {
//
//        SocketManager socketManager = SocketManager.getSocketManager(context);
//        SocketListen socketListen = new SocketListen(socketManager, callback);
//        socketListen.on(eventCategory, eventType, valueType);
//
//        return socketListen;
//    }


    public static void sendTalk(Context context, Talk talk,
                                EventCallback<TalkResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<TalkResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.TALK);
        socketRequest.emit(event, talk, TalkResponse.class);
    }

}