package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.LiveLesson;

/**
 * Created by maxiaobao on 2016/12/27.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynPost {
    public String text;
    public String drawing;
    public Audience audience;
    public String mentioned;



    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Audience {
        public int type;
        public Doc[] chosen;
    }
}
