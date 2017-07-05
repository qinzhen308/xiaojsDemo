package cn.xiaojs.xma.data.api.socket;

/**
 * Created by maxiaobao on 2017/6/30.
 */

public interface EventCallback<T> {

    void onSuccess(T t);
    void onFailed(String errorCode, String errorMessage);

}
