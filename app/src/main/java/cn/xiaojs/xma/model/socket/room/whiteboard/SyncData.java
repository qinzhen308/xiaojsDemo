package cn.xiaojs.xma.model.socket.room.whiteboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncData {
    public SyncLayer layer;
}
