package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.Teacher;

/**
 * Created by maxiaobao on 2017/3/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveClass {

    public String id;
    //private String createdBy;
    public String title;
    public CSubject subject;
    public int mode;
    public Statistic stats;
    public Schedule schedule;
    public String cover;
    public Enroll enroll;
    public String state;
    public String ticket;
    public RoomData classroom;
    public Teacher teacher;
    public Teacher[] assistants;
    public String classType;
    public String classId;
    public boolean isInvited;
    public boolean isSelfOwner;
}
