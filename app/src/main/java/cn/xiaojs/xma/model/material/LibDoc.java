package cn.xiaojs.xma.model.material;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by maxiaobao on 2017/2/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"showAction"})
public class LibDoc implements Serializable {
    public String id;
    public LibCapacity.LibAssociate associated;
    public DocExport exported;
    public long used;
    public String mimeType;
    public String typeName;

    public Date createdOn;
    public Date uploadedOn;
    //    public duration
    public String state;
    public String name;
    public String key;

    public DocOwner owner;
    //ignore field
    public boolean showAction;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocExport implements Serializable{
        public ExportImg[] images;
        public int total;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true, value = {"index"})
    public static class ExportImg implements Serializable{
        public String name;
        //ignore field, for sort
        public int index;
    }

}
