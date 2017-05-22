package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import cn.xiaojs.xma.model.CSubject;

/**
 * Created by maxiaobao on 2017/5/22.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgTeacher {

    public String id;
    public String cover;
    public String name;
    public String sex;
    //public Date birthday;
    public String remarks;
    public Stats stats;
    public CSubject[] competencies;


}
