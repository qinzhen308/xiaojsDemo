package cn.xiaojs.xma.ui.classroom.whiteboard.core;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/12/18
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import cn.xiaojs.xma.R;

public class WhiteboardConfigs {

    public static final float WHITE_BOARD_MAX_SCALE = 5.0f;
    public static final float WHITE_BOARD_MIN_SCALE = 1.0f;

    public static final float DEFAULT_PAINT_STROKE_WIDTH = 10.0f;

    public static int DEFAULT_PAINT_COLOR = Color.BLACK;

    public static int BORDER_PADDING = 10;
    public static int BORDER_STROKE_WIDTH = 5;
    public static int SELECTOR_STROKE_WIDTH = 5;
    public static int BORDER_DASH_WIDTH = 20;
    public static int SELECTOR_DASH_WIDTH = 5;
    public static int CONTROLLER_RADIUS = 50;
    public static int DEL_BTN_STROKE_WIDTH = 8;
    public static int BORDER_COLOR = 0XFF0076FF;

    public static int PRESSED_SCOPE = 20;
    public static int CORNER_EDGE_SIZE = 60;
    public static int TOUCH_SLOPE = 20;
    public static int TEXT_BORDER_PADDING  =20;

    public static float PAINT_STROKE_WIDTH = 20;
    public static float DEFAULT_TEXT_SIZE = 60;

    public static int MIN_EDIT_TEXT_WIDTH = 80;
    public static int MIN_EDIT_TEXT_HEIGHT = 60;


    public static void init(Context context) {
        Resources rs = context.getResources();

        TOUCH_SLOPE = rs.getDimensionPixelOffset(R.dimen.px20);
        PRESSED_SCOPE = rs.getDimensionPixelOffset(R.dimen.px20);
        CORNER_EDGE_SIZE = rs.getDimensionPixelOffset(R.dimen.px30);
        TEXT_BORDER_PADDING = rs.getDimensionPixelOffset(R.dimen.px12);

        BORDER_COLOR = rs.getColor(R.color.green_grass);
        DEFAULT_PAINT_COLOR = rs.getColor(R.color.wb_color_1);
        BORDER_STROKE_WIDTH = rs.getDimensionPixelOffset(R.dimen.px4);
        BORDER_DASH_WIDTH = rs.getDimensionPixelOffset(R.dimen.px10);
        BORDER_PADDING = rs.getDimensionPixelOffset(R.dimen.px10);
        SELECTOR_DASH_WIDTH = rs.getDimensionPixelOffset(R.dimen.px5);
        SELECTOR_STROKE_WIDTH = rs.getDimensionPixelOffset(R.dimen.px2);
        CONTROLLER_RADIUS = rs.getDimensionPixelOffset(R.dimen.px20);
        DEL_BTN_STROKE_WIDTH = rs.getDimensionPixelOffset(R.dimen.px8);

        PAINT_STROKE_WIDTH = rs.getDimensionPixelOffset(R.dimen.px20);

        DEFAULT_TEXT_SIZE = rs.getDimensionPixelSize(R.dimen.px60);
        MIN_EDIT_TEXT_WIDTH = rs.getDimensionPixelOffset(R.dimen.px80);
        MIN_EDIT_TEXT_HEIGHT = rs.getDimensionPixelOffset(R.dimen.px60);
    }
}
