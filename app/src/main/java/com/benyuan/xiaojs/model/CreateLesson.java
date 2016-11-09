package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/4.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLesson {

    private LiveLesson data;

    public LiveLesson getData() {
        return data;
    }

    public void setData(LiveLesson data) {
        this.data = data;
    }
}
