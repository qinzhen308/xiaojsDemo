package com.benyuan.xiaojs.data.api.model;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class RegisterInfo {

    private long mobile;
    private String password;
    private String username;
    private int code;

    /**
     *
     * @return
     * The mobile
     */
    public long getMobile() {
        return mobile;
    }

    /**
     *
     * @param mobile
     * The mobile
     */
    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    /**
     *
     * @return
     * The password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The code
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(int code) {
        this.code = code;
    }


}
