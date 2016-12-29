package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/12/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {

    public String name;
    public String avatar;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    /////////////////////////////////////////////////////////////////

    public String account;
    public String unread;
    public String lastMessage;
    public long group;
    public String alias;
    public String subtype;
    public int followType;
    public String id;
    public String subject;
    public String title;
    public String state;
    public String startedOn;

}
