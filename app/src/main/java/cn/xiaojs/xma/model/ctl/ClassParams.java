package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by maxiaobao on 2017/5/25.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassParams {

    public String title;
    public String[] advisers;
    public int join;
    public boolean accessible;
    public ClassEnroll enroll;
    public List<ClassLesson> lessons;

}
