package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;



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

    public int from = MsgFrom.FROM_PLATFORM; //0代表消息，1代表IM的


    public static class MsgFrom {
        //0代表平台消息
        public static final int FROM_PLATFORM = 0;
        //jmessage的消息
        public static final int FROM_JMESSAGE = 1;
    }

}
