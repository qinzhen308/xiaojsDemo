package cn.xiaojs.xma.ui.classroom.whiteboard.sync;

/**
 * Created by Administrator on 2017/9/13.
 */

public interface SyncDrawingListener {

    public void onBegin(Object data);
    public void onGoing(Object data);
    public void onFinished(Object data);


}
