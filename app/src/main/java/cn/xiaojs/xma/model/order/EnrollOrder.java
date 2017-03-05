package cn.xiaojs.xma.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/2/27.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollOrder {

    public int no;
    public float amount;

    public PaymentOrder items;
    public String promotion;
    public int quantity;
    public String body;
}
