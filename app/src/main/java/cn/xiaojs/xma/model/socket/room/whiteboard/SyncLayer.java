package cn.xiaojs.xma.model.socket.room.whiteboard;

import android.graphics.PointF;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
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
    public String owner;
    public float angle;

    public PointF moveDot;
    public PointF pos1;
    public PointF pos2;
    public PointF pos3;
    public PointF pos4;
    public PointF selDot1;
    public PointF selDot2;
    public PointF selDot3;
    public PointF selDot4;
    public PointF rotateDot;
    public Bounds bounds;
    public float width;
    public float height;
    public float left;
    public float top;

    public boolean visible=true;
    public boolean grouped=false;
    public int xa= Platform.AppType.MOBILE_ANDROID;


    @JsonDeserialize(using = InfoDeserializer.class)
    public SyncInfo info;

    public static class Bounds{
        PointF rigthTop;
        PointF rightBottom;
        PointF leftBottom;
        PointF leftTop;
        public float left;
        public float top;
        public float bottom;
        public float right;
    }

    public void complement(){
        bounds=new SyncLayer.Bounds();
        width=shape.width;
        height=shape.height;
        bounds.left=left=shape.left;
        bounds.top=top=shape.top;
        bounds.right=left+width;
        bounds.bottom=top+height;
        bounds.leftTop=new PointF(bounds.left,bounds.top);
        bounds.rigthTop=new PointF(bounds.right,bounds.top);
        bounds.leftBottom=new PointF(bounds.left,bounds.bottom);
        bounds.rightBottom=new PointF(bounds.right,bounds.bottom);
        moveDot=new PointF(left+width/2,top+height/2);
        pos1=new PointF(left - moveDot.x, top - moveDot.y);
        pos2=new PointF(moveDot.x - left, top - moveDot.y);
        pos3=new PointF(left - moveDot.x, moveDot.y - top);
        pos4=new PointF(moveDot.x - left, moveDot.y - top);
        int innerWidth=20;
        selDot1   = new PointF(pos1.x - innerWidth, pos1.y - innerWidth);
        selDot2   = new PointF(pos2.x + innerWidth, pos2.y - innerWidth);
        selDot3   = new PointF(pos3.x - innerWidth, pos3.y + innerWidth);
        selDot4   = new PointF(pos4.x + innerWidth, pos4.y + innerWidth);
        rotateDot = new PointF((selDot1.x + selDot2.x) / 2, pos1.y - 3 * innerWidth);

    }
}
