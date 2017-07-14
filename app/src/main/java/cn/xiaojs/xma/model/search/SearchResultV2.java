package cn.xiaojs.xma.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.account.Stats;

/**
 * Created by Paul Z on 2017/7/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultV2 {

    public String id;
    public String score;
    public Account.Basic basic;
    public String _name;
    public String typeName;

    public String mobile;
    public String title;
    public String _title;
    public Account teacher;
//    public Account[] assistants;
//    public Account[] advisers;

    public Schedule schedule;

    public Stats stats;

    public boolean isFollowed;


    public String getType(){
        return typeName;
    }



}
