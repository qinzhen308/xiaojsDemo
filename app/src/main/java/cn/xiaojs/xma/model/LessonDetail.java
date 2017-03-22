package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.ctl.Statistic;

/**
 * Created by maxiaobao on 2016/11/11.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LessonDetail implements Serializable {

    private String createdBy;
    private Date createdOn;
    private String typeName;
    private String title;
    private CSubject subject;
    private int mode;
    private Statistic stats;
    private int __v;
    //private LiveLesson.Publish publish;
    private String[] tags;
    private LiveLesson.Audit audit;
    private LiveLesson.TeachersIntro teachersIntro;
    private LiveLesson.Overview overview;
    private Promotion[] promotion;
    private Schedule schedule;
    private Price fee;
    private Enroll enroll;
    private String id;
    private String cover;
    private Teacher teacher;
    private String state;
    private Publish publish;

    public String ticket;
    public boolean isEnrolled;
    public String enrollState;



    public Statistic getStats() {
        return stats;
    }

    public void setStats(Statistic stats) {
        this.stats = stats;
    }

    public CSubject getSubject() {
        return subject;
    }

    public void setSubject(CSubject subject) {
        this.subject = subject;
    }

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

    public Publish getPublish() {
        return publish;
    }

    public void setPublish(Publish publish) {
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

    public Price getFee() {
        return fee;
    }

    public void setFee(Price fee) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
