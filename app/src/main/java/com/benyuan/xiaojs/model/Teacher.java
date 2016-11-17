package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2016/11/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Teacher implements Serializable{
    private Account.Basic basic;
    private String id;

    public Account.Basic getBasic() {
        return basic;
    }

    public void setBasic(Account.Basic basic) {
        this.basic = basic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
