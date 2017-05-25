package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/5/25.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassEnroll {


    public StudentEnroll[] students;

    @JsonProperty("import")
    public EnrollImport[] importe;


}
