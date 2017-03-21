package cn.xiaojs.xma.model.contents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/3/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {

    public String title;
    public Date createdOn;
    public String id;
    public ArticleBody body;

}
