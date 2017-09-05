package cn.xiaojs.xma.model.socket.room.whiteboard;

import android.graphics.Point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.serialize.PointDataDeserializer;

/**
 * Created by maxiaobao on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shape {

    @JsonDeserialize(using = PointDataDeserializer.class)
    public ArrayList<Point> data;

    public String type;
    public int width;
    public int height;
    public int left;
    public int top;

}
