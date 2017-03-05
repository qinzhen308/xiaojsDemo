package cn.xiaojs.xma.model.material;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/2/6.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LibCapacity {
    public String quota;
    public String used;

    /**
     * Created by maxiaobao on 2017/2/6.
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LibAssociate {
        public String id;
        public String subtype;
    }
}
