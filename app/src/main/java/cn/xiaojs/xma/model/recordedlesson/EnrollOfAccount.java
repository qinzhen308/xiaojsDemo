package cn.xiaojs.xma.model.recordedlesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Paul Z on 2017/7/29.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollOfAccount implements Serializable{
    public Date date;
    public Date deadline;
    public boolean isExpired;
}
