package cn.xiaojs.xma.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Schedule;

/**
 * Created by maxiaobao on 2017/3/31.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonInfo {
    public String id;
    public String title;
    public String cover;
    public Schedule schedule;
    public String subject;
    public String[] tags;

}
