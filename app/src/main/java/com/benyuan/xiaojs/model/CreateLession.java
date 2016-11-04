package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/4.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLession {

    private LiveLession data;

    public LiveLession getData() {
        return data;
    }

    public void setData(LiveLession data) {
        this.data = data;
    }
}
