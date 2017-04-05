package cn.xiaojs.xma.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.account.Account;

/**
 * Created by maxiaobao on 2017/3/31.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {
    public String id;
    public Account.Basic basic;
    public AccountSearch.AccountPhone phone;
}
