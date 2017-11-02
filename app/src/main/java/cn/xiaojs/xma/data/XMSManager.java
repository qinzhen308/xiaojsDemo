package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.XiaojsApplication;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.data.api.socket.xms.XMSEventListen;
import cn.xiaojs.xma.data.api.socket.xms.XMSObservable;
import cn.xiaojs.xma.data.api.socket.xms.XMSSocketManager;
import cn.xiaojs.xma.data.api.socket.xms.XMSSocketRequest;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import io.reactivex.functions.Consumer;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class XMSManager {

    private XMSObservable xmsObservable;

    public static XMSManager initXMS(Context context, Consumer<Integer> consumer) {
        XMSManager xmsManager = new XMSManager();
        String sfm = SecurityPref.getSFM(context);
        xmsManager.xmsObservable = XMSObservable.obseverXMS(context, sfm, consumer);
        return xmsManager;
    }
    
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