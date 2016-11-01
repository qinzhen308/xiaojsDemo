package com.benyuan.xiaojs.model;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginParams {

    private long mobile;
    private String password;
    private int ct;


    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCt() {
        return ct;
    }

    public void setCt(int ct) {
        this.ct = ct;
    }
}
