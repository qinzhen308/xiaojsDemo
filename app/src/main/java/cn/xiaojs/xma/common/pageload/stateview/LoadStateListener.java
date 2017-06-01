package cn.xiaojs.xma.common.pageload.stateview;

import android.view.View;

/**
 * Created by Paul Z on 2017/5/15.
 */

public interface LoadStateListener {
    /**
     * 初始状态
     */
     int STATE_NORMAL = 110;
    /**
     * 下拉刷新
     */
     int STATE_UP_REFRESH = 100;
    /**
     * 上拉加载
     */
     int STATE_DOWN_REFRESH = 101;
    /**
     * 下拉加载数据异常
     */
     int STATE_UP_ERROR = 102;
    /**
     * 上拉加载数据异常
     */
     int STATE_DOWN_ERROR = 103;
    /**
     * 所有数据为空
     */
     int STATE_ALL_EMPTY = 104;
    /**
     * 单次加载数据为空
     */
    int STATE_SINGLE_EMPTY = 105;
    /**
     * 获取数据参数错误
     */
     int STATE_PARAM_ERROR = 106;
    /**
     * 数据为空时加载数据
     */
     int STATE_LOADING = 107;
    /**
     * 数据为空时加载数据异常
     */
     int STATE_LOADING_ERROR = 108;

    void onSuccess(String msg);
    void onFailed(String msg);
    void onNoData(String msg);
    void onNoNetwork(String msg);
    void onLoading(String msg);
    View getView();
}
