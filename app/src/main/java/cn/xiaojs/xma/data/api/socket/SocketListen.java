package cn.xiaojs.xma.data.api.socket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.socket.emitter.Emitter;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public class SocketListen {

    private SocketManager socketManager;

    public SocketListen(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    public <T> void on(String event, final Observer<T> observer) {
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
                    newMsg = parseSocketResponse(args[0]);
                }

                if (observer != null) {
                    Observable.just(newMsg)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer);
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

    private <T> T parseSocketResponse(Object obj) {
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
            return mapper.readValue(result, new TypeReference<T>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
