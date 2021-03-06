package cn.xiaojs.xma.model.socket.room.whiteboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

import cn.xiaojs.xma.data.api.serialize.BoardLayerSetDeserializer;

/**
 * Created by Paul Z on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Drawing implements Serializable{

    public int width;
    public int height;
    public String name;

    @JsonDeserialize(using = BoardLayerSetDeserializer.class)
    public BoardLayerSet stylus;

}
