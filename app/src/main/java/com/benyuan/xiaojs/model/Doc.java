package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doc {

    public String id;
    public String subtype;


}
