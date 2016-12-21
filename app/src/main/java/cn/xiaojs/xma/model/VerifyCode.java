package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/11/1.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyCode extends APIEntity {

    // The date and time on which the verification message was sent.
    private long sentOn;

    // The mobile number to address.
    private long to;

    // The verification method.
    private int method;

    private long expiresOn;

    // (DEV-ONLY): The verification code sent.
    private int code;


    public long getSentOn() {
        return sentOn;
    }

    public void setSentOn(long sentOn) {
        this.sentOn = sentOn;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(long expiresOn) {
        this.expiresOn = expiresOn;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
