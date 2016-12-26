package cn.xiaojs.xma.data.db;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public interface LoadDataCallback<T> {

    //Load data completed from local
    public void onLoadCompleted(T t);

    //Refreshing data from remote server
    public void onRefreshing();

    //Local data has reset
    public void onLoadReset();

    //Refresh data from remote server has completed
    public void onRefreshOver();

}
