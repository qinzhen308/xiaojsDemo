package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Statistic implements Serializable {

    public int visited;
    public int shared;
    public int favorites;

}
