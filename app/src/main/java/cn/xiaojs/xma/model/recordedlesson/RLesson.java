package cn.xiaojs.xma.model.recordedlesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.category.SubjectName;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Fee;

/**
 * Created by Paul Z on 2017/7/24.
 * 录播课
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RLesson {

    public String id;
    public String title;
    public Date createdOn;
    public Account createdBy;
    public SubjectName subject;
    public Enroll enroll;
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
    public EnrollOfAccount enrollOfCurrentAccount;


}
