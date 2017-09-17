package cn.xiaojs.xma.model.socket.room.whiteboard;

import android.graphics.Point;
import android.graphics.PointF;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.serialize.PointDataDeserializer;
import cn.xiaojs.xma.data.api.serialize.PointDataSerializer;

/**
 * Created by maxiaobao on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shape {

    @JsonDeserialize(using = PointDataDeserializer.class)
    @JsonSerialize(using = PointDataSerializer.class)
    public ArrayList<PointF> data;

    public String type;
    public float width;
    public float height;
    public float left;
    public float top;

}
