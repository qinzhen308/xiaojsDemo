package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/1/17.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attendee {

    public String psType;
    public int xa;
    public String xav;
    public String accountId;
    public String name;
    public String avatar;
    public MediaSetting avc;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MediaSetting {
        public VideoConfig video;
        public AudioConfig audio;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoConfig{
        public boolean supported;
        public boolean enabled;
        public boolean masked;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AudioConfig {
        public boolean supported;
        public boolean enabled;
        public boolean muted;
    }

}
