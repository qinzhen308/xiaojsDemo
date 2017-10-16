package cn.xiaojs.xma.ui.classroom.whiteboard.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.data.api.serialize.SyncDataDeserializer;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;

/**
 * Created by Paul Z on 2017/9/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncBoardEvtBegin {

    public String from;
    public String board;
    public String id;

    public int evt;
    public int stg;

    public long time;
    public Ctx ctx;

    public SyncData data;


    @Override
    public String toString() {
        return ServiceRequest.objectToJsonString(this);
    }
}
