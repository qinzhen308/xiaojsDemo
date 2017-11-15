package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.model.live.TalkItem;

/**
 * Created by maxiaobao on 2017/7/7.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Talk implements Serializable{
    public int type;
    public String from;
    public String to;
    public long time;
    public TalkContent body;

    //blew use in socket talk received,not rest api use
    public boolean sync;
    public long stime;
    public String name;
    public String subtype;
    public boolean retainOnTalk;
    public String signature;


    ////////use member leave or join
    public String accountId;
    public String psType;
    public int xa;
    public String xav;
    public int sort;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TalkContent implements Serializable{
        public String text;
        public int contentType;
        public TalkItem.QiNiuImg drawing;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QiNiuImg implements Serializable{
        public String name;
    }
}
