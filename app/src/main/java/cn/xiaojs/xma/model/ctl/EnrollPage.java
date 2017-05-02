package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by maxiaobao on 2017/5/2.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollPage {

    public int total;
    public int page;
    public int numOfPages;
    public List<StudentInfo> students;

}
