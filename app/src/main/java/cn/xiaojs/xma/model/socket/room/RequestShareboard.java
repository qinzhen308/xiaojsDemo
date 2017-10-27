package cn.xiaojs.xma.model.socket.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/9/4.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestShareboard {
    public String board;
    public Integer page;//可选
    public String preview;//预览图  base64编码(可选)

}
