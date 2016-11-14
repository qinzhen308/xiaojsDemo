package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationCategory {

    public String name;
    public String remarks;
    public String id;
    public int count;

    public ArrayList<Notification> notifications;


}
