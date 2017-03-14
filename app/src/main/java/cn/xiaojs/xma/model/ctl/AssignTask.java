package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import cn.xiaojs.xma.model.Action;

/**
 * Created by maxiaobao on 2017/3/14.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignTask {
    public Assignment[] assignments;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Assignment{

        public String hp;
        public Date assignedOn;
        public String account;
        public Action[] actions;
        public String process;

    }

}
