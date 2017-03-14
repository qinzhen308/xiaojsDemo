package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.model.ctl.AssignTask;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.ctl.RoomData;
import cn.xiaojs.xma.model.ctl.Statistic;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeachLesson extends EnrolledLesson implements Serializable{


    private String initiator;
    private Publish publish;
    private AssignTask[] tasksByAssignee;
    private Enroll enroll;


//    private String hp;
    private Evt[] evts;
    private String outcome;
    private Date assignedOn;
    private String account;
    private Action[] actions;
    private String process;


    public Publish getPublish() {
        return publish;
    }

    public AssignTask[] getTasksByAssignee() {
        return tasksByAssignee;
    }

    public void setTasksByAssignee(AssignTask[] tasksByAssignee) {
        this.tasksByAssignee = tasksByAssignee;
    }


    public void setPublish(Publish publish) {
        this.publish = publish;
    }
    //    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }

//    public String getSubject() {
//        return subject;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }


//    public Promotion getPromotion() {
//        return promotion;
//    }
//
//    public void setPromotion(Promotion promotion) {
//        this.promotion = promotion;
//    }



    public Enroll getEnroll() {
        return enroll;
    }

    public void setEnroll(Enroll enroll) {
        this.enroll = enroll;
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


    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }
}
