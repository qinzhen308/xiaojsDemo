package cn.xiaojs.xma.data.api.socket.xms;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import cn.xiaojs.xma.model.socket.room.ChangeNotifyReceived;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.Talk;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by maxiaobao on 2017/11/1.
 */

public class XMSEventObserver<T> extends MainThreadDisposable implements MessageCallback{

    private Context context;
    private XMSSocketManager xmsSocketManager;

    protected final List<XMSEventListen> socketListeners;
    protected Observer<? super EventReceived> observer;

    public XMSEventObserver(Context context) {
        this.context = context;
        this.socketListeners = new ArrayList<>();
        this.xmsSocketManager = XMSSocketManager.getSocketManager(context);
    }

    public void setObserver(Observer<? super EventReceived> observer) {
        this.observer = observer;
    }

    public void on(int eventCategory, int eventType, Class<T> valueType) {
        XMSEventListen eventListen = new XMSEventListen(xmsSocketManager,valueType,this);
        eventListen.on(eventCategory, eventType);
        socketListeners.add(eventListen);
    }


    @Override
    protected void onDispose() {
        if (XiaojsConfig.DEBUG) {
            Logger.d("XMSEventObserver dispose now!");
        }
        offEvent();
    }

    @Override
    public void onMessage(int eventCategory, int eventType, Object message) {

        EventReceived eventReceived = new EventReceived(eventCategory, eventType, message);

        if (XiaojsConfig.DEBUG) {
            String data = message==null? "null" : ServiceRequest.objectToJsonString(message);
            Logger.d("XMSEventObserver received event(%d,%d) with data: %s",
                    eventCategory,
                    eventType,
                    data);
        }

        if (observer != null) {
            observer.onNext(eventReceived);
        }

    }

    protected void onEvent() {

    }

    private void offEvent() {
        if (socketListeners == null) {
            return;
        }

        for (XMSEventListen listen : socketListeners) {
            listen.off();
        }
        socketListeners.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Defined special listener
    //

    public static class ChatSession extends XMSEventObserver {
        public ChatSession(Context context) {
            super(context);
        }

        @Override
        protected void onEvent() {
            on(Su.EventCategory.XMS_MESSAGING, Su.EventType.TALK, Talk.class);
        }
    }

    public static class MainSession extends XMSEventObserver {
        public MainSession(Context context) {
            super(context);
        }

        @Override
        protected void onEvent() {
            on(Su.EventCategory.XMS_MESSAGING, Su.EventType.TALK, Talk.class);
            on(Su.EventCategory.XMS_MESSAGING, Su.EventType.DIALOG_READ, Talk.class);
            on(Su.EventCategory.XMS_MESSAGING, Su.EventType.REMOVE_DIALOG, Talk.class);
            on(Su.EventCategory.XMS_MESSAGING, Su.EventType.RETAIN_DIALOG, Talk.class);
            on(Su.EventCategory.XMS_MESSAGING, Su.EventType.CHANGE_NOTIFY, ChangeNotifyReceived.class);
        }
    }


}


