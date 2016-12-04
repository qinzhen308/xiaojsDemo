package com.benyuan.xiaojs.ui.classroom.whiteboard.core;
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
 * Date:2016/10/19
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;

public class Utils {
    public final static String SHARED_NAME = "blackboard";
    public final static double ZERO = 1e-9;

    public final static int DEFAULT_COLOR = Color.BLACK;
    public final static float DEFAULT_STROKE_WIDTH = 20;
    public final static float DEFAULT_TEXT_SIZE = 60;

    private final static int TOUCH_SLOPE = 10;
    private static PointF mPoint = new PointF();
    private final static LineSegment[] mDiagonal = new LineSegment[2];

    /**
     * map the point of specified canvas to screen point
     * @param x
     * @param y
     * @param doodleBounds
     * @param dst
     */
    public static void mapDoodlePointToScreen(float x, float y, RectF doodleBounds, PointF dst) {
        x = x * doodleBounds.width();
        y = y * doodleBounds.height();
        x = x + doodleBounds.left;
        y = y + doodleBounds.top;
        dst.set(x, y);
    }

    public static PointF mapDoodlePointToScreen(float x, float y, RectF doodleBounds) {
        x = x * doodleBounds.width();
        y = y * doodleBounds.height();
        x = x + doodleBounds.left;
        y = y + doodleBounds.top;
        mPoint.set(x, y);
        return mPoint;
    }

    /**
     * Normalize the point of screen to specified canvas which scope in [0.0, 1.0]
     */
    public static void mapScreenToDoodlePoint(float x, float y, RectF doodleBounds, PointF dst) {
        x = x - doodleBounds.left;
        y = y - doodleBounds.top;
        x = x / doodleBounds.width();
        y = y / doodleBounds.height();
        //x,y scope in [0.0, 1.0]
        dst.set(x, y);
    }

    public static PointF mapScreenToDoodlePoint(float x, float y, RectF doodleBounds) {
        x = x - doodleBounds.left;
        y = y - doodleBounds.top;
        x = x / doodleBounds.width();
        y = y / doodleBounds.height();
        //x,y scope in [0.0, 1.0]
        mPoint.set(x, y);
        return mPoint;
    }

    public static PointF normalizeScreenPoint(float w, float h, RectF doodleBounds) {
        w = w / doodleBounds.width();
        h = h / doodleBounds.height();
        //w,h scope in [0.0, 1.0]
        mPoint.set(w, h);
        return mPoint;
    }

    public static void mapDoodleToScreen(Doodle doodle, PointF downPint, PointF upPoint) {
        WhiteBoard.BlackParams params = doodle.getWhiteboard().getBlackParams();
        PointF dp = doodle.getDownPoint();
        PointF up = doodle.getUpPoint();

        mapDoodlePointToScreen(dp.x, dp.y, params.drawingBounds, downPint);
        mapDoodlePointToScreen(up.x, up.y, params.drawingBounds, upPoint);
    }

    public static boolean isMovable(float downX, float downY, float currX, float currY) {
        return Math.abs(currX - downX) > TOUCH_SLOPE || Math.abs(currY - downY) > TOUCH_SLOPE;
    }

    /**
     * Creates hand writing paint for blackboard.
     */
    public static Paint createHandWritingPaint() {
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        return paint;
    }

    /**
     * Creates text writing paint for blackboard.
     */
    public static Paint createTextWritingPaint() {
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(DEFAULT_TEXT_SIZE);
        return paint;
    }

    public static float dpToPixel(Context context, float dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.density * dp;
    }

    /**
     * Returns the input value x clamped to the range [min, max].
     * @param x
     * @param min
     * @param max
     * @return
     */
    public static int clamp(int x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    public static float clamp(float x, float min, float max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    public static long clamp(long x, long min, long max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    /**
     * 获取矩形的对角线
     * @param rectF
     * @return
     */
    public static LineSegment[] getDiagonal(RectF rectF) {
        if (mDiagonal[0] == null) {
            mDiagonal[0] = new LineSegment();
        }

        if (mDiagonal[1] == null) {
            mDiagonal[1] = new LineSegment();
        }

        mDiagonal[0].point1.x = rectF.left;
        mDiagonal[0].point1.y = rectF.top;
        mDiagonal[0].point2.x = rectF.right;
        mDiagonal[0].point2.y = rectF.bottom;

        mDiagonal[1].point1.x = rectF.left;
        mDiagonal[1].point1.y = rectF.bottom;
        mDiagonal[1].point2.x = rectF.right;
        mDiagonal[1].point2.y = rectF.top;

        return mDiagonal;
    }

    /**
     * //A, B为一条线段两端点; C, D为另一条线段的两端点 若AB与CD相交返回true
     * @param lineSegment1
     * @param lineSegment2
     * @return
     */
    public static boolean intersect(LineSegment lineSegment1, LineSegment lineSegment2) {
        PointF A = lineSegment1.point1;
        PointF B = lineSegment1.point2;
        PointF C = lineSegment2.point1;
        PointF D = lineSegment2.point2;

        //AC = Vector(A, C)
        //AD = Vector(A, D)
        //BC = Vector(B, C)
        //BD = Vector(B, D)
        //return (vector_product(AC, AD) * vector_product(BC, BD) <= ZERO)
        //and (vector_product(AC, BC) * vector_product(AD, BD) <= ZERO)

        //TODO to be optimized ：clam down new object
        PointF vectorAC = new PointF(C.x - A.x, C.y - A.y);
        PointF vectorAD = new PointF(D.x - A.x, D.y - A.y);
        PointF vectorBC = new PointF(C.x - B.x, C.y - B.y);
        PointF vectorBD = new PointF(D.x - B.x, D.y - B.y);

        return (vectorProduct(vectorAC, vectorAD) * vectorProduct(vectorBC, vectorBD) <= ZERO)
                && (vectorProduct(vectorAC, vectorBC) * vectorProduct(vectorAD, vectorBD) <= ZERO);

    }

    /**
     * 判断矩形是否和线段相交
     *  1 若线段两端的任意一个点存在矩形内，返回true，否则执行第2步
     *  2 若矩形的对角线和线段相交，返回true
     * @param rectF
     * @param lineSegment
     * @return
     */
    public static boolean intersect(RectF rectF, LineSegment lineSegment) {
        boolean intersect = false;

        if (rectF.contains(lineSegment.point1.x, lineSegment.point1.y) ||
                rectF.contains(lineSegment.point2.x, lineSegment.point2.y)) {
            //exist a point in rect
            intersect = true;
        } else {
            LineSegment[] rectDiagonals = Utils.getDiagonal(rectF);
            if (rectDiagonals != null) {
                for (LineSegment diagonal : rectDiagonals) {
                    if(Utils.intersect(diagonal, lineSegment)) {
                        intersect = true;
                        break;
                    }
                }
            }
        }

        return intersect;
    }

    /**
     * 两个向量叉乘
     * @param vectorA
     * @param vectorB
     * @return
     */
    public static double vectorProduct(PointF vectorA, PointF vectorB) {
        return vectorA.x * vectorB.y - vectorB.x * vectorA.y;
    }

}
