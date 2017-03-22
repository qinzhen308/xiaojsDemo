package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by maxiaobao on 2017/3/22.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyStatus {

    public String state;
    public Date requestedOn;
    public Date enteredOn;
    public VerifyBasic basic;
    public VerifyIdentity identity;
    public String reason;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VerifyBasic{
        public String name;
        public int type;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VerifyIdentity{
        public String no;
        public int type;
    }
}
