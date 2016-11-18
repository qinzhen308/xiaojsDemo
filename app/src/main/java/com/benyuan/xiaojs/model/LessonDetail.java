package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2016/11/11.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonDetail {

    private String createdBy;
    private Date createdOn;
    private String typeName;
    private String title;
    private String subject;
    private int mode;
    private int __v;
    private LiveLesson.Publish publish;
    private String[] tags;
    private LiveLesson.Audit audit;
    private LiveLesson.TeachersIntro teachersIntro;
    private LiveLesson.Overview overview;
    private Promotion[] promotion;
    private Schedule schedule;
    private Fee fee;
    private Enroll enroll;
    private String id;
    private String cover;
    private Teacher teacher;


    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public LiveLesson.Publish getPublish() {
        return publish;
    }

    public void setPublish(LiveLesson.Publish publish) {
        this.publish = publish;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public LiveLesson.Audit getAudit() {
        return audit;
    }

    public void setAudit(LiveLesson.Audit audit) {
        this.audit = audit;
    }

    public LiveLesson.TeachersIntro getTeachersIntro() {
        return teachersIntro;
    }

    public void setTeachersIntro(LiveLesson.TeachersIntro teachersIntro) {
        this.teachersIntro = teachersIntro;
    }

    public LiveLesson.Overview getOverview() {
        return overview;
    }

    public void setOverview(LiveLesson.Overview overview) {
        this.overview = overview;
    }

    public Promotion[] getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion[] promotion) {
        this.promotion = promotion;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public Enroll getEnroll() {
        return enroll;
    }

    public void setEnroll(Enroll enroll) {
        this.enroll = enroll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
