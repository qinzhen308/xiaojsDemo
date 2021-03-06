package cn.xiaojs.xma.model.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/6/16.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {
    public boolean result;
    public String ec;
    //public String details;
    public String errorMessage;
}
