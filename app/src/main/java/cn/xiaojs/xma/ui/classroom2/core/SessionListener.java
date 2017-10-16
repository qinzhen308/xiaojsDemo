package cn.xiaojs.xma.ui.classroom2.core;

/**
 * 教室状态回调
 */
public interface SessionListener {
    /**
     * 连接教室成功
     */
    void connectSuccess();

    /**
     * 连接教室失败，或者与教室断开连接
     */
    void connectFailed(String errorCode, String errorMessage);

    /**
     * 网络状态改变
     */
    void networkStateChanged(int state);
}