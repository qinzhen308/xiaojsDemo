package cn.xiaojs.xma.data.api.socket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public class SocketListen<T> {

    private SocketManager socketManager;
    private MessageCallback<T> callback;
    private String event;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (callback !=null) {
                callback.onMessage(event, (T)msg.obj);
            }
        }
    };


    public SocketListen(SocketManager socketManager, MessageCallback<T> callback) {
        this.socketManager = socketManager;
        this.callback = callback;
    }

    public void on(final String event,final Class<T> valueType) {

        this.event = event;

        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so listen event failed...");
            }
            return;
        }

        socketManager.on(event, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                T newMsg =null;
                if (args != null && args.length > 0) {
                    newMsg = parseSocketResponse(args[0],valueType);
                }

                if (XiaojsConfig.DEBUG) {
                    String data = newMsg==null? "null" : ServiceRequest.objectToJsonString(newMsg);
                    Logger.d("received event:%s, with data:%s",event,data);
                }

                if (handler != null) {
                    Message message = new Message();
                    message.obj = newMsg;
                    handler.sendMessage(message);
                }
            }
        });


    }

    public void off(String event) {
        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so off failed...");
            }
            return;
        }
        socketManager.off(event);
    }

    public void off() {
        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so off failed...");
            }
            return;
        }
        socketManager.off();
    }

    private T parseSocketResponse(Object obj, final Class<T> valueType) {
        if (obj == null) {
            return null;
        }

        try {
            String result;
            if (obj instanceof JSONObject) {
                result = obj.toString();
            } else if (obj instanceof String) {
                result = (String) obj;
            } else {
                result = obj.toString();
            }

            if (TextUtils.isEmpty(result)) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
