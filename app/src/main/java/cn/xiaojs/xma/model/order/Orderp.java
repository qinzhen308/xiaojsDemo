package cn.xiaojs.xma.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/2/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Orderp {

    public BuyItem[] items;
    public float amount;
    public String remarks;
    public String description;
    public String currency;
    public String coupon;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BuyItem{
        public String id;
        public int type;
        public String promotion;
        public int quantity = 1;
        public float total;
        public String remarks;
    }


}
