package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2016/11/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recurrence implements Serializable{


    private Pattern pattern;

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
