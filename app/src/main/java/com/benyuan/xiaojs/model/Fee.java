package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fee implements Serializable{
    private boolean free;
    private int type;
    private BigDecimal charge;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFree() {
        return free;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public void setFree(boolean free) {
        this.free = free;
    }
}
