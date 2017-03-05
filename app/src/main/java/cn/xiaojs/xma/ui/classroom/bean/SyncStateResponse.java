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
 * Date:2017/2/12
 * Desc:
 *
 * ======================================================================================== */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncStateResponse {
    public String event;
    public String from;
    public String to;
    public TimeLine timeline;

    /**
     * The timeline details. Use with cautious due to several attributes are available on specific
     * states only.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeLine {
        public boolean hasPostponed;
        public Duration currentDuration;
        public Date startOnDate;
        public long startOn;
        public Date finishOnDate;
        public long finishOn;
        public Date conveneOn;
        public Date restartOn;
        public Date dismissDue;
        public Date resumeDue;
        public long hasTaken;
    }

    /**
     * The current duration.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Duration {
        public Date start;
        public long duration;
    }

}
