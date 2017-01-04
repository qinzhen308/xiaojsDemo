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

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Beeline;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Triangle;

import java.util.ArrayList;

public class IntersectionHelper {
    /**
     * 矩形没有被选中
     */
    public static final int RECT_NO_SELECTED = 0;
    /**
     * 矩形body
     */
    public static final int RECT_BODY = 100;
    /**
     * 矩形的角
     */
    public static final int LEFT_TOP_CORNER = 200;
    public static final int RIGHT_TOP_CORNER = 201;
    public static final int LEFT_BOTTOM_CORNER = 202;
    public static final int RIGHT_BOTTOM_CORNER = 203;
    /**
     * 矩形的边
     */
    public static final int TOP_EDGE = 300;
    public static final int RIGHT_EDGE = 301;
    public static final int BOTTOM_EDGE = 302;
    public static final int LEFT_EDGE = 303;

    private static final int OVAL_FOCUS_ON_X_AXLE = 1024;
    private static final int OVAL_FOCUS_ON_Y_AXLE = 2048;

    private final static RectF mTransRect = new RectF();
    private final static Region mRegion = new Region();
    private final static Region mSelectorRegion = new Region();

    /**
     * @param x         按下点的x
     * @param y         按下点的y
     * @param rectP1    矩形的其中的一个角对应的点
     * @param rectP2    相对于rectP1的对角点
     * @param mapMatrix 映射矩阵
     */
    public static int checkRectPressed(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        RectF transRect = Utils.transformToScreenRect(rectP1, rectP2, mapMatrix);
        int scope = WhiteboardConfigs.PRESSED_SCOPE;
        transRect.set(transRect.left - scope, transRect.top - scope, transRect.right + scope, transRect.bottom + scope);
        return transRect.contains(x, y) ? RECT_BODY : RECT_NO_SELECTED;
    }

    /**
     * @param x         按下点的x
     * @param y         按下点的y
     * @param rect      变换前的矩形
     * @param mapMatrix 映射矩阵
     */
    public static int checkRectPressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = Utils.transformToScreenRect(rect, mapMatrix);
        int scope = WhiteboardConfigs.PRESSED_SCOPE;

