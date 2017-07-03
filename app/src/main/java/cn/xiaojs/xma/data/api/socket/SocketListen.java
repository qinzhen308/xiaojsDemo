package cn.xiaojs.xma.data.api.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public class SocketListen<T> {

    private final int MSG_NEW_MESSAGE = 0x1;


    private SocketManager socketManager;
    private MessageCallback<T> messageCallback;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public SocketListen(SocketManager socketManager, MessageCallback<T> callback) {
        this.socketManager = socketManager;
        this.messageCallback = callback;
    }

    public void on(String event) {
        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so listen event failed...");
            }
            return;
        }

        socketManager.on(event, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args != null && args.length > 0) {

                }
            }
        });


    }
}
