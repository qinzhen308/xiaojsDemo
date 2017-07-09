package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/7/7.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncClassStateReceive {
    public String event;
    public String from;
    public String to;
    public cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse.Current current;
    public cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse.Next next;
    public cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse.Volatiles[] volatiles;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        public String id;
        public String title;
        public cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse.Schedule schedule;
        public String typeName;
        public boolean playback;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Next {
        public String id;
        public String title;
        public String typeName;
        public cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse.Schedule schedule;
        public boolean playback;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Schedule {
        public Date start;
        public int duration;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Volatiles {
        public String accountId;
        public String psType;
    }
}
