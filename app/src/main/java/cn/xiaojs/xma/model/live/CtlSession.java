package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/1/9.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CtlSession {

    public String secret;
    public Ctl ctl;
    public boolean accessible;
    public ConnectType connected;
    public String psType;
    public String state;
    public long startOn;
    public long finishOn;
    public String publishUrl;
    public String playUrl;
    public int streamMode;
    public String claimedBy;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ctl {
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
    public static class ConnectType {
        public int app;
    }
}
