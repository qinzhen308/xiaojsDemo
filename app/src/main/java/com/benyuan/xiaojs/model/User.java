package com.benyuan.xiaojs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2016/10/31.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String sessionID;
    private Account account;
    private String name;
    private AliasTags aliasAndTags;

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
