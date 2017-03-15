package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.Teacher;

/**
 * Created by maxiaobao on 2017/3/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveClass implements Serializable{
    public ArrayList<LiveItem> enrolled;
    public ArrayList<LiveItem> taught;
}
