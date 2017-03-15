package cn.xiaojs.xma.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.model.account.User;

/**
 * Created by maxiaobao on 2016/10/31.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginInfo extends APIEntity {

    public User user;
    public Upgrade upgrade;
    public HashMap<Long, String> contactGroups;

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
