package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/7/7.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncStateReceive {
    public String event;
    public String from;
    public String to;
    public cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse.TimeLine timeline;
    public String psType;

    /**
     * The timeline details. Use with cautious due to several attributes are available on specific
     * states only.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeLine {
        public boolean hasPostponed;
        public cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse.Duration currentDuration;
        public Date startOnDate;
        public long startOn;
        public Date finishOnDate;
        public long finishOn;
        public Date conveneOn;
        public Date restartOn;
        public Date dismissDue;
        public Date resumeDue;
        public long hasTaken;
    }

    /**
     * The current duration.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Duration {
        public Date start;
        public long duration;
    }

}

