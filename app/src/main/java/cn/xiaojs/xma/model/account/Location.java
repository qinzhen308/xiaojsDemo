package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/3/7.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    public double lon;
    public double lat;
    public int adCode;

    @Override
    public String toString() {
        return "Location lon:" + lon + ", lat:" + lat + ", adCode:" + adCode;
    }
}
