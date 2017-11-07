package cn.xiaojs.xma.ui.classroom.page;

import cn.xiaojs.xma.model.live.BoardItem;

/**
 * Created by Paul Z on 2017/10/18.
 */

public class WhiteboardModel {
    public BoardItem boardItem;

    public boolean isSelected;

    public WhiteboardModel(BoardItem boardItem){
        this.boardItem=boardItem;

    }
}
