package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by maxiaobao on 2016/12/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionCalendar<T> {
    public int limit;
    public int total;
    public int page;
    public Date start;
    public Date end;
    public List<T> calendar;
}
