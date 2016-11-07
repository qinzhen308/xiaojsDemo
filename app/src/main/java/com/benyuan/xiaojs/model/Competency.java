package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/4.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Competency {

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

}
