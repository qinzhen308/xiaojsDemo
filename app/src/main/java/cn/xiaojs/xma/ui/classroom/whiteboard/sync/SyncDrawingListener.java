package cn.xiaojs.xma.ui.classroom.whiteboard.sync;

/**
 * Created by Administrator on 2017/9/13.
 */

public interface SyncDrawingListener {

    public void onBegin(String data);
    public void onGoing(String data);
    public void onFinished(String data);


}
