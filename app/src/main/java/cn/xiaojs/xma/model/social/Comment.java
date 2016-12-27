package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;

import cn.xiaojs.xma.model.Account;
import cn.xiaojs.xma.model.Doc;

/**
 * Created by maxiaobao on 2016/12/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    public String id;
    public String comment;
    public Doc doc;

    public String reply;

    public Account createdBy;
    public Date createdOn;
    public CommentContent body;
    public ArrayList<Comment> replies;



    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentContent {
        public String text;
    }

}
