package cn.xiaojs.xma.model.socket.room.whiteboard;

import android.graphics.RectF;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Paul Z on 2017/9/5.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Viewport implements Serializable{
    public int width;
    public int height;


    public RectF buildRect(){
        return new RectF(0,0,width,height);
    }

}
