package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.model.Schedule;

/**
 * Created by maxiaobao on 2017/5/25.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassLesson implements Serializable{

    public String title;
    public boolean recordable;
    public Schedule schedule;
    public CLessonTeach teaching;

}
