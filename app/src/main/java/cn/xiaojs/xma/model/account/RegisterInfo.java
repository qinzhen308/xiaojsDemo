package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.common.xf_foundation.schemas.Security;

/**
 * Created by maxiaobao on 2016/10/25.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterInfo{

    public long mobile;
    public String password;
    public String username;
    public int code;
    public int ct = Security.CredentialType.PERSION;

}
