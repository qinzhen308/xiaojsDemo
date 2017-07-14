package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.live.TalkItem;

/**
 * Created by maxiaobao on 2017/7/7.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Talk {
    public String from;
    public String to;
    public long time;
    public TalkContent body;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TalkContent{
        public String text;
        public int contentType;
        public TalkItem.QiNiuImg drawing;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QiNiuImg{
        public String name;
    }
}