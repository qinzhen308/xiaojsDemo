package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GELessonsResponse {

    private int total;
    private int page;
    private int numOfPages;
    private ArrayList<EnrolledLesson> objectsOfPage;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(int numOfPages) {
        this.numOfPages = numOfPages;
    }

    public ArrayList<EnrolledLesson> getObjectsOfPage() {
        return objectsOfPage;
    }

    public void setObjectsOfPage(ArrayList<EnrolledLesson> objectsOfPage) {
        this.objectsOfPage = objectsOfPage;
    }
}
