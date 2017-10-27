package cn.xiaojs.xma.ui.classroom.whiteboard.sync;

import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;

/**
 * Created by Paul Z on 2017/10/26.
 * 将图层格式化为同步数据，也许会用于本地保持，线上保存
 */

public interface SyncLayerBuilder {

    public SyncLayer onBuildLayer();

}
