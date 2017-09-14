package cn.xiaojs.xma.ui.classroom.whiteboard.sync;

/**
 * Created by Paul Z on 2017/9/13.
 */

public interface SyncCollector<T> {

    T onCollect(int type);

}
