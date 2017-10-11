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
        RectF dest=new RectF();
        float[] p=new float[]{border.right,border.top};
        matrix.mapPoints(p);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(p[0]-radius,p[1]-radius,p[0]+radius,p[1]+radius);
        return dest;
    }


    public static RectF getDeletePoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF();
        float[] p=new float[]{border.left,border.bottom};
        matrix.mapPoints(p);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(p[0]-radius,p[1]-radius,p[0]+radius,p[1]+radius);
        return dest;
    }


    public static RectF getLeftPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF();
        float[] p=new float[]{border.left,border.centerY()};
        matrix.mapPoints(p);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(p[0]-radius,p[1]-radius,p[0]+radius,p[1]+radius);
        return dest;
    }

    public static RectF getTopPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF();
        float[] p=new float[]{border.centerX(),border.top};
        matrix.mapPoints(p);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(p[0]-radius,p[1]-radius,p[0]+radius,p[1]+radius);
        return dest;
    }

    public static RectF getRightPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF();
        float[] p=new float[]{border.right,border.centerY()};
        matrix.mapPoints(p);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(p[0]-radius,p[1]-radius,p[0]+radius,p[1]+radius);
        return dest;
    }

    public static RectF getBottomPoint(RectF border, Matrix matrix,float scale){
        RectF dest=new RectF();
        float[] p=new float[]{border.centerX(),border.bottom};
        matrix.mapPoints(p);
        float radius=WhiteboardConfigs.CONTROLLER_RADIUS/scale;
        dest.set(p[0]-radius,p[1]-radius,p[0]+radius,p[1]+radius);
        return dest;
    }


}
