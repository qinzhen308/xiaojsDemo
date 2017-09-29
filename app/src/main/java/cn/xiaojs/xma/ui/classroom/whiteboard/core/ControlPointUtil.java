package cn.xiaojs.xma.ui.classroom.whiteboard.core;

import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by Paul Z on 2017/9/29.
 * 定义本类里面的方法返回的rect都基于（0,0,screenW,screenH）
 * 注：实际屏幕rect还要进行一次mDisplaymatrix的变换
 */
public class ControlPointUtil {

    public static RectF getRotatePoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF(border);
        matrix.mapRect(dest);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(dest.right-radius,dest.top-radius,dest.right+radius,dest.top+radius);
        return dest;
    }


    public static RectF getDeletePoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF(border);
        matrix.mapRect(dest);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(dest.left-radius,dest.bottom-radius,dest.left+radius,dest.bottom+radius);
        return dest;
    }


    public static RectF getLeftPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF(border);
        matrix.mapRect(dest);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(dest.left-radius,dest.centerY()-radius,dest.left+radius,dest.centerY()+radius);
        return dest;
    }

    public static RectF getTopPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF(border);
        matrix.mapRect(dest);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(dest.centerX()-radius,dest.top-radius,dest.centerX()+radius,dest.top+radius);
        return dest;
    }

    public static RectF getRightPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF(border);
        matrix.mapRect(dest);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(dest.right-radius,dest.centerY()-radius,dest.right+radius,dest.centerY()+radius);
        return dest;
    }

    public static RectF getBottomPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF(border);
        matrix.mapRect(dest);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(dest.centerX()-radius,dest.bottom-radius,dest.centerX()+radius,dest.bottom+radius);
        return dest;
    }


}
