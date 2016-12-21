package cn.xiaojs.xma.data.api.service;

/**
 * Created by maxiaobao on 2016/10/29.
 */

public interface APIServiceCallback<T> {

    void onSuccess(T object);

    void onFailure(String errorCode,String errorMessage);

}