        transRect.set(transRect.left - scope, transRect.top - scope, transRect.right + scope, transRect.bottom + scope);
        return transRect.contains(x, y) ? RECT_BODY : RECT_NO_SELECTED;
    }

    /**
     * 判断某个点是否按下矩形边框
     *
     * @param x      按下点的x
     * @param y      按下点的y
     * @param rectP1 矩形的其中的一个角对应的点
     * @param rectP2 相对于rectP1的对角点
     */
    public static boolean isRectFramePressed(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        RectF rect = Utils.transformToScreenRect(rectP1, rectP2, mapMatrix);
        int edge = whichEdgePressed(x, y, rect.left, rect.top, rect.right, rect.bottom);
        return edge != RECT_NO_SELECTED;
    }

    public static boolean isRectFramePressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = Utils.transformToScreenRect(rect, mapMatrix);
        int edge = whichEdgePressed(x, y, transRect.left, transRect.top, transRect.right, transRect.bottom);
        return edge != RECT_NO_SELECTED;
    }

    public static int whichEdgePressed(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        RectF rect = Utils.transformToScreenRect(rectP1, rectP2, mapMatrix);
        return whichEdgePressed(x, y, rect.left, rect.top, rect.right, rect.bottom);
    }

    public static int whichEdgePressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = Utils.transformToScreenRect(rect, mapMatrix);
        return whichEdgePressed(x, y, transRect.left, transRect.top, transRect.right, transRect.bottom);
    }

    /**
     * 判断是按下矩形的哪一条边
     *
     * @param x 按下点的x
     * @param y 按下点的y
     */
    public static int whichEdgePressed(float x, float y, float left, float top, float right, float bottom) {
        int scope = WhiteboardConfigs.PRESSED_SCOPE + WhiteboardConfigs.BORDER_PADDING;
        //top edge
        mTransRect.set(left, top - scope, right, top + scope);
        if (mTransRect.contains(x, y)) {
            return TOP_EDGE;
        }

        //bottom edge
        mTransRect.set(left, bottom - scope, right, bottom + scope);
        if (mTransRect.contains(x, y)) {
            return BOTTOM_EDGE;
        }

        //left edge
        mTransRect.set(left - scope, top, left + scope, bottom);
        if (mTransRect.contains(x, y)) {
            return LEFT_EDGE;
        }

        //right edge
        mTransRect.set(right - scope, top, right + scope, bottom);
        if (mTransRect.contains(x, y)) {
            return RIGHT_EDGE;
        }

        return RECT_NO_SELECTED;
    }


    /**
     * 判断按下的某个点是否在rectP1，rectP1构成矩形的某个角上
     *
     * @param x 按下点x
     * @param y 按下点y
     */
    public static int whichCornerPressed(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        RectF transRect = Utils.transformToScreenRect(rectP1, rectP2, mapMatrix);
        return whichCornerPressed(x, y, transRect);
    }

    /**
     * 判断按下的某个点是否在矩形的某个角上
     *
     * @param x 按下点x
     * @param y 按下点y
     */
    public static int whichCornerPressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = Utils.transformToScreenRect(rect, mapMatrix);
        return whichCornerPressed(x, y, transRect);
    }

    /**
     * 判断哪个矩形的哪个角被按下了
     */
    private static int whichCornerPressed(float x, float y, RectF rect) {
        float scope = WhiteboardConfigs.CORNER_EDGE_SIZE;
        float offset = scope * 0.6f;
        //left top corner
        Utils.buildRect(mTransRect, rect.left, rect.top, scope, -offset, -offset);
        if (mTransRect.contains(x, y)) {
            return LEFT_TOP_CORNER;
        }

        //right top corner
        Utils.buildRect(mTransRect, rect.right, rect.top, scope, offset, -offset);
        if (mTransRect.contains(x, y)) {
            return RIGHT_TOP_CORNER;
        }

        //left bottom corner
        Utils.buildRect(mTransRect, rect.left, rect.bottom, scope, -offset, offset);
        if (mTransRect.contains(x, y)) {
            return LEFT_BOTTOM_CORNER;
        }

        //right bottom corner
        Utils.buildRect(mTransRect, rect.right, rect.bottom, scope, offset, offset);
        if (mTransRect.contains(x, y)) {
            return RIGHT_BOTTOM_CORNER;
        }

        return RECT_NO_SELECTED;
    }

    public static boolean checkOvalFramePress(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        PointF p = Utils.transformToScreenPoint(rectP1.x, rectP1.y, mapMatrix);
        float dpx = p.x;
        float dpy = p.y;
        p = Utils.transformToScreenPoint(rectP2.x, rectP2.y, mapMatrix);
        float upx = p.x;
        float upy = p.y;

        //transform the device coordinate to mathematical coordinate
        float centerOffsetX = (dpx + upx) / 2.0f;
        float centerOffsetY = (dpy + upy) / 2.0f;
        x = x - centerOffsetX;
        y = (centerOffsetY - y);

        dpx = dpx - centerOffsetX;
        dpy = (centerOffsetY - dpy);
        upx = upx - centerOffsetX;
        upy = (centerOffsetY - upy);

        return checkOvalFramePress(x, y, dpx, dpy, upx, upy);
    }

    public static boolean checkOvalFramePress(float x, float y, RectF rect, Matrix mapMatrix) {
        PointF p = Utils.transformToScreenPoint(rect.left, rect.top, mapMatrix);
        float dpx = p.x;
        float dpy = p.y;
        p = Utils.transformToScreenPoint(rect.right, rect.bottom, mapMatrix);
        float upx = p.x;
        float upy = p.y;

        //transform the device coordinate to mathematical coordinate
        float centerOffsetX = (dpx + upx) / 2.0f;
        float centerOffsetY = (dpy + upy) / 2.0f;
        x = x - centerOffsetX;
        y = (centerOffsetY - y);

        dpx = dpx - centerOffsetX;
        dpy = (centerOffsetY - dpy);
        upx = upx - centerOffsetX;
        upy = (centerOffsetY - upy);

        return checkOvalFramePress(x, y, dpx, dpy, upx, upy);
    }

    /**
     * 判断某个点是否在以rect产生的椭圆（椭圆的圆心在0,0）
     */
    public static boolean checkOvalFramePress(float x, float y, float rectP1x, float rectP1y, float rectP2x, float rectP2y) {
        //oval equations:
        /**
         * focus on the x axle:
         * x^2 / a^2 + y^2 / b^2 = 1
         *
         * focus on the y axle:
         * y^2 / a^2 + x^2 / b^2 = 1
         */
        float a = 1.0f;
        float b = 1.0f;
        int focus = Math.abs(rectP2x - rectP1x) > Math.abs(rectP2y - rectP1y) ? OVAL_FOCUS_ON_X_AXLE : OVAL_FOCUS_ON_Y_AXLE;
        a = (focus == OVAL_FOCUS_ON_X_AXLE) ? Math.abs(rectP2x - rectP1x) / 2.0f : Math.abs(rectP2y - rectP1y) / 2.0f;
        b = (focus == OVAL_FOCUS_ON_X_AXLE) ? Math.abs(rectP2y - rectP1y) / 2.0f : Math.abs(rectP2x - rectP1x) / 2.0f;

        int scope = WhiteboardConfigs.PRESSED_SCOPE;
        float outerOval_a = a + scope;
        float outerOval_b = b + scope;

        float innerOval_a = a - scope;
        float innerOval_b = b - scope;

        float innerOvalLocusY = 0.0f;
        float outerOvalLocusY = 0.0f;

        float tempInnerOvalLocusY = 0;
        float tempOuterOvalLocusY = 0;

        if (innerOval_a <= 0 || innerOval_b <= 0) {
            //only checks the point is out of outer oval
            switch (focus) {
                case OVAL_FOCUS_ON_X_AXLE:
                    tempOuterOvalLocusY = (1 - (x * x) / (outerOval_a * outerOval_a)) * (outerOval_b * outerOval_b);
                    outerOvalLocusY = (float) Math.sqrt(tempOuterOvalLocusY);
                    break;
                case OVAL_FOCUS_ON_Y_AXLE:
                    tempOuterOvalLocusY = (1 - (x * x) / (outerOval_b * outerOval_b)) * (outerOval_a * outerOval_a);
                    outerOvalLocusY = (float) Math.sqrt(tempOuterOvalLocusY);
                    break;
            }
            if (tempOuterOvalLocusY < 0 || y > outerOvalLocusY || y < -outerOvalLocusY) {
                //the point is outside the outer oval
                return false;
            }
        } else {
            //checks the point is out of outer oval or within inner oval
            switch (focus) {
                case OVAL_FOCUS_ON_X_AXLE:
                    tempInnerOvalLocusY = (1 - (x * x) / (innerOval_a * innerOval_a)) * (innerOval_b * innerOval_b);
                    tempOuterOvalLocusY = (1 - (x * x) / (outerOval_a * outerOval_a)) * (outerOval_b * outerOval_b);
                    innerOvalLocusY = (float) Math.sqrt(tempInnerOvalLocusY);
                    outerOvalLocusY = (float) Math.sqrt(tempOuterOvalLocusY);
                    break;
                case OVAL_FOCUS_ON_Y_AXLE:
                    tempInnerOvalLocusY = (1 - (x * x) / (innerOval_b * innerOval_b)) * (innerOval_a * innerOval_a);
                    tempOuterOvalLocusY = (1 - (x * x) / (outerOval_b * outerOval_b)) * (outerOval_a * outerOval_a);
                    innerOvalLocusY = (float) Math.sqrt(tempInnerOvalLocusY);
                    outerOvalLocusY = (float) Math.sqrt(tempOuterOvalLocusY);
                    break;
            }

            if (tempOuterOvalLocusY < 0 || y > outerOvalLocusY || y < -outerOvalLocusY) {
                //the point is outside the outer oval
                return false;
            }
            if (tempInnerOvalLocusY < 0) {
                return true;
            } else if (y >= -innerOvalLocusY && y <= innerOvalLocusY) {
                //the point within inner oval
                return false;
            }
        }
        return true;
    }

    public static boolean checkShapeOvalPress(float x, float y, PointF doodleDownPoint, PointF doodleUpPoint) {
        float centerOffsetX = (doodleDownPoint.x + doodleUpPoint.x) / 2.0f;
        float centerOffsetY = (doodleDownPoint.y + doodleUpPoint.y) / 2.0f;

        //transform the device coordinate to mathematical coordinate
        x = x - centerOffsetX;
        y = (centerOffsetY - y);
        float downPointX = doodleDownPoint.x - centerOffsetX;
        float downPointY = (centerOffsetY - doodleUpPoint.y);
        float upPointX = doodleDownPoint.x - centerOffsetX;
        float upPointY = (centerOffsetY - doodleUpPoint.y);

        int focus = Math.abs(upPointX - downPointX) > Math.abs(upPointY - downPointY) ? OVAL_FOCUS_ON_X_AXLE : OVAL_FOCUS_ON_Y_AXLE;
        float a = (focus == OVAL_FOCUS_ON_X_AXLE) ? Math.abs(upPointX - downPointX) / 2.0f : Math.abs(upPointY - downPointY) / 2.0f;
        float b = (focus == OVAL_FOCUS_ON_X_AXLE) ? Math.abs(upPointY - downPointY) / 2.0f : Math.abs(upPointX - downPointX) / 2.0f;
        a = a + WhiteboardConfigs.PRESSED_SCOPE;
        b = b + WhiteboardConfigs.PRESSED_SCOPE;

        float tempOuterOvalLocusY = 0;
        float outerOvalLocusY = 0.0f;
        //only check point is outside the oval
        switch (focus) {
            case OVAL_FOCUS_ON_X_AXLE:
                tempOuterOvalLocusY = (1 - (x * x) / (a * a)) * (b * b);
                outerOvalLocusY = (float) Math.sqrt(tempOuterOvalLocusY);
                break;
            case OVAL_FOCUS_ON_Y_AXLE:
                tempOuterOvalLocusY = (1 - (x * x) / (b * b)) * (a * a);
                outerOvalLocusY = (float) Math.sqrt(tempOuterOvalLocusY);
                break;
        }
        if (tempOuterOvalLocusY < 0 || y > outerOvalLocusY || y < -outerOvalLocusY) {
            //the point is outside the oval
            return false;
        }

        return true;
    }

    public static boolean intersect(float x, float y, Doodle d) {
        int scope = WhiteboardConfigs.PRESSED_SCOPE;
        Utils.buildRect(mTransRect, x, y, scope);
        return intersect(x, y, d, mTransRect);
    }

    public static boolean intersect(float x, float y, Doodle d, RectF transRect) {
        if (d == null || !d.isShow()) {
            return false;
        }

        mSelectorRegion.set((int) transRect.left, (int) transRect.top, (int) transRect.right, (int) transRect.bottom);

        boolean intersect = false;
        if (d instanceof Beeline) {
            LineSegment beeLineSeg = ((Beeline) d).getLineSegment();
            intersect = Utils.intersect(transRect, beeLineSeg);
        } else if (d instanceof Triangle) {
            LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments();
            for (LineSegment lineSegment : beeLineSeg) {
                intersect = Utils.intersect(transRect, lineSegment);
            }
        } else {
            Path originalPath = d.getScreenPath();
            if (originalPath == null) {
                return false;
            }

            mRegion.setEmpty();
            intersect = mRegion.setPath(originalPath, mSelectorRegion);
            mSelectorRegion.setEmpty();
        }

        if (intersect) {
            d.setState(Doodle.STATE_EDIT);
        }
        return intersect;
    }

    public static int intersect(ArrayList<Doodle> allDoodles, RectF transRect) {
        mSelectorRegion.set((int) transRect.left, (int) transRect.top, (int) transRect.right, (int) transRect.bottom);

        boolean intersect = false;
        int intersectCount = 0;
        if (allDoodles != null) {
            for (int i = allDoodles.size() - 1; i >= 0; i--) {
                Doodle d = allDoodles.get(i);
                if (d == null || !d.isShow()) {
                   continue;
                }

                if (d instanceof Beeline) {
                    LineSegment beeLineSeg = ((Beeline) d).getLineSegment();
                    intersect = Utils.intersect(transRect, beeLineSeg);
                    if (intersect) {
                        intersectCount++;
                        d.setState(Doodle.STATE_EDIT);
                    }
                } else if (d instanceof Triangle) {
                    LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments();
                    for (LineSegment lineSegment : beeLineSeg) {
                        intersect = Utils.intersect(transRect, lineSegment);
                        if (intersect) {
                            intersectCount++;
                            d.setState(Doodle.STATE_EDIT);
                            break;
                        }
                    }
                } else {
                    Path originalPath = d.getScreenPath();
                    if (originalPath == null) {
                        continue;
                    }

                    intersect = mRegion.setPath(originalPath, mSelectorRegion);
                    if (intersect) {
                        intersectCount++;
                        d.setState(Doodle.STATE_EDIT);
                    }
                }
            }
        }

        return intersectCount;
    }
}
