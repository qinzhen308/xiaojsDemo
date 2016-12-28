package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class FollowParam {

    public String contact;
    public int group;

}
