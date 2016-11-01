package com.benyuan.xiaojs.model;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class Account {

    private Basic basic;

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }





    public static class Basic{

        private String title;

        // true means man, otherwise woman
        private boolean sex;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isSex() {
            return sex;
        }

        public void setSex(boolean sex) {
            this.sex = sex;
        }
    }
}
