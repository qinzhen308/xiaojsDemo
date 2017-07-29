package cn.xiaojs.xma.model.recordedlesson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.Fee;

/**
 * Created by Paul Z on 2017/7/24.
 * 录播课
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RLessonDetail implements Serializable{

    public String id;
    public String title;
    public Date createdOn;
    public Account createdBy;
    public CSubject subject;
    public EnrollMode enroll;
    public String cover;
    public String state;
    public Fee fee;
    public Publish publish;
    public Account[] teachers;
    public Account[] assistants;
    public String[] tags;
    public LiveLesson.Overview overview;
    public String reason;
    public Expire expire;
    public String enrollState;
    public EnrollOfAccount enrollOfCurrentAccount;


    public ArrayList<Section> sections;
}
