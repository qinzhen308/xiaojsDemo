package cn.xiaojs.xma.ui.classroom.socketio;
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
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

public class ProtocolConfigs {
    /**
     * 白板虚拟宽
     */
    public final static int VIRTUAL_WIDTH = 800;
    /**
     * 白板虚拟高
     */
    public final static int VIRTUAL_HEIGHT = 600;

    //=======================constants for commend======================
    /**
     * 创建一个图形
     */
    public final static String NEW = "N";
    /**
     * 删除图形
     */
    public final static String DEL = "D";
    /**
     * 移动（绝对值）
     */
    public final static String MOVE_A = "M";
    /**
     * 移动（相对值）
     */
    public final static String MOVE_R = "m";
    /**
     * 缩放（缩放至宽高：绝对缩放）
     */
    public final static String ZOOM_A = "Z";
    /**
     * 缩放（缩放为原来的%n：等比缩放）
     */
    public final static String ZOOM_R = "z";
    /**
     * 调整高度至
     */
    public final static String ADJUST = "A";
    /**
     * 旋转(绝对)
     */
    public final static String ROTATE_A = "R";
    /**
     * 旋转(相对)
     */
    public final static String ROTATE_R = "r";
    /**
     * 改变画笔(全局画布的颜色和宽度)
     */
    public final static String PAINT = "P";
    /**
     * 修改图形的颜色
     */
    public final static String CHANGE_COLOR = "C";


    //=======================constants for shape=========================
    /**
     * 矩形
     */
    public final static String SHAPE_RECT = "rect";
    /**
     * 圆，椭圆
     */
    public final static String SHAPE_CIRCLE = "circ";
    /**
     * 三角形
     */
    public final static String SHAPE_TRIANGLE = "trng";
    /**
     * 直线
     */
    public final static String SHAPE_BEELINE = "line";

}
