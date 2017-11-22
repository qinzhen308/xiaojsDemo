package cn.xiaojs.xma.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.xms.XMSObservable;
import cn.xiaojs.xma.data.api.socket.xms.XMSSocketManager;
import cn.xiaojs.xma.data.api.socket.xms.XMSSocketRequest;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.RecallTalk;
import cn.xiaojs.xma.model.socket.RemoveTalk;
import cn.xiaojs.xma.model.socket.room.ChangeNotify;
import cn.xiaojs.xma.model.socket.room.ReadTalk;
import cn.xiaojs.xma.model.socket.room.RemoveDlg;
import cn.xiaojs.xma.model.socket.room.RetainDlg;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import io.reactivex.functions.Consumer;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class XMSManager {

    private static XMSManager xmsManager;

    private Context context;
    private XMSObservable xmsObservable;
    private Set<String> sessionIds;


    public static XMSManager getXmsManager(Context appContext) {

        if (xmsManager == null) {

            synchronized (XMSManager.class) {

                if (xmsManager == null) {
                    xmsManager = new XMSManager(appContext);
                }
            }
        }

        return xmsManager;
    }

    private XMSManager(Context appContext) {
        this.context = appContext.getApplicationContext();

    }
    public void connectXMS(Consumer<Integer> consumer) {
        String sfm = SecurityPref.getSFM(context);
        xmsManager.xmsObservable = XMSObservable.obseverXMS(context, sfm, consumer);
    }


    public boolean sessionOpened(String session) {
        if (sessionIds == null || sessionIds.size() ==0)
            return false;

        if (sessionIds.contains(session)) {
            return true;
        }
        return false;

    }

    public void closeSession(String session) {
        if (sessionIds !=null) {
            sessionIds.remove(session);
        }
    }

    public void addOpenedSession(String sessionId) {
        if (sessionIds == null) {
            sessionIds = new HashSet<>();
        }

        sessionIds.add(sessionId);

    }




    public static void sendTalk(final Context context, final boolean retain, final Talk talk,
                                final EventCallback<TalkResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<TalkResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                new EventCallback<TalkResponse>() {
                    @Override
                    public void onSuccess(TalkResponse talkResponse) {

                        if (retain) {
                            RetainDlg retainDlg = new RetainDlg();
                            retainDlg.to = talk.to;
                            retainDlg.type = talk.type;
                            sendRetainDialog(context, retainDlg, null);
                        }


                        callback.onSuccess(talkResponse);
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        callback.onFailed(errorCode, errorMessage);
                    }
                });

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.TALK);
        socketRequest.emit(event, talk, TalkResponse.class);
    }


    public static void sendRetainDialog(Context context, RetainDlg retainDlg,
                                        EventCallback<EventResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<EventResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.RETAIN_DIALOG);
        socketRequest.emit(event, retainDlg, EventResponse.class);
    }

    public static void sendReadTalk(Context context, ReadTalk readTalk,
                                    EventCallback<EventResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<EventResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.READ_TALK);
        socketRequest.emit(event, readTalk, EventResponse.class);
    }

    public static void sendRemoveDialog(Context context, RemoveDlg removeDlg,
                                        EventCallback<EventResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<EventResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.REMOVE_DIALOG);
        socketRequest.emit(event, removeDlg, EventResponse.class);
    }

    public static void sendChangeNotify(Context context, ChangeNotify changeNotify,
                                        EventCallback<EventResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<EventResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.CHANGE_NOTIFY);
        socketRequest.emit(event, changeNotify, EventResponse.class);
    }

    public static void sendRemoveTalk(Context context, RemoveTalk removeTalk,
                                        EventCallback<EventResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<EventResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.REMOVE_TALK);
        socketRequest.emit(event, removeTalk, EventResponse.class);
    }

    public static void sendRecallTalk(Context context, RecallTalk recallTalk,
                                      EventCallback<EventResponse> callback) {
        XMSSocketManager socketManager = XMSSocketManager.getSocketManager(context);
        XMSSocketRequest<EventResponse> socketRequest = new XMSSocketRequest<>(socketManager,
                callback);

        String event = Su.getEventSignature(Su.EventCategory.XMS_APP,
                Su.EventType.RECALL_TALK);
        socketRequest.emit(event, recallTalk, EventResponse.class);
    }
}