package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.model.socket.room.whiteboard.Board;
import cn.xiaojs.xma.model.socket.room.whiteboard.Sharing;

/**
 * Created by maxiaobao on 2017/9/1.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareboardReceive implements Serializable{

    public String from;
    public Board board;
    public Sharing sharing;
    public String publishUrl;
    public String strokeColor;
    public boolean grantControl;

}
