package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.live.Attendee;

/**
 * Created by maxiaobao on 2017/3/14.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomData {

    public int total;
    public int current;
    public Attendee[] attendees;
    public String liveState;
    public long startOn;
    public long finishOn;
    public long hasTaken;


}
