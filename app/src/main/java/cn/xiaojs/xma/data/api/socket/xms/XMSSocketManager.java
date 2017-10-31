package cn.xiaojs.xma.data.api.socket.xms;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/6/7.
 */

public class XMSSocketManager {
    private Context context;

    private volatile static XMSSocketManager socketManager;

    private Socket socket;

    private XMSSocketManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static XMSSocketManager getSocketManager(Context context) {
        if (socketManager == null) {

            synchronized (XMSSocketManager.class) {
                if (socketManager == null) {
                    socketManager = new XMSSocketManager(context);
                }
            }
        }
        return socketManager;
    }



    public void initSocket(String url, IO.Options options) throws Exception {
        socket = IO.socket(url, options);
    }

    public void connect() throws Exception {
        socket.connect();
    }


    public void disConnect() {
        if (socket != null) {
            socket.off();
            socket.disconnect();
            socket = null;
        }
    }

    public boolean connected() {
        return socket == null ? false : socket.connected();
    }


    public void emit(final String event, Object data, Ack ack) {

        if (socket == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null or not connect,so emit failed...");
            }

            ack.call();
            return;
        }

        if (data == null) {
            socket.emit(event, ack);
        } else {
            JSONObject[] jsonObjects = new JSONObject[1];
            jsonObjects[0] = wrapSocketBean(data);
            socket.emit(event, jsonObjects, ack);
        }

    }

    public void on(final String event, Emitter.Listener listener) {
        if (socket == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null or not connect,so on listener failed...");
            }
            return;
        }
        socket.on(event, listener);
    }

    public void off(String event) {
        if (socket != null) {
            socket.off(event);
        }

    }

    public void off() {
        if (socket != null) {
            socket.off();
        }

    }

    public void off(String event, Emitter.Listener listener) {
        if (socket != null) {
            socket.off(event, listener);
        }
    }


    private JSONObject wrapSocketBean(Object obj) {
        JSONObject data = null;
        try {
            String sendJson;
            if (obj instanceof String) {
                sendJson = (String) obj;
            } else {
                ObjectMapper mapper = new ObjectMapper();
                sendJson = mapper.writeValueAsString(obj);
            }

            if (sendJson == null) {
                return null;
            }
            data = new JSONObject(sendJson);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return data;
    }

}
