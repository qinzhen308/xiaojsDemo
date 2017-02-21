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
    public String amount;
    public String remarks;
    public String description;
    public String currency;
    public String coupon;


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BuyItem{
        public String id;
        public String type;
        public String promotion;
        public String quantity;
        public String total;
        public String remarks;
    }


}
