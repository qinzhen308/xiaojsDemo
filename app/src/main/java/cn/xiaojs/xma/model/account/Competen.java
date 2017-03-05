package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import cn.xiaojs.xma.model.Competency;

/**
 * Created by maxiaobao on 2017/2/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Competen {
    private String subject;
    private Competency.Current current;
}
