package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/10/29.
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIEntity {


    //错误码
    private String ec;

    private boolean match;

    /**
     *
     * @return
     */
    public String getEc() {
        return ec;
    }

    /**
     *
     * @param ec
     */
    public void setEc(String ec) {
        this.ec = ec;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }
}
