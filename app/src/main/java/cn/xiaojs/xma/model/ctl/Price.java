package cn.xiaojs.xma.model.ctl;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2017/1/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price extends Fee implements Serializable{

    public float total;
    public Discounted discounted;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Discounted implements Serializable{
        public float subtotal;
        public float ratio;
        public float saved;
        public Applied[] applied;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Applied implements Serializable {
        public float discount;
        public int quota;
        public int before;

    }
}
