package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.model.Account;

/**
 * Created by maxiaobao on 2016/12/12.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactGroup {
    public String id;
    public String name;
    //public ArrayList<Contact> contacts;
    //////////////////////////////////////////////////

    public String set;
    public long group;
    public Account.SimpleAccount subject;
    public ArrayList<Contact> collection;
}
