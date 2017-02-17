package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.jpush.im.android.api.model.Conversation;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationCategory {

    public String name;
    public String remarks;
    public String id;
    public int count;

    public ArrayList<Notification> notifications;

    public Conversation conversation;

}
