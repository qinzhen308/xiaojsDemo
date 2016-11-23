package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2016/11/8.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fee implements Serializable{
    private boolean free;
    private int type;
    private float charge;
    private float total;
    private Discounted discounted;
    private Applied[] applied;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFree() {
        return free;
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Discounted getDiscounted() {
        return discounted;
    }

    public void setDiscounted(Discounted discounted) {
        this.discounted = discounted;
    }

    public Applied[] getApplied() {
        return applied;
    }

    public void setApplied(Applied[] applied) {
        this.applied = applied;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Discounted implements Serializable{
        private float subtotal;
        private float ratio;
        private float saved;

        public float getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(float subtotal) {
            this.subtotal = subtotal;
        }

        public float getRatio() {
            return ratio;
        }

        public void setRatio(float ratio) {
            this.ratio = ratio;
        }

        public float getSaved() {
            return saved;
        }

        public void setSaved(float saved) {
            this.saved = saved;
        }
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Applied implements Serializable {
        private float discount;
        private int quota;
        private int before;

        public float getDiscount() {
            return discount;
        }

        public void setDiscount(float discount) {
            this.discount = discount;
        }

        public int getQuota() {
            return quota;
        }

        public void setQuota(int quota) {
            this.quota = quota;
        }

        public int getBefore() {
            return before;
        }

        public void setBefore(int before) {
            this.before = before;
        }
    }
}
