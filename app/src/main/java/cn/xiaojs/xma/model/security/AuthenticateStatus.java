package cn.xiaojs.xma.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by maxiaobao on 2017/1/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticateStatus {
    public boolean auth;
    public String csrf;
    public String sessionID;
}
