package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2017/3/29.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionPageData<T> {

    public int total;
    public int limit;
    public int page;
    public int numOfPages;
    public ArrayList<T> lessons;
    public ArrayList<T> accounts;
}
