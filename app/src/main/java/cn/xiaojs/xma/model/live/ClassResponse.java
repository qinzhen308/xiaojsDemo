package cn.xiaojs.xma.model.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

/**
 * Created by maxiaobao on 2017/2/10.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassResponse extends ResponseBody{
    public String publishUrl;
    public String playbackUrl;

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public BufferedSource source() {
        return null;
    }
}
