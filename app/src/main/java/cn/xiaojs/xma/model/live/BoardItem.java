package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Set;

import cn.xiaojs.xma.model.socket.room.whiteboard.Drawing;

/**
 * Created by maxiaobao on 2017/2/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardItem extends Board implements Serializable{
    public String id;
    public boolean active;
    public boolean primary;
    public int type;
    public String title;
    public Drawing drawing;
    public String snapshot;

}
