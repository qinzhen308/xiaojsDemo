package cn.xiaojs.xma.ui.classroom.whiteboard.sync.model;

import android.graphics.PointF;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.data.api.serialize.SyncDataDeserializer;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;

/**
 * Created by Paul Z on 2017/9/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncBoardEvtGoing {

    public String from;
    public String board;

    public int evt;
    public int stg;

    public GoingPoints data;
    public long time;

    public static class GoingPoints{
        public PointF startPos;
        public PointF endPos;
    }

}
