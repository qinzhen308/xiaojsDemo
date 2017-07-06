package cn.xiaojs.xma.ui.classroom2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class ClassroomEngine {

    private final int MSG_CONNECT_SUCCESS = 1;
    private final int MSG_CONNECT_ERROR = 2;
    private final int MSG_CONNECT_TIMEOUT = 3;
    private final int MSG_NETWORK_CHANGED = 4;

    private volatile static ClassroomEngine classroomEngine;

    private Context context;
    private SocketManager socketManager;
    private SessionListener sessionListener;
    private RoomSession roomSession;
    private RoomRequest roomRequest;

    private ClassroomStateMachine stateMachine;

    private NetworkChangedReceiver networkChangedReceiver;


    //此类可以不用单利模式，用单例主要是方便在不同Fragment中调用。
    public static ClassroomEngine getEngine(Context appContext, SessionListener listener) {

        if (classroomEngine == null) {
            synchronized (ApiManager.class) {
                if (classroomEngine == null) {
                    classroomEngine = new ClassroomEngine(appContext, listener);
                }
            }
        }
        return classroomEngine;
    }

    public void init(String ticket) {
        reset();
        initSessionAndSocket(ticket);
    }

    public static ClassroomEngine getRoomEngine() {
        return classroomEngine;
    }

    /**
     * 销毁engine
     */
    public void destoryEngine() {
        reset();
        sessionListener = null;
        context = null;
        classroomEngine = null;
    }

    /**
     * 添加事件监听
     */
    public void addEvenListener(EventListener listener) {
        if (stateMachine != null) {
            stateMachine.addEventListener(listener);
        }
    }

    /**
     * 注销事件监听
     */
    public void removeEvenListener(EventListener listener) {
        if (stateMachine != null) {
            stateMachine.removeEventListener(listener);
        }
    }

    /**
     * 获取教室类型
     */
    public ClassroomType getClassroomType() {
        return roomSession == null ? ClassroomType.Unknown : roomSession.classroomType;
    }

    public String getLiveState() {
        return stateMachine.getLiveState();
    }

    public RoomSession getRoomSession() {
        return stateMachine.getSession();
    }

    public String getPublishUrl() {
        return stateMachine.getPublishUrl();
    }

    public String getPlayUrl() {
        return stateMachine.getPlayUrl();
    }

    public int getLiveMode() {
        return stateMachine.getLiveMode();
    }

    public String getCsOfCurrent() {
        return getRoomSession().csOfCurrent;
    }

    public void setCsOfCurrent(String csOfCurrent) {
        getRoomSession().csOfCurrent = csOfCurrent;
    }

    public String getRoomTitle() {
        return stateMachine.getTitle();
    }

    /**
     * 请求开始直播秀
     * @param mode
     * @param callback
     */
    public void claimStream(int mode, final EventCallback<ClaimReponse> callback) {
        if (roomRequest != null) {
            roomRequest.claimStream(mode, callback);
        }
    }

    /**
     * 开始推流
     * @param callback
     */
    public void startStreaming(final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.startStreaming(callback);
        }
    }

    /**
     * 停止推流
     * @param csOfCurrent
     * @param callback
     */
    public void stopStreaming(int streamType, String csOfCurrent,
                              final EventCallback<StreamStoppedResponse> callback) {
        if (roomRequest != null) {
            roomRequest.stopStreaming(streamType, csOfCurrent, callback);
        }
    }

    /**
     * 一对一推流时候，推流成功后发送的socket事件
     * @param mediaStatus
     * @param callback
     */
    public void mediaFeedback(int mediaStatus,
                              final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.mediaFeedback(mediaStatus, callback);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ClassroomEngine(Context context, SessionListener listener) {
        //此处是Activity的context，此session的生命周期要与该activity保持一致
        //退出教室时，需要销毁
        this.context = context;
        this.socketManager = SocketManager.getSocketManager(context);
        this.sessionListener = listener;
    }

    private void initSessionAndSocket(final String ticket) {

        LiveManager.bootSession(context, ticket, new APIServiceCallback<CtlSession>() {
            @Override
            public void onSuccess(CtlSession session) {

                roomSession = new RoomSession(session);

                //连接socket
                connectSocket(ticket, session.secret);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if (sessionListener != null) {
                    sessionListener.connectFailed(errorCode, errorMessage);
                }
            }
        });
    }

    private void connectSocket(String ticket, String secret) {
        String url = getClassroomUrl(ticket);
        try {
            socketManager.initSocket(url, buildOptions(secret));
            socketManager.on(Socket.EVENT_CONNECT, connectListener);
            socketManager.on(Socket.EVENT_DISCONNECT, disConnectListener);
            socketManager.on(Socket.EVENT_CONNECT_ERROR, errorListener);
            socketManager.on(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);
            socketManager.connect();
            handler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, 20 * 1000);
        } catch (Exception e) {
            e.printStackTrace();

            if (sessionListener != null) {
                sessionListener.connectFailed("-1", "");
            }

        }
    }

    private void initWhenConnected() {

        ClassroomType ctype = getClassroomType();
        if (ctype == ClassroomType.ClassLesson) {
            //初始化班级状态机
        } else {
            stateMachine = new StandloneStateMachine(context, roomSession);
        }
        stateMachine.start();

        roomRequest = new RoomRequest(context, stateMachine);

        registeNetwork();
    }

    private void offSocket() {
        if (socketManager != null) {
            socketManager.off();
        }
    }

    private void reset() {
        unRegisteNetwork();
        roomRequest = null;
        if (stateMachine != null) {
            stateMachine.destoryAndQuitNow();
            stateMachine = null;
        }
    }

    private void registeNetwork() {
        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkChangedReceiver, filter);
    }

    private void unRegisteNetwork() {
        if (networkChangedReceiver != null) {
            context.unregisterReceiver(networkChangedReceiver);
            networkChangedReceiver = null;
        }

    }

    private String getClassroomUrl(String ticket) {
        return new StringBuilder(ApiManager.getXLSUrl(context))
                .append("/")
                .append(ticket)
                .toString();
    }

    private IO.Options buildOptions(String secret) {
        IO.Options opts = new IO.Options();
        opts.query = new StringBuilder("secret=")
                .append(secret)
                .append("&avc={\"video\":")
                .append(true)
                .append(",\"audio\":")
                .append(true)
                .append("}&forcibly=")
                .append("true")
                .toString();
        opts.timeout = 20 * 1000; //ms
        opts.transports = new String[]{"websocket"};

        return opts;
    }


    private Emitter.Listener connectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_SUCCESS);
            handler.sendEmptyMessage(MSG_CONNECT_SUCCESS);
        }
    };

    private Emitter.Listener disConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //断开连接
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_ERROR);
            handler.sendEmptyMessage(MSG_CONNECT_ERROR);
        }
    };

    private Emitter.Listener errorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //连接出错
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_ERROR);
            handler.sendEmptyMessage(MSG_CONNECT_ERROR);
        }
    };

    private Emitter.Listener timeoutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //连接超时
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.sendEmptyMessage(MSG_CONNECT_TIMEOUT);
        }
    };


    /**
     * 网络切换监听
     */
    public class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();

            boolean mobileNet = mobileInfo == null ? false : mobileInfo.isConnected();
            boolean wifiNet = wifiInfo == null ? false : wifiInfo.isConnected();
            String activeNet = activeInfo == null ? "null" : activeInfo.getTypeName();

            if (activeInfo == null) {
                //没有网络
                Message message = new Message();
                message.what = MSG_NETWORK_CHANGED;
                message.arg1 = CTLConstant.NetworkType.TYPE_NONE;
                handler.sendMessage(message);

            } else if (wifiNet) {
                // WIFI
                Message message = new Message();
                message.what = MSG_NETWORK_CHANGED;
                message.arg1 = CTLConstant.NetworkType.TYPE_WIFI;
                handler.sendMessage(message);
            } else if (mobileNet) {
                // mobile network
                Message message = new Message();
                message.what = MSG_NETWORK_CHANGED;
                message.arg1 = CTLConstant.NetworkType.TYPE_MOBILE;
                handler.sendMessage(message);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_SUCCESS:     //连接教室成功
                    initWhenConnected();
                    if (sessionListener != null) {
                        sessionListener.connectSuccess();
                    }
                    break;
                case MSG_CONNECT_ERROR:       //连接教室失败
                case MSG_CONNECT_TIMEOUT:     //连接教室超时

                    offSocket();

                    if (sessionListener != null) {
                        sessionListener.connectFailed("-1", "");
                    }
                    break;
                case MSG_NETWORK_CHANGED:     //网络状态改变
                    if (sessionListener != null) {
                        sessionListener.networkStateChanged(msg.arg1);
                    }
                    break;
            }
        }
    };


}
