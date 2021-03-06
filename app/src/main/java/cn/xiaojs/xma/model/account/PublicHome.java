package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.HomeData;
import cn.xiaojs.xma.model.PersonHomeLesson;
import cn.xiaojs.xma.model.social.Dynamic;

/**
 * Created by maxiaobao on 2017/2/10.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHome {

    public CSubject[] competencies;
    public int countOfUnreadN;
    public PubProfile profile;
    public ArrayList<PersonHomeLesson> lessons;
    public ArrayList<Dynamic> activities;
    public String relationship;
    public boolean isTeacher;
    public boolean isFollowed;

    public Account.Basic basic;
    public PubCG contactGroups;
    public HomeData.Reputation reputation;
    public Follow followed;

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
