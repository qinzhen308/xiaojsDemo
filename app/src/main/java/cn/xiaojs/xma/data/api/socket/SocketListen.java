package cn.xiaojs.xma.data.api.socket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public class SocketListen<T> {

    private SocketManager socketManager;
    private MessageCallback<T> callback;
    private String event;
    private Emitter.Listener eListener;

    private int eventCategory;
    private int eventType;


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (callback !=null) {
                callback.onMessage(eventCategory, eventType, (T)msg.obj);
            }
        }
    };


    public SocketListen(SocketManager socketManager, MessageCallback<T> callback) {
        this.socketManager = socketManager;
        this.callback = callback;
    }

    public void on(int eventCategory, int eventType,final Class<T> valueType) {

        this.eventCategory = eventCategory;
        this.eventType = eventType;
        this.event = Su.getEventSignature(eventCategory,eventType);

        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so listen event failed...");
            }
            return;
        }

        eListener = new EventLis(valueType);
        socketManager.on(this.event, eListener);
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

    public void offCurrentEventAndCallback() {
        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so off failed...");
            }
            return;
        }

        socketManager.off(event, eListener);
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

    class EventLis implements Emitter.Listener {

        private Class<T> valueType;

        public EventLis(final Class<T> valueType) {
            this.valueType = valueType;
        }

        @Override
        public void call(Object... args) {
            T newMsg =null;
            if (args != null && args.length > 0) {
                newMsg = parseSocketResponse(args[0],valueType);
            }

            if (XiaojsConfig.DEBUG) {
                String data = newMsg==null? "null" : ServiceRequest.objectToJsonString(newMsg);
                Logger.d("received event:%s, with data:%s",event,data);
                Logger.d("------qz--------received event:%s, with data:%s",event,args[0]);
            }

            if (handler != null) {
                Message message = new Message();
                message.obj = newMsg;
                handler.sendMessage(message);
            }
        }
    };

}
