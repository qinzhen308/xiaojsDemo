package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;

/**
 * Created by maxiaobao on 2017/1/9.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CtlSession implements Serializable {

    public String secret;
    public String ticket;
    public Ctl ctl;
    public Cls cls;
    public boolean accessible;
    public boolean preemptive;
    public int mode;
    public ConnectType connected;
    public String psType;
    public String psTypeInLesson;
    public String state;
    public boolean facultyTalk;
    public long startOn;
    public long finishOn;
    public long hasTaken;
    public String publishUrl;
    public String playUrl;
    public int streamMode;
    public int streamType = Live.StreamType.NONE;
    public String claimedBy;
    public String titleOfPrimary;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ctl implements Serializable {
        public String id;
        public String subtype;
        public String subject;
        public String title;
        public String state;
        public String startedOn;
        public long duration;
        public CtlLead lead;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cls implements Serializable {
        public String id;
        public String subtype;
        public String title;
        public String state;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectType implements Serializable {
        public int app;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CtlLead {
        public String id;
        public String name;
    }


    @Override
    public String toString() {
        return "CtlSession{" +
                "secret='" + secret + '\'' +
                ", ticket='" + ticket + '\'' +
                ", ctl=" + ctl +
                ", cls=" + cls +
                ", accessible=" + accessible +
                ", preemptive=" + preemptive +
                ", mode=" + mode +
                ", connected=" + connected +
                ", psType='" + psType + '\'' +
                ", psTypeInLesson='" + psTypeInLesson + '\'' +
                ", state='" + state + '\'' +
                ", facultyTalk=" + facultyTalk +
                ", startOn=" + startOn +
                ", finishOn=" + finishOn +
                ", hasTaken=" + hasTaken +
                ", publishUrl='" + publishUrl + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", streamMode=" + streamMode +
                ", claimedBy='" + claimedBy + '\'' +
                ", titleOfPrimary='" + titleOfPrimary + '\'' +
                '}';
    }
}
