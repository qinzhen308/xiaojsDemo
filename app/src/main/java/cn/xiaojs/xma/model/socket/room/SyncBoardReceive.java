package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;

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
    @JsonProperty("data")
    public SyncData data;
    public long time;
    public Ctx ctx;

    @JsonProperty("data")
    public ArrayList<SyncLayer> removeLayers;

}
