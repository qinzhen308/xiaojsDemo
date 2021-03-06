package cn.xiaojs.xma.model.recordedlesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2017/7/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expire implements Serializable{
    public int mode;
    public int effective;
}
