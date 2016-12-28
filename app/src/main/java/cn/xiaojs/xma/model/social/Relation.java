package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/12/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Relation {
    public int followType;
}
