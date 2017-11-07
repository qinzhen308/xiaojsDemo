package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.account.Account;

/**
 * Created by maxiaobao on 2017/5/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassInfo implements Serializable{
    public String title;
    public String ticket;
    public Date createdOn;
    public String createdBy;
    //public String adviserName;
    public Account[] advisers;
    public Account owner;
    public int lessons;
    public int students;
    public String id;
    public ClassJoin join;
    public String state;

    public Publish publish;
    public boolean visitor;
    public boolean talk;
    public boolean library;
}
