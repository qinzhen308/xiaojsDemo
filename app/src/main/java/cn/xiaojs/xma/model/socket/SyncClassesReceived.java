package cn.xiaojs.xma.model.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/11/14.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncClassesReceived {

    public String id;
    public String change;
    public String subtype;
    public String owner;
    public String ownerId;
    public String ticket;
    public String cover;
    public String title;
    public String state;
}
