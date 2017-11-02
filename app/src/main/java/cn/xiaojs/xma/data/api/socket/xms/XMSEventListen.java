package cn.xiaojs.xma.data.api.socket.xms;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.MessageCallback;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public class XMSEventListen<T> implements Emitter.Listener {


    private int eventCategory;
    private int eventType;
    private String event;
    private Class<T> valueType;
    private MessageCallback<T> callback;

    private XMSSocketManager socketManager;

    public XMSEventListen(XMSSocketManager socketManager,
                          final Class<T> valueType, MessageCallback<T> callback) {
        this.valueType = valueType;
        this.socketManager = socketManager;
        this.callback = callback;
    }

    @Override
    public void call(Object... args) {

        T newMsg = null;
        if (args != null && args.length > 0) {
            newMsg = parseSocketResponse(args[0], valueType);
        }

        if (callback != null) {
            callback.onMessage(eventCategory, eventType, newMsg);
        }
    }


    public void on(int eventCategory, int eventType) {
        this.eventCategory = eventCategory;
        this.eventType = eventType;
        this.event = Su.getEventSignature(eventCategory, eventType);
        socketManager.on(this.event, this);
    }

    public void off() {
        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null,so off failed...");
            }
            return;
        }

        socketManager.off(event, this);
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
