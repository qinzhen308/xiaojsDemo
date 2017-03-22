package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Fee;
import cn.xiaojs.xma.model.ctl.RoomData;
import cn.xiaojs.xma.model.ctl.Statistic;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2017/2/22
 * Desc:
 *
 * ======================================================================================== */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonHomeLesson {

    public int mode;
    public String id;
    public String title;
    public String type;
    public String state;
    public String teacher;
    public String[] assistants;
    public String[] students;
    public String ticket;
    public Promotion[] promotion;
    public Schedule schedule;
    public Fee fee;
    public String cover;
    public Enroll enroll;


}
