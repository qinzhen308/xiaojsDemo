package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import java.io.Serializable;

import cn.xiaojs.xma.model.AliasTags;

/**
 * Created by maxiaobao on 2016/10/31.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable{
    private String id;
    private String sessionID;
    private Account account;
    private String name;
    private AliasTags aliasAndTags;
    public boolean hasClass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AliasTags getAliasAndTags() {
        return aliasAndTags;
    }

    public void setAliasAndTags(AliasTags aliasAndTags) {
        this.aliasAndTags = aliasAndTags;
    }

}
