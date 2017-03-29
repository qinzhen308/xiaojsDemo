package cn.xiaojs.xma.model;
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
 * Date:2017/3/23
 * Desc:
 *
 * ======================================================================================== */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import cn.xiaojs.xma.model.account.Stats;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Fee;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonHomeUserLesson {
    public String createdBy;
    public String cover;
    public int mode;
    public String subject;
    public String ticket;
    public Stats stats;
    public Publish publish;
    public List<Promotion> promotion;
    public Schedule schedule;
    public Fee fee;
    public Enroll enroll;
    public String title;
    public String id;
    public String teacher;
    public String state;
}
