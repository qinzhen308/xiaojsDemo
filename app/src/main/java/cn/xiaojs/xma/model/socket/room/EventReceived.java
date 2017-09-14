package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by maxiaobao on 2017/9/13.
 */

public class EventReceived {

    @JsonIgnore
    public int eventType;
    @JsonIgnore
    public int eventCategory;
    @JsonIgnore
    public Object t;


    public EventReceived(int eventCategory,int eventType, Object t) {
        this.eventType = eventType;
        this.eventCategory = eventCategory;
        this.t = t;
    }
}
