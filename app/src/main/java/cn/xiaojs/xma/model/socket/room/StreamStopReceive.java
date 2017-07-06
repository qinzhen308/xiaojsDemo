package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/7/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamStopReceive {
    public String claimedBy;
    public int streamType;
    public int startOn;
    public boolean preemptive;
    public String deprivedBy;
    public boolean liveNow;
    public boolean stillOnLive;
}
