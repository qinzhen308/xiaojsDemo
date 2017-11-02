package cn.xiaojs.xma.model.ctl;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.account.Account;

/**
 * Created by Paul Z on 2017/11/1.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleLesson implements Serializable{
    public String id;
    public String title;
    public String typeName;
    public String ticket;
    public Schedule schedule;
    public Enroll enroll;
    public String state;
    public Account owner;
    public Account teacher;
    public Account[] assistants;
    public Adviser[] advisers;
    public boolean accessible;
    public String playback;
    public boolean recordable;
    public String mimeType;
    public boolean imLead;
    public Classroom classroom;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Classroom{
        public String liveState;
    }


}
