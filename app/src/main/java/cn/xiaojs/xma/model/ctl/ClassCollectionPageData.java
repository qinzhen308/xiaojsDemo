package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

/**
 * Created by Paul Z on 2017/8/2.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassCollectionPageData<T> {

    public int total;
    public int limit;
    public int page;
    public int numOfPages;
    public ArrayList<T> classes;
}
