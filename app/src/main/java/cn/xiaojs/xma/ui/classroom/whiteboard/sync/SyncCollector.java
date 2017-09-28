package cn.xiaojs.xma.ui.classroom.whiteboard.sync;

/**
 * Created by Paul Z on 2017/9/13.
 */

public interface SyncCollector<T> {

    /**
     * 创建图层的采集
     * @param type
     * @return
     */
    T onCollect(int type);

    /**
     * 除了创建外的其他数据采集
     * @param action
     * @param type
     * @return
     */
    T onCollect(int action,int type);

}
