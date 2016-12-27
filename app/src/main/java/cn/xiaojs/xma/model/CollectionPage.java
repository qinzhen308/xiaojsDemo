package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxiaobao on 2016/12/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionPage<T> {
    public int totalUpdates;
    public int total;
    public int page;
    public int numOfPages;
    public ArrayList<T> objectsOfPage;
}
