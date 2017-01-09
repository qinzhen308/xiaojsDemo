package cn.xiaojs.xma.model.LiveSession;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/1/9.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CtlSession {

    public String secret;
    public String cs;
    public Ctl ctl;
    public boolean accessible;
    public ConnectType connected;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ctl {
        public String id;
        public String subtype;
        public String subject;
        public String title;
        public String state;
        public String startedOn;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectType {
        public int app;
    }
}
