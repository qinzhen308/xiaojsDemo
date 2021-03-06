package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;

/**
 * Created by Paul Z on 2017/11/1.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardSaveTitleParams {
    public String title;
    public String draft;
    public String favoriteCommands;
    public Integer page;

}
