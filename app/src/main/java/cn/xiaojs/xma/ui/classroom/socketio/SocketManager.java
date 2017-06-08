package cn.xiaojs.xma.ui.classroom.socketio;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/1/8
 * Desc: reference https://github.com/socketio/socket.io-client-java
 *
 * ======================================================================================== */

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private final static String SCHEME = "http://";
    private final static String SCHEME_SSL = "https://";

    private final static int MSG_ON_CALLBACK = 100;
    private final static int MSG_EMIT_CALLBACK = 101;
    private final static int MSG_TIME_OUT = 102;

    private final static int TIME_OUT = 10000; //ms

    private static Socket mSocket;
    private static Handler mHandler;
    private static ConcurrentMap<String, ConcurrentLinkedQueue<EventListener>> mEventListeners;

    public synchronized static void init(Context context, String ticket, String secret, boolean videoSupported, boolean audioSupported, boolean force) {
        initHandler();
        if (mSocket == null) {
            try {
                String forceStr = force ? "true" : "false";
                IO.Options opts = new IO.Options();
                opts.query = "secret=" + secret + "&" + "avc={\"video\":" + videoSupported + ",\"audio\":" + audioSupported + "}"
                        + "&" + "forcibly=" + forceStr;
                opts.timeout = TIME_OUT; //ms
                opts.transports = new String[]{"websocket"};
                mSocket = IO.socket(getClassroomSocketUrl(context, ticket), opts);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            if (mEventListeners == null) {
                mEventListeners = new ConcurrentHashMap<String, ConcurrentLinkedQueue<EventListener>>();
            }
        } else {
            throw new RuntimeException("Only one socket may be created!");
        }
    }

    public synchronized static Socket getSocket() {
        return mSocket;
    }

    public static String getClassroomSocketUrl(Context context, String ticket) {
        String url = ApiManager.getXLSUrl(context) + "/" + ticket;
        return url;
    }

    public static void connect() {
        if (mSocket != null) {
            mSocket.connect();
        }
    }

    /**
     * off all socket listeners and clear EventListeners
     */
    public static void off() {
        off(true);
    }

    /**
     * off all listeners
     */
    private static void off(boolean clear) {
        if (mEventListeners != null) {
            for (String event : mEventListeners.keySet()) {
                ConcurrentLinkedQueue<EventListener> eventListeners = mEventListeners.get(event);
                if (eventListeners != null) {
                    //off emit listener
                    if (mSocket != null) {
                        for (EventListener listener : eventListeners) {
                            mSocket.off(event, listener.originalEmitListener);
                        }
                    }

                    if (clear) {
                        eventListeners.clear();
                    }
                }
            }
            if (clear) {
                mEventListeners.clear();
            }
        }

        if (mSocket != null) {
            mSocket.off();
        }
    }


    /**
     * off some listeners special event
     */
    public static void off(String event) {
        if (mEventListeners != null) {
            ConcurrentLinkedQueue<EventListener> eventListeners = mEventListeners.get(event);
            if (eventListeners != null) {
                //off emit listener
                if (mSocket != null) {
                    for (EventListener listener : eventListeners) {
                        mSocket.off(event, listener.originalEmitListener);
                    }
                }

                eventListeners.clear();
            }
        }

        if (mSocket != null) {
            mSocket.off(event);
        }
    }

    /**
     * off special listener
     */
    public static void off(String event, EventListener listener) {
        if (mEventListeners == null) {
            return;
        }

        //off emit listener
        if (mSocket != null) {
            mSocket.off(event, listener.originalEmitListener);
        }

        ConcurrentLinkedQueue<EventListener> eventListeners = mEventListeners.get(event);
        if (eventListeners != null) {
            Iterator<EventListener> it = eventListeners.iterator();
            while (it.hasNext()) {
                EventListener internal = it.next();
                if (internal != null && internal.originalEmitListener == listener.originalEmitListener) {
                    it.remove();
                    break;
                }
            }
        }
    }

    public static void close() {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

    public static void reListener() {
        if (mEventListeners == null) {
            return;
        }

        for (String event : mEventListeners.keySet()) {
            ConcurrentLinkedQueue<EventListener> eventListeners = mEventListeners.get(event);
            if (eventListeners != null) {
                Iterator<EventListener> it = eventListeners.iterator();
                while (it.hasNext()) {
                    EventListener listener = it.next();
                    on(event, listener, false);
                }
            }
        }
    }

    public static void emit(final String event, final Object... args) {
        if (mSocket != null) {
            if (args != null && args.length > 0) {
                if (args[args.length - 1] instanceof IAckListener) {
                    final IAckListener listener = (IAckListener) args[args.length - 1];
                    JSONObject[] data = null;
                    if (args.length > 1) {
                        data = new JSONObject[args.length - 1];
                        for (int i = 0; i < args.length - 1; i++) {
                            data[i] = ClassroomBusiness.wrapSocketBean(args[i]);
                        }
                        mSocket.emit(event, data, new Ack() {
                            @Override
                            public void call(final Object... response) {
                                Message msg = new Message();
                                msg.what = MSG_EMIT_CALLBACK;
                                SocketCallback callback = new SocketCallback();
                                callback.ackListener = listener;
                                callback.data = response;
                                msg.obj = callback;
                                if (mHandler != null) {
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });
                    } else {
                        mSocket.emit(event, new Ack() {
                            @Override
                            public void call(final Object... response) {
                                Message msg = new Message();
                                msg.what = MSG_EMIT_CALLBACK;
                                SocketCallback callback = new SocketCallback();
                                callback.ackListener = listener;
                                callback.data = response;
                                msg.obj = callback;
                                if (mHandler != null) {
                                    mHandler.sendMessage(msg);
                                }
                            }
                        });
                    }
                } else {
                    mSocket.emit(event, args);
                }
            }

        }
    }

    public static void on(String event, final EventListener listener) {
        on(event, listener, true);
    }

    public static void on(String event, final EventListener listener, boolean needAdd) {
        if (mSocket != null) {
            Emitter.Listener emitterListener = new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Message msg = new Message();
                    msg.what = MSG_ON_CALLBACK;
                    SocketCallback callback = new SocketCallback();
                    callback.eventListener = listener;
                    callback.data = args;
                    msg.obj = callback;
                    if (mHandler != null) {
                        mHandler.sendMessage(msg);
                    }
                }
            };
            listener.originalEmitListener = emitterListener;

            mSocket.on(event, emitterListener);
            if (needAdd) {
                addListener2Map(event, listener);
            }
        } else {
            if (needAdd) {
                addListener2Map(event, listener);
            }
        }
    }

    private static void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    SocketCallback callback = null;
                    switch (msg.what) {
                        case MSG_ON_CALLBACK:
                            callback = (SocketCallback) msg.obj;
                            if (callback != null && callback.eventListener != null) {
                                callback.eventListener.call(callback.data);
                            }
                            break;
                        case MSG_EMIT_CALLBACK:
                            callback = (SocketCallback) msg.obj;
                            if (callback != null && callback.ackListener != null) {
                                callback.ackListener.call(callback.data);
                            }
                            break;
                    }
                }
            }
        };
    }

    private static void addListener2Map(String event, EventListener listener) {
        if (mEventListeners == null) {
            return;
        }

        ConcurrentLinkedQueue<EventListener> eventListeners = mEventListeners.get(event);
        if (eventListeners == null) {
            eventListeners = new ConcurrentLinkedQueue<EventListener>();
            ConcurrentLinkedQueue<EventListener> tempEventListeners = mEventListeners.putIfAbsent(event, eventListeners);
            if (tempEventListeners != null) {
                eventListeners = tempEventListeners;
            }
        }
        eventListeners.add(listener);
    }

    private static class SocketCallback {
        public EventListener eventListener;
        public IAckListener ackListener;
        public Object[] data;
    }

    public static abstract class EventListener implements IEventListener {
        public Emitter.Listener originalEmitListener;
    }

    public interface IEventListener {
        public void call(Object... args);
    }

    public interface IAckListener {
        void call(Object... args);
    }

    public interface OnSocketListener {
        void onSocketConnectChanged(boolean connected);
    }

}
