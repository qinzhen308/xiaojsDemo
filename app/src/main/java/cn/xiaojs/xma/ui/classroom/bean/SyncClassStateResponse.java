package cn.xiaojs.xma.ui.classroom.bean;
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
 * Date:2017/6/5
 * Desc: Receives an event that indicates a state transition on the class was occurred.
 *
 * ======================================================================================== */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncClassStateResponse {
    public String event;
    public String from;
    public String to;
    public Current current;
    public Next next;
    public Volatiles[] volatiles;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        public String id;
        public String title;
        public Schedule schedule;
        public String typeName;
        public boolean playback;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Next {
        public String id;
        public String title;
        public String typeName;
        public Schedule schedule;
        public boolean playback;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Schedule {
        public Date start;
        public int duration;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Volatiles {
        public String accountId;
        public String psType;
    }

}
