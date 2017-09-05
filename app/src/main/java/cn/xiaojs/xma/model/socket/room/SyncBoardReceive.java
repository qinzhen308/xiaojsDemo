package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;

/**
 * Created by maxiaobao on 2017/9/1.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncBoardReceive {

    public String from;
    public String board;

    public int evt;
    public int stg;
    public Object data;
    public long time;
    public Ctx ctx;
}
