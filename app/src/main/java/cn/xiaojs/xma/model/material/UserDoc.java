package cn.xiaojs.xma.model.material;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by maxiaobao on 2017/2/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDoc {

    public String libId;
    public String id;
    public long used;
    public long quota;
    public List<LibDoc> documents;
}
