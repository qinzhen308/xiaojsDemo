package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/1/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class UpTokenParam {

    public String type;
    public int quantity;

}
