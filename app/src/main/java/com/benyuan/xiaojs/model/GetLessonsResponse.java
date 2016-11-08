package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLessonsResponse {

    private int total;
    private int page;
    private int numOfPages;
    private ObjectsOfPage[] objectsOfPage;


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

    public ObjectsOfPage[] getObjectsOfPage() {
        return objectsOfPage;
    }

    public void setObjectsOfPage(ObjectsOfPage[] objectsOfPage) {
        this.objectsOfPage = objectsOfPage;
    }
}
