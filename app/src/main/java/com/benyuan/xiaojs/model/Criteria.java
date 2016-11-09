package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Criteria {

    private String source;
    private Duration duration;
    private String title;
    private String state;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
