package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

    public String id;
    public Date createdOn;
    public String body;
    public String process;
    public Initiator initiator;
    public Action[] actions;
    public Doc doc;
    public String state;

    public boolean read;//是否已读，本地字段
}
