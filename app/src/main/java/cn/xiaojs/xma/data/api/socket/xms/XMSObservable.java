package cn.xiaojs.xma.data.api.socket.xms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.util.NetUtil;
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
 * Created by maxiaobao on 2017/10/27.
 */

public class XMSObservable extends Observable<Integer> {

    private Context context;
    private String sfm;
    private Disposable disposable;
    private SocketStatusListener socketStatusListener;

    public XMSObservable(Context context, String sfm) {
        this.context = context;
        this.sfm = sfm;
    }

    @Override
    protected void subscribeActual(Observer<? super Integer> observer) {
        socketStatusListener = new SocketStatusListener(context, sfm, observer);
        observer.onSubscribe(socketStatusListener);
        socketStatusListener.startConnect();
    }


    public static XMSObservable obseverXMS(Context context, String sfm, Consumer<Integer> consumer) {
        XMSObservable xmsObservable = new XMSObservable(context, sfm);
        xmsObservable.disposable = xmsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return xmsObservable;

    }


    public static class SocketStatusListener extends MainThreadDisposable {

        private final int MAX_CONNECT_COUNT = 50;
        private final int MAX_CONNECT_TIMEOUT = 20 * 1000; //s

        private Context context;
        protected Observer<? super Integer> observer;
        protected Disposable disposableTimeout;
        private XMSSocketManager socketManager;
        private int connectCount;
        private String address;
        private IO.Options ioOptions;
        private int currentStatus;
        //private NetworkStatusReceiver networkStatusReceiver;

        public SocketStatusListener(Context context, String sfm, Observer observer) {
            this.context = context;
            this.observer = observer;
            this.connectCount = 0;
            this.address = ApiManager.getXMSUrl(context);
            //this.address = "http://192.168.100.3:3007";
            this.ioOptions = buildOptions(sfm);

            socketManager = XMSSocketManager.getSocketManager(context);

            if (XiaojsConfig.DEBUG) {
                Logger.d("Connect XMS url: %s, \n IO options query: %s", address, ioOptions.query);
            }

            //registerNetStatus();

        }

        @Override
        protected void onDispose() {

            if (XiaojsConfig.DEBUG) {
                Logger.d("SocketStatusListener dispose now!");
            }
            offTimout();
            offSocket();
            socketManager.disConnect();
            //unRegisteNetStatus();
        }

//        private void registerNetStatus() {
//            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//            context.registerReceiver(networkStatusReceiver, filter);
//        }
//
//        private void unRegisteNetStatus() {
//            if (networkStatusReceiver != null) {
//                context.unregisterReceiver(networkStatusReceiver);
//                networkStatusReceiver = null;
//            }
//        }

//        private void reconnectWhenNetActived(boolean mobile) {
//            try {
//
//                if (XiaojsConfig.DEBUG) {
//
//                    String active = mobile ? "MOBLIE" : "WIFI";
//
//                    Logger.d("received network activied by: %s", active);
//                }
//
//
//                if (socketManager.connected()) {
//
//                    if (XiaojsConfig.DEBUG) {
//                        Logger.d("the XMS socket has connected, so nothing to do...");
//                    }
//
//                    return;
//                }
//
//
//                if (XiaojsConfig.DEBUG) {
//                    Logger.d("start to reConnect XMS socket now....");
//                }
//
//                connectCount = 0;
//                toReconnect();
//            } catch (Exception e) {
//                e.printStackTrace();
//                sendStatus(SocketStatus.CONNECT_FAILED);
//            }
//        }


        private void startConnect() {

            sendStatus(SocketStatus.CONNECT_BEGIN);

            try {
                connectSocket();
            } catch (Exception e) {
                e.printStackTrace();
                sendStatus(SocketStatus.CONNECT_FAILED);
            }

        }

        private Emitter.Listener connectListener = new Emitter.Listener() {                //连接成功
            @Override
            public void call(Object... args) {

                offTimout();
                connectCount = 0;
                sendStatus(SocketStatus.CONNECT_SUCCESS);
            }
        };

        private Emitter.Listener disConnectListener = new Emitter.Listener() {             //断开连接
            @Override
            public void call(Object... args) {

                offTimout();
                sendStatus(SocketStatus.DISCONNECTED);
                connectError();
            }
        };

        private Emitter.Listener errorListener = new Emitter.Listener() {                  //连接出错
            @Override
            public void call(Object... args) {
                offTimout();
                sendStatus(SocketStatus.CONNECT_FAILED);
                connectError();
            }
        };

        private Emitter.Listener timeoutListener = new Emitter.Listener() {                //连接超时
            @Override
            public void call(Object... args) {

                offTimout();
                sendStatus(SocketStatus.CONNECT_TIMEOUT);
                connectError();
            }
        };


        private void sendStatus(int status) {

            currentStatus = status;

            if (observer != null) {
                observer.onNext(status);
            }
        }

