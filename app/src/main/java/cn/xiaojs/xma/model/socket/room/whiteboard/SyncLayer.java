package cn.xiaojs.xma.model.socket.room.whiteboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.data.api.serialize.InfoDeserializer;

/**
 * Created by maxiaobao on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncLayer {
    public String id;
    public Shape shape;
    public int lineWidth;
    public String lineColor;

    @JsonDeserialize(using = InfoDeserializer.class)
    public SyncInfo info;
}
