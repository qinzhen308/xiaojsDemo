package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.model.social.Dynamic;

/**
 * Created by maxiaobao on 2017/3/22.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeDynamic implements Serializable{

    public String createdBy;
    public Date createdOn;
    public String typeName;
    public Dynamic.DynStatus stats;
    public Dynamic.DynBody body;
    public String id;
}
