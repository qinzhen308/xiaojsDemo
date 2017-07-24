package cn.xiaojs.xma.model.recordedlesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.Duration;

/**
 * Created by Paul Z on 2017/7/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordedLessonCriteria {

    public String role;
    public String title;
    public String state;


}
