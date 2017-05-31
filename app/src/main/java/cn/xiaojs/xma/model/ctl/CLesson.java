package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.account.Account;

/**
 * Created by maxiaobao on 2017/5/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CLesson {
    public String id;
    public String title;
    public Schedule schedule;
    public String state;
    public Account owner;
    public Account teacher;
    public Account[] assistants;
    @JsonProperty("class")
    public ClassInfo classInfo;
}
