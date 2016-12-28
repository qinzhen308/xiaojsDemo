package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

import cn.xiaojs.xma.model.Account;
import cn.xiaojs.xma.model.Doc;

/**
 * Created by maxiaobao on 2016/12/27.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DynUpdate {

    public String tips;
    public String typeName;
    public Account.SimpleAccount behavedBy;
    public UpdateInfo body;
    public Date createdOn;
    public Doc source;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateInfo {

        public String summary;
        public UpdateRef ref;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateRef {

        public Account.SimpleAccount account;
        public String snap;
        public Dimension dimension;
        public String title;
        public Doc doc;

    }
}
