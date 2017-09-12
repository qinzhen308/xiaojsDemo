package cn.xiaojs.xma.model.socket.room.whiteboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Paul Z on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Command implements Serializable{

    public int f;
    public ArrayList<SyncLayer> p;
    public String m;

}
