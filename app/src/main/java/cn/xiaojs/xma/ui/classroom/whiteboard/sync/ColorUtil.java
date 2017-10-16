package cn.xiaojs.xma.ui.classroom.whiteboard.sync;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/14.
 */

public class ColorUtil {
    @ColorInt public static final int BLACK       = 0xFF000000;
    @ColorInt public static final int DKGRAY      = 0xFF444444;
    @ColorInt public static final int GRAY        = 0xFF888888;
    @ColorInt public static final int LTGRAY      = 0xFFCCCCCC;
    @ColorInt public static final int WHITE       = 0xFFFFFFFF;
    @ColorInt public static final int RED         = 0xFFFF0000;
    @ColorInt public static final int GREEN       = 0xFF00FF00;
    @ColorInt public static final int BLUE        = 0xFF0000FF;
    @ColorInt public static final int YELLOW      = 0xFFFFFF00;
    @ColorInt public static final int CYAN        = 0xFF00FFFF;
    @ColorInt public static final int MAGENTA     = 0xFFFF00FF;
    @ColorInt public static final int TRANSPARENT = 0;
    private static final HashMap<String, Integer> sColorNameMap;
    static {
        sColorNameMap = new HashMap<String, Integer>();
        sColorNameMap.put("black", BLACK);
        sColorNameMap.put("darkgray", DKGRAY);
        sColorNameMap.put("gray", GRAY);
        sColorNameMap.put("lightgray", LTGRAY);
        sColorNameMap.put("white", WHITE);
        sColorNameMap.put("red", RED);
        sColorNameMap.put("green", GREEN);
        sColorNameMap.put("blue", BLUE);
        sColorNameMap.put("yellow", YELLOW);
        sColorNameMap.put("cyan", CYAN);
        sColorNameMap.put("magenta", MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", DKGRAY);
        sColorNameMap.put("grey", GRAY);
        sColorNameMap.put("lightgrey", LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);

    }


    /**
     *  换取颜色名字
     * @param color 颜色值
     * @return 颜色名字：black,white....
     */
    public static String getColorName(int color){

        Iterator<Map.Entry<String,Integer>> data=sColorNameMap.entrySet().iterator();
        while (data.hasNext()){
            Map.Entry<String,Integer> entry=data.next();
            if(entry.getValue()==color){
                return entry.getKey();
            }
        }
        return "black";
    }

    public static int parseColor(String colorStr){
        try {
            return Color.parseColor(colorStr);
        }catch (Exception e){
            Logger.d(e);
            return Color.BLACK;
        }
    }
}
