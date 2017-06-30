package cn.xiaojs.xma.data.api.socket;


import io.socket.client.Ack;

/**
 * Created by maxiaobao on 2017/6/30.
 */

public class SocketRequest<T> {

    private SocketManager socketManager;
    private T t;

    public void emit(final String event, Object data, EventCallback<T> callback) {

        socketManager.emit(event, data, new Ack() {
            @Override
            public void call(Object... args) {

                callback.callback();
            }
        });

    }


}
