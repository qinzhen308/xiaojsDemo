package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/12/27.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {
    public String ec;
    public String details;
}
