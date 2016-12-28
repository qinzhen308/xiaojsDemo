package cn.xiaojs.xma.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.xiaojs.xma.model.Account;

/**
 * Created by maxiaobao on 2016/12/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSearch {
    public String _type;
    public String _id;
    public SearchInfo _source;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchInfo  {
        public AccountPhone phone;
        public String typeName;
        public Account.Basic basic;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccountPhone {
        public String subsNum;
    }

}


