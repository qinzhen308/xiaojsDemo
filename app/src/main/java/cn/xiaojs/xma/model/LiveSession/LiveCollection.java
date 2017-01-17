package cn.xiaojs.xma.model.LiveSession;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2017/1/17.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveCollection<T> {
    public int total;
    public int current;
    public boolean open;
    public ArrayList<T> attendees;

}
