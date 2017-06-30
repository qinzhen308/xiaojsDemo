package cn.xiaojs.xma.data.api.socket;

import android.content.Context;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.ApiManager;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by maxiaobao on 2017/6/7.
 */

public class SocketManager {

    private volatile static SocketManager socketManager;

    private Context context;

    private Socket socket;
    private String ticket;
    private IO.Options options;
    private String url;

    public static SocketManager getSocketManager(Context context) {
        if (socketManager == null) {

            synchronized(SocketManager.class) {
                if (socketManager == null) {
                    socketManager = new SocketManager(context);
                }
            }
        }
        return socketManager;
    }

    private SocketManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void connect(String url, String ticket, IO.Options options) throws Exception{

        socket = IO.socket(url,options);
        socket.connect();
    }

    public void disConnect() {
        if (socket != null) {
            socket.off();
            socket.close();
        }
    }


    public void emit(final String event, Object data, Ack ack) {

        if (socket == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so emit failed...");
            }

            ack.call();
            return;
        }

        if (data == null) {
            socket.emit(event,ack);
        }else {
            socket.emit(event,data,ack);
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getClassroomUrl(Context context, String ticket) {
        return new StringBuilder(ApiManager.getXLSUrl(context))
                .append("/")
                .append(ticket)
                .toString();
    }




}
