package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/11/13.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GNOResponse {

    public int total;
    public ArrayList<NotificationCategory> categories;


}