        private void offTimout() {
            if (disposableTimeout != null && !disposableTimeout.isDisposed()) {
                disposableTimeout.dispose();
                disposableTimeout = null;
            }
        }

        private Disposable beginTimeoutTimer() {
            Disposable disposable = Observable.timer(MAX_CONNECT_TIMEOUT / 1000, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {                  //连接超时
                            sendStatus(SocketStatus.CONNECT_TIMEOUT);
                            connectError();
                        }
                    });

            return disposable;
        }


        private void offSocket() {
            if (socketManager != null) {
                socketManager.off(Socket.EVENT_CONNECT, connectListener);
                socketManager.off(Socket.EVENT_DISCONNECT, disConnectListener);
                socketManager.off(Socket.EVENT_CONNECT_ERROR, errorListener);
                socketManager.off(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);

            }
        }

        private void connectSocket() throws Exception {


            if (XiaojsConfig.DEBUG) {
                Logger.d("start connect the XMS server...");
            }


            connectCount++;

            offTimout();
            disposableTimeout = beginTimeoutTimer();

            socketManager.initSocket(address, ioOptions);
            socketManager.on(Socket.EVENT_CONNECT, connectListener);
            socketManager.on(Socket.EVENT_DISCONNECT, disConnectListener);
            socketManager.on(Socket.EVENT_CONNECT_ERROR, errorListener);
            socketManager.on(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);

            socketManager.connect();

        }


        private IO.Options buildOptions(String sfm) {

            IO.Options opts = new IO.Options();
            opts.query = new StringBuilder("sfm=")
                    .append(sfm)
                    .toString();

            //opts.query = "uery:secret=c18928d526c2b820ca564eb405405bcd1ba5c1c2230f77fd4d8260d96599f8eddcad550a1987e153e0e3ad885b91d70c8dfc798fef55b0ef9fbe1714b38c7e7db4caafc198085a8205814a227eeb7b4a48c57c643fc5af5039d11f4de53f557bc6d42a885972a1bffe92771fd5f6d9b59ca89cc938113945e4d04c8084ee5aec4f0c3b062ab00b8db7d042f421b45b575fb646911c59ec83855ee3d18daaca9950404db4d1d88c3bb1039d32ab715ec4d65018b3f542d352e2d0eef44dcd52a2278122036bcabd508ee0ca16e561b5b9a5637ca207e6ef6159a23331d0c6fa6748d308a67a4345ed53d468ffa077170fa3796c61cb9739665647f920ff959907c9cba7555d131bf66f11d410956f554d3741fcef43d766fa8afb3c200d0109864154526437260dd755e8ac11daa54b2c1f06c3972692a0fa3c63d6b36996840745d74da1945c8445410650f6e87bf672&avc={\"video\":true,\"audio\":true}&forcibly=false";

            opts.timeout = MAX_CONNECT_TIMEOUT; //ms
            opts.transports = new String[]{"websocket"};

            return opts;
        }


        private void connectError() {

            if (XiaojsConfig.DEBUG) {
                Logger.e("connect socket classroom failed");
            }

            offSocket();

            if (connectCount > MAX_CONNECT_COUNT) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("reconnect count is equals max count,so show failed for user");
                }
                //Notify failed
                sendStatus(SocketStatus.RECONNECT_ARRIVED_MAX_COUNT);

            } else {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the current reconnect count is %d and < max count,so auto reconnect");
                }

                //判断网络
                if (NetUtil.getCurrentNetwork(context) == NetUtil.NETWORK_NONE) {

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("the current network is disconnected ,so cancel reconnect");
                    }

                    return;
                }


                //直接重试连接
                try {
                    toReconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Notify failed
                    sendStatus(SocketStatus.CONNECT_FAILED);
                }
            }
        }


        private void toReconnect() throws Exception {
            sendStatus(SocketStatus.RECONNECT);
            connectSocket();
        }


        /**
         * 网络切换监听
         */
//        public class NetworkStatusReceiver extends BroadcastReceiver {
//            @Override
//            public void onReceive(final Context context, Intent intent) {
//
//
//                if (XiaojsConfig.DEBUG) {
//                    Logger.d("received network status is changed...");
//                }
//
//
//                ConnectivityManager manager = (ConnectivityManager)
//                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
//
//                boolean mobileNet = mobileInfo == null ? false : mobileInfo.isConnected();
//                boolean wifiNet = wifiInfo == null ? false : wifiInfo.isConnected();
//
//                if (activeInfo == null) {
//                    //没有网络
//                    if (XiaojsConfig.DEBUG) {
//                        Logger.d("received network is disconnected");
//                    }
//                    return;
//
//                }
//
//                if (wifiNet) {
//                    // WIFI
//                    reconnectWhenNetActived(false);
//                    return;
//                }
//
//
//                if (mobileNet) {
//                    reconnectWhenNetActived(true);
//                    return;
//                }
//            }
//        }


    }

}
