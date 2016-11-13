package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/11/13.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GNOResponse {

    public int total;
    public ArrayList<NotificationCategory> categories;


}
