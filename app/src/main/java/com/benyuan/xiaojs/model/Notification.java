package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

    public String id;
    public Date createdOn;
    public String body;
    public String process;
    public Initiator initiator;
    public Action[] actions;
    public Doc doc;
}
