package cn.xiaojs.xma.model.socket.room.whiteboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.serialize.InfoDeserializer;

/**
 * Created by maxiaobao on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncData {
    public String id;
    public SyncLayer layer;
    public ArrayList<SyncLayer> changedLayers;
    public Command command;

    @JsonProperty("shape")
    public Shape paste_shape;

    @JsonProperty("lineWidth")
    public int paste_lineWidth;

    @JsonProperty("lineColor")
    public String paste_lineColor;

    @JsonDeserialize(using = InfoDeserializer.class)
    @JsonProperty("info")
    public SyncInfo paste_info;
}
