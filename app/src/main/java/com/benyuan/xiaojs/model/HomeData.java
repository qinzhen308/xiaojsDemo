package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/4.
 */

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


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Competency {

        private String subject;
        private String subjectName;
        private Current current;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public Current getCurrent() {
            return current;
        }

        public void setCurrent(Current current) {
            this.current = current;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        private int TSIL;
        private int popularity;

        public int getPopularity() {
            return popularity;
        }

        public void setPopularity(int popularity) {
            this.popularity = popularity;
        }

        public int getTSIL() {
            return TSIL;
        }

        public void setTSIL(int TSIL) {
            this.TSIL = TSIL;
        }
    }

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
