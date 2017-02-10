package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.HomeData;

/**
 * Created by maxiaobao on 2017/2/10.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHome {
    public Account.Basic basic;
    public PubCG contactGroups;
    public HomeData.Reputation reputation;
    public Follow followed;
    public int countOfUnreadN;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubCG {
        public String KEY;
        public String NAME;
    }
}
