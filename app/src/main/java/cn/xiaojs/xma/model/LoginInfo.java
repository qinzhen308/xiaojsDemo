package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/10/31.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
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
