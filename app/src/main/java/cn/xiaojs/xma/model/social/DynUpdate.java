package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import cn.xiaojs.xma.model.Account;
import cn.xiaojs.xma.model.Doc;

/**
 * Created by maxiaobao on 2016/12/27.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynUpdate {

    public String tips;
    public String typeName;
    public Account.SimpleAccount behavedBy;
    public UpdateInfo body;
    public Date createdOn;
    public Doc source;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateInfo {

        public String summary;
        public UpdateRef ref;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateRef {

        public Account.SimpleAccount account;
        public String snap;
        public Dimension dimension;
        public String title;
        public String text;
        public Doc doc;

    }
}
