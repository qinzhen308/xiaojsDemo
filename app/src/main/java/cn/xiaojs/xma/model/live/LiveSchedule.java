package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.account.Account;

/**
 * Created by maxiaobao on 2017/6/8.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveSchedule {
    public Schedule schedule;
    public Account lead;
    public String title;
    public String id;
    public String next;
    public String playback;
    public String state;
    public Room classroom;

//    imAssistant
}
