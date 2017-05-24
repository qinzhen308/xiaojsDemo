package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.common.xf_foundation.schemas.Security;

/**
 * Created by maxiaobao on 2017/5/24.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialRegisterInfo {

    public long mobile;
    public String avatar;
    public String nickname;
    public String sex;
    public int code;
}
