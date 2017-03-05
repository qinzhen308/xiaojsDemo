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
 * Desc:
 *
 * ======================================================================================== */

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.net.URISyntaxException;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.LiveService;
import cn.xiaojs.xma.ui.classroom.ClassroomBusiness;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private final static String SCHEME = "http://";
    private final static String SCHEME_SSL = "https://";

    private final static int MSG_ON_CALLBACK = 100;
    private final static int MSG_EMIT_CALLBACK = 101;

    private static Socket mSocket;
    private static Handler mHandler;

    public synchronized static void init(String ticket, String secret, boolean videoSupported, boolean audioSupported) {
        initHandler();
        if (mSocket == null) {
            try {
                IO.Options opts = new IO.Options();
                opts.query = "secret=" + secret + "&" + "avc={\"video\":" + videoSupported + ",\"audio\":" + audioSupported + "}";
                opts.timeout = 10000; //ms
                opts.transports = new String[]{"websocket"};
                mSocket = IO.socket(getClassroomSocketUrl(ticket), opts);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Only one socket may be created!");
        }
    }

    public synchronized static Socket getSocket() {
        return mSocket;
    }

    public static String getClassroomSocketUrl(String ticket) {
        String url = XiaojsConfig.BASE_URL + ":" + XiaojsConfig.LIVE_PORT + "/" + ticket;
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

    public static void close() {
        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public static void emit(final String event, final Object... args) {
        if (mSocket != null) {
            if (args != null && args.length > 0) {
                if (args[args.length - 1] instanceof AckListener) {
                    final AckListener listener = (AckListener)args[args.length - 1];
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
                                mHandler.sendMessage(msg);
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
                                mHandler.sendMessage(msg);
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
                    mHandler.sendMessage(msg);
                }
            });
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
                            callback = (SocketCallback)msg.obj;
                            if (callback != null && callback.eventListener != null) {
                                callback.eventListener.call(callback.data);
                            }
                            break;
                        case MSG_EMIT_CALLBACK:
                            callback = (SocketCallback)msg.obj;
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

}
