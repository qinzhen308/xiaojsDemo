package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.exceptions.JMFileSizeExceedException;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationCategory{

    public String name;
    public String remarks;
    public String id;
    public int count;

    public ArrayList<Notification> notifications;

    public Conversation conversation;

    public int from = MsgFrom.FROM_PLATFORM; //0代表消息，1代表jmessage的


    public static class MsgFrom {
        //0代表平台消息
        public static final int FROM_PLATFORM = 0;
        //jmessage的消息
        public static final int FROM_JMESSAGE = 1;
    }

}
