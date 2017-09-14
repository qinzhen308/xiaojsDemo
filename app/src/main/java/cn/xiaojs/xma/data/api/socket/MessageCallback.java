package cn.xiaojs.xma.data.api.socket;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public interface MessageCallback<T> {
    void onMessage(int eventCategory, int eventType, T message);
}
