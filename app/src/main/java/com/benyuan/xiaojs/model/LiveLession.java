package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/4.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLession {

    private String title;
    private String subject;
    private Enroll enroll;
    private int mode;
    private Fee fee;
    private Schedule schedule;
    private Promotion promotion;
    private Overview overview;
    private TeachersIntro teachersIntro;
    private Audit audit;
    private String[] tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Enroll getEnroll() {
        return enroll;
    }

    public void setEnroll(Enroll enroll) {
        this.enroll = enroll;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    public TeachersIntro getTeachersIntro() {
        return teachersIntro;
    }

    public void setTeachersIntro(TeachersIntro teachersIntro) {
        this.teachersIntro = teachersIntro;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Enroll {

        private int max;
        private int type;


        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fee {
        private String free;
        private int type;
        private int charge;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCharge() {
            return charge;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Schedule{
        private String start;
        private int duration;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Promotion {
        private String enabled;

        public String getEnabled() {
            return enabled;
        }

        public void setEnabled(String enabled) {
            this.enabled = enabled;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Overview{
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeachersIntro{
        private String biography;
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Audit{
        private String enabled;
    }




}
