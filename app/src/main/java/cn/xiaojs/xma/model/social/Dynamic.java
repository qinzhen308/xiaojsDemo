package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

import cn.xiaojs.xma.model.Doc;

/**
 * Created by maxiaobao on 2016/12/26.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dynamic {

    public String id;
    public Date createdOn;
    public String typeName;
    public boolean liked;
    public DynOwner owner;
    public DynBody body;
    public DynStatus stats;



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DynOwner{
        public String account;
        public String alias;
        public String tag;
        public boolean followed;
        public String title;
        public String id;
        public String subtype;
        public Date startedOn;
        public String state;
        public boolean myself;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DynBody{
        public DynPhoto[] drawings;
        public String text;
        public DynRef ref;

    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DynPhoto extends Dimension{
        public String name;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DynRef {
        public String snap;
        public String title;
        public boolean overview;
        public Date startedOn;
        public boolean free;
        public int duration;
        public Doc doc;
        public String state;
    }


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DynStatus implements Serializable{
        public int liked;
        public int comments;
        public int forwarded;
        public int shared;
    }


}
