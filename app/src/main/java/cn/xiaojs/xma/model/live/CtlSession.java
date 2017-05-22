package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by maxiaobao on 2017/1/9.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CtlSession implements Serializable{

    public String secret;
    public String ticket;
    public Ctl ctl;
    public boolean accessible;
    public ConnectType connected;
    public String psType;
    public String state;
    public long startOn;
    public long finishOn;
    public long hasTaken;
    public String publishUrl;
    public String playUrl;
    public int streamMode;
    public String claimedBy;
    public String titleOfPrimary;
    public int mode;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ctl implements Serializable{
        public String id;
        public String subtype;
        public String subject;
        public String title;
        public String state;
        public String startedOn;
        public long duration;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectType implements Serializable{
        public int app;
    }

    @Override
    public String toString() {
        return "CtlSession{" +
                "secret='" + secret + '\'' +
                ", ticket='" + ticket + '\'' +
                ", ctl=" + ctl +
                ", accessible=" + accessible +
                ", connected=" + connected +
                ", psType='" + psType + '\'' +
                ", state='" + state + '\'' +
                ", startOn=" + startOn +
                ", finishOn=" + finishOn +
                ", hasTaken=" + hasTaken +
                ", publishUrl='" + publishUrl + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", streamMode=" + streamMode +
                ", claimedBy='" + claimedBy + '\'' +
                ", titleOfPrimary='" + titleOfPrimary + '\'' +
                ", mode=" + mode +
                '}';
    }
}
