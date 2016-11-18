package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/11/4.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeData {

    private Reputation reputation;
    private Followed followed;
    private int countOfUnreadN;

    public Reputation getReputation() {
        return reputation;
    }

    public void setReputation(Reputation reputation) {
        this.reputation = reputation;
    }

    public Followed getFollowed() {
        return followed;
    }

    public void setFollowed(Followed followed) {
        this.followed = followed;
    }

    public int getCountOfUnreadN() {
        return countOfUnreadN;
    }

    public void setCountOfUnreadN(int countOfUnreadN) {
        this.countOfUnreadN = countOfUnreadN;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Reputation{
        private String grade;
        private int popularity;
        private Competency[] competencies;

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public int getPopularity() {
            return popularity;
        }

        public void setPopularity(int popularity) {
            this.popularity = popularity;
        }

        public Competency[] getCompetencies() {
            return competencies;
        }

        public void setCompetencies(Competency[] competencies) {
            this.competencies = competencies;
        }
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Followed {

        private int me;
        private int byMyself;

        public int getMe() {
            return me;
        }

        public void setMe(int me) {
            this.me = me;
        }

        public int getByMyself() {
            return byMyself;
        }

        public void setByMyself(int byMyself) {
            this.byMyself = byMyself;
        }
    }
}
