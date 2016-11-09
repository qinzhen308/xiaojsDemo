package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by maxiaobao on 2016/11/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Duration {

    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
