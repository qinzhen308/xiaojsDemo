package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLessonsResponse {

    private int total;
    private int page;
    private int numOfPages;
    private ArrayList<ObjectsOfPage> objectsOfPage;


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


    public ArrayList<ObjectsOfPage> getObjectsOfPage() {
        return objectsOfPage;
    }

    public void setObjectsOfPage(ArrayList<ObjectsOfPage> objectsOfPage) {
        this.objectsOfPage = objectsOfPage;
    }
}
