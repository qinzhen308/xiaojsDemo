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
    public PubProfile profile;
    public String relationship;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubCG {
        public String KEY;
        public String NAME;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PubProfile extends PrivateHome.PriProfile{
//        public String sex;
//        public String title;
//        public String typeName;
//        public Follow stats;
//        public boolean isPerson;
//        public String id;
//        public String name;

    }
}
