package cn.xiaojs.xma.data.api.socket;

import cn.xiaojs.xma.model.socket.EventResponse;

/**
 * Created by maxiaobao on 2017/6/30.
 */

public interface EventCallback<T> {

    void onSuccess(T t);
    void onFailed(String errorCode, EventResponse response);

}
