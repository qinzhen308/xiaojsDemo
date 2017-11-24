package cn.xiaojs.xma.ui.classroom2.core;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.LiveRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.live.CtlSession;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/9/19.
 */

public class BootObservable extends Observable<BootObservable.BootSession> {

    private Context context;
    private String ticket;
    private BootListener bootListener;


    public BootObservable(Context context, String ticket) {
        this.context = context;
        this.ticket = ticket;
    }

    public void continueConnect() {
        if (bootListener != null) {
            bootListener.continueConnect();
        }
    }

    public void dispose() {
        if (bootListener !=null && !bootListener.isDisposed()) {
            bootListener.dispose();
        }
    }


    @Override
    protected void subscribeActual(Observer<? super BootSession> observer) {

        bootListener = new BootListener(context, ticket, observer);
        observer.onSubscribe(bootListener);
        bootListener.boot();


    }

    public static class BootListener extends MainThreadDisposable {

        private final int MAX_CONNECT_COUNT = 3;
        private final int MAX_CONNECT_TIME = 5; //s

        private Context context;
        private String initTicket;
        protected Observer<? super BootSession> observer;
        protected Disposable disposableTimeout;
        protected CtlSession ctlSession;
        protected ClassInfo classInfo;
        private SocketManager socketManager;
        private LiveRequest liveRequest;
        private int connectCount;

        public BootListener(Context context, String ticket, Observer<? super BootSession> observer) {
            this.context = context;
            this.initTicket = ticket;
            this.observer = observer;
            connectCount = 0;
        }

        @Override
        protected void onDispose() {

            if (XiaojsConfig.DEBUG) {
                Logger.d("BootListener dispose now!");
            }

            socketManager.disConnect();

            cancelBoot();
            offTimout();
            offSocket();
        }

        public void continueConnect() {
            try {
                connectSocket(ctlSession, true);
            } catch (Exception e) {
                e.printStackTrace();
                sendStatus(Status.SOCKET_CONNECT_FAILED);
            }
        }

        private void boot() {

            sendStatus(Status.BOOT_BEGIN);

            try {
                ctlSession = LiveManager.bootSession2(context, initTicket);
                if (ctlSession != null) {
                    sendStatus(Status.BOOT_SUCCESS);


                    classInfo = LessonDataManager.getClassInfoSync(context, ctlSession.cls.id);
                    sendStatus(Status.GET_CLASSINFO_OVER);


                    if (TextUtils.isEmpty(ctlSession.ticket)) {
                        ctlSession.ticket = initTicket;
                    }

                    if (ctlSession.accessible) {
                        //连接socket
                        try {
                            connectSocket(ctlSession, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendStatus(Status.SOCKET_CONNECT_FAILED);
                        }
                    } else {
                        //询问用户是否强制进入
                        sendStatus(Status.BOOT_QUERY_KICKOUT);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ctlSession == null) {
                    sendStatus(Status.BOOT_FAILED);
                }
            }


//            liveRequest = LiveManager.bootSession(context,
//                    initTicket, new APIServiceCallback<CtlSession>() {
//                @Override
//                public void onSuccess(CtlSession session) {
//
//                    sendStatus(Status.BOOT_SUCCESS);
//
//                    if (TextUtils.isEmpty(session.ticket)) {
//                        session.ticket = initTicket;
//                    }
//
//                    ctlSession = session;
//
//                    if (session.accessible) {
//                        //连接socket
//                        try {
//                            connectSocket(ctlSession, false);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            sendStatus(Status.SOCKET_CONNECT_FAILED);
//                        }
//                    } else {
//                        //询问用户是否强制进入
//                        sendStatus(Status.BOOT_QUERY_KICKOUT);
//                    }
//
//                }
//
//                @Override
//                public void onFailure(String errorCode, String errorMessage) {
//                    sendStatus(Status.BOOT_FAILED);
//                }
//            });
        }

        private Emitter.Listener connectListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //连接成功
                offTimout();
                sendStatus(Status.SOCKET_CONNECT_SUCCESS);
            }
        };

