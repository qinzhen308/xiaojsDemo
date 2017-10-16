package cn.xiaojs.xma.ui.classroom.whiteboard.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.serialize.SyncDataDeserializer;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;

/**
 * Created by Paul Z on 2017/9/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncBoardFinished {

    public String from;
    public String board;
    public String id;

    public int evt;
    public int stg;

    @JsonDeserialize(using = SyncDataDeserializer.class)
    public SyncData data;
    public long time;
    public Ctx ctx;

    @Override
    public String toString() {
        return ServiceRequest.objectToJsonString(this);
    }

}
