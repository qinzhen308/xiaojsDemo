package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.account.Account;

/**
 * Created by Paul Z on 2017/8/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassByUser implements Serializable{
    public String title;
    public Date createdOn;
    public Account createdBy;
    public Account[] teachers;
    public String id;
    public ClassJoin join;
    public String state;
    public String typeName;
    public Publish publish;
}