        private Emitter.Listener disConnectListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //断开连接
                offTimout();
                sendStatus(Status.SOCKET_DISCONNECTED);
            }
        };

        private Emitter.Listener errorListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //连接出错
                offTimout();
                connectError(Status.SOCKET_CONNECT_FAILED);
            }
        };

        private Emitter.Listener timeoutListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //连接超时
                offTimout();
                connectError(Status.SOCKET_CONNECT_TIMEOUT);
            }
        };

        private Emitter.Listener kickedoutListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendStatus(Status.KICK_OUT_BY_CONSTRAINT);
            }
        };

        private void sendStatus(Status status) {
            if (observer != null) {
                BootSession bootSession = new BootSession(status);
                if (status == Status.BOOT_SUCCESS
                        || status == Status.SOCKET_CONNECT_SUCCESS
                        || status == Status.GET_CLASSINFO_OVER) {
                    bootSession.ctlSession = ctlSession;
                    bootSession.classInfo = classInfo;
                }

                observer.onNext(bootSession);
            }
        }

        private void cancelBoot() {
            if (liveRequest != null) {
                liveRequest.cancelRequest();
            }
        }

        private void offTimout() {
            if (disposableTimeout != null && !disposableTimeout.isDisposed()) {
                disposableTimeout.dispose();
            }
        }

        private void offSocket() {
            if (socketManager != null) {
                socketManager.off(Socket.EVENT_CONNECT, connectListener);
                socketManager.off(Socket.EVENT_DISCONNECT, disConnectListener);
                socketManager.off(Socket.EVENT_CONNECT_ERROR, errorListener);
                socketManager.off(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);
            }
        }

        private void connectSocket(CtlSession session, boolean force) throws Exception {

            connectCount++;
            observer.onNext(new BootSession(Status.SOCKET_CONNECT_BEGIN));

            if (socketManager == null) {
                socketManager = SocketManager.getSocketManager(context);
            }

            offTimout();
            disposableTimeout = beginTimeoutTimer();

            socketManager.initSocket(ApiManager.getClassroomUrl(context, session.ticket),
                    buildOptions(session.secret, force));
            socketManager.on(Socket.EVENT_CONNECT, connectListener);
            socketManager.on(Socket.EVENT_DISCONNECT, disConnectListener);
            socketManager.on(Socket.EVENT_CONNECT_ERROR, errorListener);
            socketManager.on(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);
            socketManager.on(
                    Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.KICK_OUT_BY_CONSTRAINT),
                    kickedoutListener);

            socketManager.connect();

        }


        private IO.Options buildOptions(String secret, boolean force) {

            String forceStr = force ? "true" : "false";

            IO.Options opts = new IO.Options();
            opts.query = new StringBuilder("secret=")
                    .append(secret)
                    .append("&avc={\"video\":")
                    .append(true)
                    .append(",\"audio\":")
                    .append(true)
                    .append("}&forcibly=")
                    .append(forceStr)
                    .toString();

            if (XiaojsConfig.DEBUG) {
                Logger.d("IO.Options query: " + opts.query);
            }

            opts.timeout = 20 * 1000; //ms
            opts.transports = new String[]{"websocket"};

            return opts;
        }


        private void connectError(Status status) {

            if (XiaojsConfig.DEBUG) {
                Logger.e("connect socket classroom failed");
            }

            offSocket();

            if (connectCount > MAX_CONNECT_COUNT) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("reconnect count is equals max count,so show failed for user");
                }
                //Notify failed
                sendStatus(status);

            } else {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the current reconnect count is %d and < max count,so auto reconnect");
                }

                //直接重试连接
                try {
                    toReconnect(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Notify failed
                    sendStatus(Status.RECONNECT_FAILED);
                }
            }
        }


        private void toReconnect(boolean overall) throws Exception {

            if (overall || ctlSession == null) {
                boot();
            } else {
                connectSocket(ctlSession, !ctlSession.accessible);
            }

        }

        private Disposable beginTimeoutTimer() {
            Disposable disposable = Observable.timer(MAX_CONNECT_TIME, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            connectError(Status.SOCKET_CONNECT_TIMEOUT);
                        }
                    });

            return disposable;
        }

    }

    public static class BootSession {
        public Status status;
        public CtlSession ctlSession;
        public ClassInfo classInfo;
        public Object extraData;

        public BootSession(Status status) {
            this.status = status;
        }

    }

    public enum Status {
        BOOT_BEGIN,                               //开始boot session
        BOOT_SUCCESS,                             //boot session成功
        BOOT_FAILED,                              //boot session失败
        BOOT_QUERY_KICKOUT,                       //boot session成功后，发现已经在其他端进入了教室
        GET_CLASSINFO_OVER,                       //获取班级信息完成
        SOCKET_CONNECT_BEGIN,                     //开始连接socket
        SOCKET_CONNECT_SUCCESS,                   //socket连接成功
        SOCKET_CONNECT_FAILED,                    //socket连接失败
        SOCKET_CONNECT_TIMEOUT,                   //socket连接超时
        SOCKET_DISCONNECTED,                      //与socket断开连接
        RECONNECT_FAILED,                         //重新连接失败
        KICK_OUT_BY_CONSTRAINT                    //账户在其他端进入了教室，此时需要自动被挤出教室
    }


}
