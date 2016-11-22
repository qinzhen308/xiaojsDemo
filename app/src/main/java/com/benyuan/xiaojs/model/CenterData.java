package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/11/22.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterData {


    private PersonUgc ugc;
    private Account.Basic basic;


    public PersonUgc getUgc() {
        return ugc;
    }

    public void setUgc(PersonUgc ugc) {
        this.ugc = ugc;
    }

    public Account.Basic getBasic() {
        return basic;
    }

    public void setBasic(Account.Basic basic) {
        this.basic = basic;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonUgc{

        private int likedCount;
        private int followedCount;
        private int favoritedCount;

        public int getLikedCount() {
            return likedCount;
        }

        public void setLikedCount(int likedCount) {
            this.likedCount = likedCount;
        }

        public int getFollowedCount() {
            return followedCount;
        }

        public void setFollowedCount(int followedCount) {
            this.followedCount = followedCount;
        }

        public int getFavoritedCount() {
            return favoritedCount;
        }

        public void setFavoritedCount(int favoritedCount) {
            this.favoritedCount = favoritedCount;
        }
    }
}
