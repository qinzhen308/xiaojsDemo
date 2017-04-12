package cn.xiaojs.xma.model.material;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/2/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibDoc {
    public String id;
    public LibCapacity.LibAssociate associated;
    public DocExport exported;
    public long used;
    public String mimeType;
    public Date uploadedOn;
//    public duration
    public String state;
    public String name;
    public String key;

    public DocOwner owner;




    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocExport{
        public ExportImg[] images;
        public int total;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExportImg{
        public String name;
    }

}
