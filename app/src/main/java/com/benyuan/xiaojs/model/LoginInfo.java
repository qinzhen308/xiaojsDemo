package com.benyuan.xiaojs.model;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginInfo extends APIEntity {

    private User user;
    private Upgrade upgrade;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(Upgrade upgrade) {
        this.upgrade = upgrade;
    }

}
