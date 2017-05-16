package cn.xiaojs.xma.model.material;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/5/8.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConflictRes {

    public String id;
    public String name;
    public String typeName;

    @Override
    public String toString() {
        //此处重写，是因为弹出批量分享资料库冲突框，用ArrayAdapter展示文件名字用的。
        return name;
    }
}
