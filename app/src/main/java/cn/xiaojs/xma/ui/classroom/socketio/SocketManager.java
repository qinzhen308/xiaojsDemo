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
import java.util.HashMap;
import java.util.Map;

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
    private static Map<String, EventListener> mEventListeners;

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

            mEventListeners = new HashMap<String, EventListener>();
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

    public static void off() {
        if (mSocket != null) {
            mSocket.off();
        }
    }

    public static void off(String event) {
        if (mSocket != null) {
            mSocket.off(event);
        }

        if (mEventListeners != null && mEventListeners.containsKey(event)) {
            mEventListeners.remove(event);
        }
    }

    public static void close() {
        close(true);
    }

    public static void close(boolean clearListener) {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (clearListener) {
            if (mEventListeners != null) {
                mEventListeners.clear();
                mEventListeners = null;
            }
        }
    }

    public static void reListener() {
        if (mEventListeners != null) {
            for (Map.Entry<String, EventListener> entry : mEventListeners.entrySet()) {
                String event = entry.getKey();
                EventListener listener = entry.getValue();
                on(event, listener, false);
            }
        }
    }

    public static void emit(final String event, final Object... args) {
        if (mSocket != null) {
            if (args != null && args.length > 0) {
                if (args[args.length - 1] instanceof AckListener) {
                    final AckListener listener = (AckListener) args[args.length - 1];
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
            mSocket.on(event, new Emitter.Listener() {
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
            });

            if (needAdd) {
                mEventListeners.put(event, listener);
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

    private static class SocketCallback {
        public EventListener eventListener;
        public AckListener ackListener;
        public Object[] data;
    }

    public interface EventListener {
        void call(Object... args);
    }

    public interface AckListener {
        void call(Object... args);
    }

    public interface OnSocketListener {
        void onSocketConnectChanged(boolean connected);
    }

}
