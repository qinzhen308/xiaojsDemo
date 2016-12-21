package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeachLesson implements Serializable{

    //private String createdBy;
    private String title;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String subject;
    private int mode;
    private Ugc ugc;
    //private Promotion promotion;
    private Schedule schedule;
    private Fee fee;
    private Enroll enroll;
    private String id;
    private String cover;
//    private String hp;
    private Evt[] evts;
    private String outcome;
    private Date assignedOn;
    private String account;
    private Action[] actions;
    private String process;
    private String state;
    private String initiator;

//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getSubject() {
//        return subject;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Ugc getUgc() {
        return ugc;
    }

    public void setUgc(Ugc ugc) {
        this.ugc = ugc;
    }

//    public Promotion getPromotion() {
//        return promotion;
//    }
//
//    public void setPromotion(Promotion promotion) {
//        this.promotion = promotion;
//    }


    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

//    public String getHp() {
//        return hp;
//    }
//
//    public void setHp(String hp) {
//        this.hp = hp;
//    }

    public Evt[] getEvts() {
        return evts;
    }

    public void setEvts(Evt[] evts) {
        this.evts = evts;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Date getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(Date assignedOn) {
        this.assignedOn = assignedOn;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Action[] getActions() {
        return actions;
    }

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }
}
