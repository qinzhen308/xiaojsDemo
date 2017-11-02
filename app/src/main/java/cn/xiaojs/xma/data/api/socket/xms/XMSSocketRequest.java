package cn.xiaojs.xma.data.api.socket.xms;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.socket.EventResponse;
import io.socket.client.Ack;

/**
 * Created by maxiaobao on 2017/6/30.
 */

public class XMSSocketRequest<T extends EventResponse> {

    private final int MSG_SUCCESS = 0x1;
    private final int MSG_FAILED = 0x2;

    private XMSSocketManager socketManager;
    private EventCallback<T> callback;
    private String event;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:

                    if (callback != null) {
                        callback.onSuccess((T) msg.obj);
                    }
                    break;
                case MSG_FAILED:
                    if (callback != null) {
                        T response = (T) msg.obj;
                        String errorCode = response == null ? Errors.UNKNOWN_ERROR : response.ec;
                        String errorMessage = ErrorPrompts.getErrorMessage(event, errorCode);
                        callback.onFailed(errorCode, errorMessage);
                    }

                    break;
            }
        }
    };

    public XMSSocketRequest(XMSSocketManager manager, EventCallback<T> callback) {
        this.socketManager = manager;
        this.callback = callback;
    }


    public void emit(final String event, Object data, final Class<T> valueType) {

        this.event = event;

        if (XiaojsConfig.DEBUG) {
            String dataStr = data == null ? "null" :
                    data instanceof String? (String) data : ServiceRequest.objectToJsonString(data);
            Logger.d("the emit event:##%s##, and data:%s", event, dataStr);
        }

        if (socketManager == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the socket is null or not connect, so return failed...");
            }
            if (callback != null) {
                callback.onFailed(Errors.UNKNOWN_ERROR, null);
            }
            return;
        }

        socketManager.emit(event, data, new Ack() {
            @Override
            public void call(Object... args) {

                try {
                    T response = null;
                    if (args != null && args.length > 0) {
                        response = parseSocketResponse(args[0], valueType);
                        if (response != null && response.result) {

                            if (XiaojsConfig.DEBUG) {
                                String reponseStr = ServiceRequest.objectToJsonString(response);
                                Logger.d("the emit callback onSuccess, the reponse is:%s",
                                        reponseStr);
                            }

                            if (handler != null) {
                                Message message = new Message();
                                message.what = MSG_SUCCESS;
                                message.obj = response;
                                handler.sendMessage(message);
                            }




                            return;
                        }
                    }


                    if (XiaojsConfig.DEBUG) {
                        String errorDetail = response == null ?
                                "null" : ServiceRequest.objectToJsonString(response);
                        Logger.d("the emit callback onFailed, because reponse is:%s", errorDetail);
                    }

                    if (handler != null) {
                        Message message = new Message();
                        message.what = MSG_FAILED;
                        message.obj = response;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("the emit callback onFailed, because occur exception:%s",
                                e.getMessage());
                    }
                }
            }
        });

    }


    private T parseSocketResponse(Object obj, Class<T> valueType) {
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
