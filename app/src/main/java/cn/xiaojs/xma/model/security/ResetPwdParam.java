package cn.xiaojs.xma.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.common.xf_foundation.schemas.Security;
import cn.xiaojs.xma.model.account.PwdParam;

/**
 * Created by maxiaobao on 2017/3/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPwdParam extends PwdParam{

    public long mobile;
    public int code;
    public int ct = Security.CredentialType.PERSION;
}
