package cn.xiaojs.xma.model.ctl;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/3/15
 * Desc:
 *
 * ======================================================================================== */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.Teacher;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveItem implements Serializable{
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
