package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2017/1/17.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attendee implements Serializable{

    public String psType;
    public int xa;
    public String xav;
    public String accountId;
    public String name;
    public String avatar;
    public MediaSetting avc;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MediaSetting implements Serializable{
        public VideoConfig video;
        public AudioConfig audio;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoConfig implements  Serializable{
        public boolean supported;
        public boolean enabled;
        public boolean masked;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AudioConfig implements Serializable {
        public boolean supported;
        public boolean enabled;
        public boolean muted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attendee attendee = (Attendee) o;

        return accountId.equals(attendee.accountId);

    }

    @Override
    public int hashCode() {
        return accountId.hashCode();
    }
}
