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
 * Date:2017/1/4
 * Desc:
 *
 * ======================================================================================== */

public interface Transformation {
    /**
     * 修改图形(拖动图形某一条边修改图形)
     *
     * @param oldX   上一次按下的x坐标
     * @param oldY   上一次按下的y坐标
     * @param x      当前按下的x坐标
     * @param y      当前按下的y坐标
     * @param byEdge 按下的图形的哪条边
     */
    public abstract void changeByEdge(float oldX, float oldY, float x, float y, int byEdge);

    /**
     * 修改图形(拖动图形某一条边修改图形)
     *
     * @param deltaX 变化量x
     * @param deltaY 变化量y
     * @param byEdge 按下图形的哪条边
     */
    public abstract void changeByEdge(float deltaX, float deltaY, int byEdge);


    /**
     * 图形移动
     *
     * @param oldX 上一次按下的x坐标
     * @param oldY 上一次按下的y坐标
     * @param x    当前按下的x坐标
     * @param y    当前按下的y坐标
     */
    public abstract void move(float oldX, float oldY, float x, float y);

    /**
     * 图形移动
     *
     * @param deltaX x轴的偏移量
     * @param deltaY y轴的偏移量
     */
    public abstract void move(float deltaX, float deltaY);

    /**
     * 图形缩放(等比缩放，中心缩放)
     *
     * @param oldX 上一次按下的x坐标
     * @param oldY 上一次按下的y坐标
     * @param x    当前按下的x坐标
     * @param y    当前后按下的y坐标
     */
    public abstract float scale(float oldX, float oldY, float x, float y);

    /**
     * 图形缩放(等比缩放，中心缩放)
     *
     * @param scale 缩放值
     */
    public abstract void scale(float scale);

    /**
     * 图形旋转
     *
     * @param oldX 上一次按下的x坐标
     * @param oldY 上一次按下的y坐标
     * @param x    当前按下的x坐标
     * @param y    当前后按下的y坐标
     */
    public abstract float rotate(float oldX, float oldY, float x, float y);

    /**
     * 图形旋转
     *
     * @param degree 旋转的角度
     */
    public abstract void rotate(float degree);

    /**
     * 图形缩放和旋转
     *
     * @param oldX 上一次按下的x坐标
     * @param oldY 上一次按下的y坐标
     * @param x    当前按下的x坐标
     * @param y    当前后按下的y坐标
     * @return 返回缩放值float[0]和旋转角度float[1]
     */
    public abstract float[] scaleAndRotate(float oldX, float oldY, float x, float y);

    /**
     * 图形缩放和旋转
     *
     * @param scale  缩放值
     * @param degree 旋转的角度
     */
    public abstract void scaleAndRotate(float scale, float degree);
}
