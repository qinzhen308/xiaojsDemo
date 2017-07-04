package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.live.CtlSession;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class ClassroomSession {

    private volatile static ClassroomSession classroomSession;

    private Context context;
    private SocketManager socketManager;
    private EventManager.EventReceiver eventReceiver;
    private SessionListener sessionListener;
    private LiveRoom liveRoom;

    //此类可以不用单利模式，用单例主要是方便在不同Fragment中调用。
    public static ClassroomSession getClassroomSession(Context appContext, SessionListener listener) {

        if (classroomSession == null) {
            synchronized (ApiManager.class) {
                if (classroomSession == null) {
                    classroomSession = new ClassroomSession(appContext, listener);
                }
            }
        }
        return classroomSession;
    }

    public void initSession(String ticket) {
        initSessionAndSocket(ticket);
    }

    //销毁相关资源
    public void destorySession() {
        context = null;
        sessionListener = null;
        classroomSession = null;
    }

    /**
     * 获取教室类型
     */
    public ClassroomType getClassroomType() {
        return liveRoom == null ? ClassroomType.Unknown : liveRoom.classroomType;
    }

    /**
     * 教室状态回调
     */
    public interface SessionListener {
        /**
         * 连接教室成功
         */
        void connectSuccess();

        /**
         * 连接教室失败，或者与教室断开连接
         */
        void connectFailed(String errorCode, String errorMessage);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ClassroomSession(Context context, SessionListener listener) {
        //此处是Activity的context，此session的生命周期要与该activity保持一致
        //退出教室时，需要销毁
        this.context = context;
        this.socketManager = SocketManager.getSocketManager(context);
        this.sessionListener = listener;
    }

    private void initSessionAndSocket(final String ticket) {

        //TODO 判断网络是否可用

        LiveManager.bootSession(context, ticket, new APIServiceCallback<CtlSession>() {
            @Override
            public void onSuccess(CtlSession session) {

                liveRoom = new LiveRoom(session);

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
            socketManager.connect(url, buildOptions(secret));
            socketManager.on(Socket.EVENT_CONNECT, connectListener);
            socketManager.on(Socket.EVENT_DISCONNECT, disConnectListener);
            socketManager.on(Socket.EVENT_CONNECT_ERROR, errorListener);
            socketManager.on(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);
        } catch (Exception e) {
            e.printStackTrace();

            if (sessionListener != null) {
                sessionListener.connectFailed("-1", "");
            }

        }
    }

    private void initWhenConnected() {
        eventReceiver = new EventManager.EventReceiver(socketManager);
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
            //连接成功
            initWhenConnected();

            if (sessionListener != null) {
                sessionListener.connectSuccess();
            }
        }
    };

    private Emitter.Listener disConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //断开连接
            if (sessionListener != null) {
                sessionListener.connectFailed("-1", "");
            }
        }
    };

    private Emitter.Listener errorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //连接出错
            if (sessionListener != null) {
                sessionListener.connectFailed("-1", "");
            }
        }
    };

    private Emitter.Listener timeoutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //连接超时
            if (sessionListener != null) {
                sessionListener.connectFailed("-1", "");
            }
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static enum ClassroomType {
        Unknown,         //未知
        ClassLesson,     //班级
        StandaloneLesson //公开课
    }

    private class LiveRoom {
        //当前教室类型，是班级还是公开课等
        private ClassroomType classroomType;
        //当前教室的状态值
        private CtlSession ctlSession;

        public LiveRoom(CtlSession session) {
            ctlSession = session;

            if (ctlSession.cls != null) {
                classroomType = ClassroomType.ClassLesson;
            } else {
                classroomType = ClassroomType.StandaloneLesson;
            }
        }
    }


}
