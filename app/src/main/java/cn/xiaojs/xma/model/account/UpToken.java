package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpToken {

    public String type;
    public String token;
    public String key;

}
