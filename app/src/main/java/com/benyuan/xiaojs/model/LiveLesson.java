package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by maxiaobao on 2016/11/4.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLesson {

    private String title;
    private String subject;
    private Enroll enroll;
    private int mode;
    private Fee fee;
    private Schedule schedule;
    private String cover;
    private Promotion promotion;
    private Overview overview;
    private TeachersIntro teachersIntro;
    private Audit audit;
    private String[] tags;
    private Publish publish;
    private boolean autoOnShelves;

    public Publish getPublish() {
        return publish;
    }

    public void setPublish(Publish publish) {
        this.publish = publish;
    }

    public boolean isAutoOnShelves() {
        return autoOnShelves;
    }

    public void setAutoOnShelves(boolean autoOnShelves) {
        this.autoOnShelves = autoOnShelves;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

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
    public static class Overview{
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeachersIntro{
        private boolean biography;
        private String text;

        public boolean isBiography() {
            return biography;
        }

        public void setBiography(boolean biography) {
            this.biography = biography;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Audit{
        private boolean enabled;
        private String[] grantedTo;
        private boolean visibleToStudents;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getGrantedTo() {
            return grantedTo;
        }

        public void setGrantedTo(String[] grantedTo) {
            this.grantedTo = grantedTo;
        }

        public boolean isVisibleToStudents() {
            return visibleToStudents;
        }

        public void setVisibleToStudents(boolean visibleToStudents) {
            this.visibleToStudents = visibleToStudents;
        }
    }


    public static class Rule {
        private int before;
        private boolean enabled;
        private int discount;
        private int quota;

        public int getBefore() {
            return before;
        }

        public void setBefore(int before) {
            this.before = before;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public int getQuota() {
            return quota;
        }

        public void setQuota(int quota) {
            this.quota = quota;
        }
    }


    public static class Publish{
        private boolean onShelves;

        public boolean isOnShelves() {
            return onShelves;
        }

        public void setOnShelves(boolean onShelves) {
            this.onShelves = onShelves;
        }
    }


}
