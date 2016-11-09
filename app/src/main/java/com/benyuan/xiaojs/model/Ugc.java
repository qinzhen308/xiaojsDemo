package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/11/8.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ugc {

    private int visitedCount;
    private int sharedCount;
    private int favoritedCount;

    public int getVisitedCount() {
        return visitedCount;
    }

    public void setVisitedCount(int visitedCount) {
        this.visitedCount = visitedCount;
    }

    public int getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(int sharedCount) {
        this.sharedCount = sharedCount;
    }

    public int getFavoritedCount() {
        return favoritedCount;
    }

    public void setFavoritedCount(int favoritedCount) {
        this.favoritedCount = favoritedCount;
    }
}
