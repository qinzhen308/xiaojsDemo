package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.model.ctl.Fee;
import cn.xiaojs.xma.model.ctl.RoomData;
import cn.xiaojs.xma.model.ctl.Statistic;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrolledLesson implements Serializable{

    private String id;
    //private String createdBy;
    private String title;
    private CSubject subject;
    private int mode;
    private Statistic stats;
    private Promotion[] promotion;
    private Schedule schedule;
    private String cover;
    private Fee fee;
    private String state;
    private String ticket;
    private RoomData classroom;
    private Teacher teacher;
    private Teacher[] assistants;
    private String classType;
    private String classId;
    private String reason;

    public Teacher[] getAssistants() {
        return assistants;
    }

    public Statistic getStats() {
        return stats;
    }

    public void setStats(Statistic stats) {
        this.stats = stats;
    }

    public RoomData getClassroom() {
        return classroom;
    }

    public void setClassroom(RoomData classroom) {
        this.classroom = classroom;
    }

    public void setAssistants(Teacher[] assistants) {
        this.assistants = assistants;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CSubject getSubject() {
        return subject;
    }

    public void setSubject(CSubject subject) {
        this.subject = subject;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
