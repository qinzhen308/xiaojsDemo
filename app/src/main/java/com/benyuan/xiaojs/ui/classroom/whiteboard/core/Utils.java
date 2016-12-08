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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Beeline;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Triangle;

public class Utils {
    public static final int RECT_NO_SELECTED = 0;

    public static final int RECT_BODY = 100;

    public static final int LEFT_TOP_CORNER = 200;
    public static final int RIGHT_TOP_CORNER = 201;
    public static final int LEFT_BOTTOM_CORNER = 202;
    public static final int RIGHT_BOTTOM_CORNER = 203;

    public static final int TOP_EDGE = 300;
    public static final int BOTTOM_EDGE = 301;
    public static final int LEFT_EDGE = 302;
    public static final int RIGHT_EDGE = 303;

    private static final int OVAL_FOCUS_ON_X_AXLE = 1024;
    private static final int OVAL_FOCUS_ON_Y_AXLE = 2048;

    private final float MIN_SCALE = 1 / 2.0f;
    private final float MAX_SCALE = 4.0f;

    public final static String SHARED_NAME = "blackboard";
    public final static double ZERO = 1e-9;

    public final static int DEFAULT_COLOR = Color.BLACK;
    public final static float DEFAULT_STROKE_WIDTH = 20;
    public final static float DEFAULT_TEXT_SIZE = 60;

    private final static RectF mRect = new RectF();
    private final static Region mRegion = new Region();
    private final static Region mSelectorRegion = new Region();

    private static PointF mPoint = new PointF();
    private final static LineSegment[] mDiagonal = new LineSegment[2];

    private final static Matrix mMapMatrix = new Matrix();

    /**
     * map the point of specified canvas to screen point
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

    public static PointF normalizeScreenPoint(float x, float y, RectF doodleBounds) {
        x = x / doodleBounds.width();
        y = y / doodleBounds.height();
        //w,h scope in [0.0, 1.0]
        mPoint.set(x, y);
        return mPoint;
    }

    public static PointF reNormalizeScreenPoint(float x, float y, RectF doodleBounds) {
        x = x * doodleBounds.width();
        y = y * doodleBounds.height();
        mPoint.set(x, y);
        return mPoint;
    }

    public static void mapDoodleToScreen(Doodle doodle, PointF downPint, PointF upPoint) {
        WhiteBoard.BlackParams params = doodle.getWhiteboard().getBlackParams();
        PointF dp = doodle.getFirstPoint();
        PointF up = doodle.getLastPoint();

        mapDoodlePointToScreen(dp.x, dp.y, params.drawingBounds, downPint);
        mapDoodlePointToScreen(up.x, up.y, params.drawingBounds, upPoint);
    }

    public static boolean isMovable(float downX, float downY, float currX, float currY, int slope) {
        return Math.abs(currX - downX) > slope || Math.abs(currY - downY) > slope;
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

    public static Paint createPaint(int color, float strokeWidth, Paint.Style style) {
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(style);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth < 0 ? DEFAULT_STROKE_WIDTH : strokeWidth);
        paint.setColor(color);
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

    public static LineSegment getLineSegment(float stx, float sty, float edx, float edy, boolean original, RectF drawingBounds) {
        return getLineSegment(stx, sty, edx, edy, original, null);
    }

    public static LineSegment getLineSegment(float stx, float sty, float edx, float edy, boolean original,
                                      RectF drawingBounds, LineSegment lineSegment) {
        if (lineSegment == null) {
            lineSegment = new LineSegment();
        }
        if (original) {
            PointF p = Utils.mapDoodlePointToScreen(stx, sty, drawingBounds);
            int x1 = (int)p.x;
            int y1 = (int)p.y;

            p = Utils.mapDoodlePointToScreen(edx, edy, drawingBounds);
            int x2 = (int)p.x;
            int y2 = (int)p.y;

            lineSegment.point1.set(x1, y1);
            lineSegment.point2.set(x2, y2);
        } else {
            lineSegment.point1.set(stx, sty);
            lineSegment.point2.set(edx, edy);
        }

        return lineSegment;
    }

    /**
     * //A, B为一条线段两端点; C, D为另一条线段的两端点 若AB与CD相交返回true
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
     * 判断矩形是否和线段相交 1 若线段两端的任意一个点存在矩形内，返回true，否则执行第2步 2 若矩形的对角线和线段相交，返回true
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
                    if (Utils.intersect(diagonal, lineSegment)) {
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
     */
    public static double vectorProduct(PointF vectorA, PointF vectorB) {
        return vectorA.x * vectorB.y - vectorB.x * vectorA.y;
    }

    //如果两个向量点积的值大于0，他们的夹角是锐角，反之是钝角。
    private float dotProduct(float x, float y, PointF rectP1, PointF rectP2, float degree, RectF drawingBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, drawingBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, drawingBounds);
        float upx = p.x;
        float upy = p.y;

        float centerX = (dpx + upx) / 2.0f;
        float centerY = (dpy + upy) / 2.0f;

        //文本框水平中线向量 horizontalVector,因为是水平向量，y值为0
        float horizontalVector[] = {upx - centerX, 0};

        //映射到旋转前
        float[] pressedP = {x, y};
        mMapMatrix.reset();
        mMapMatrix.setRotate(-degree, centerX, centerY);
        mMapMatrix.mapPoints(pressedP);

        //文本框中心(起点)与手势按下点(终点)所成的向量
        float[] pressedVector = {pressedP[0] - centerX, pressedP[1] - centerY};

        //2个向量的点积
        return horizontalVector[0] * pressedVector[0] + horizontalVector[1] * pressedVector[1];
    }

    public static float calcRectRotation(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2, RectF drawingBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, drawingBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, drawingBounds);
        float upx = p.x;
        float upy = p.y;

        dpx = dpx - WhiteBoard.TEXT_BORDER_PADDING;
        dpy = dpy - WhiteBoard.TEXT_BORDER_PADDING;
        upx = upx + WhiteBoard.TEXT_BORDER_PADDING;
        upy = upy + WhiteBoard.TEXT_BORDER_PADDING;

        float centerX = (dpx + upx) / 2.0f;
        float centerY = (dpy + upy) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double previousAngle = Math.atan2(preDeltaY, preDeltaX);
        double angle = Math.atan2(deltaY, deltaX);

        double degrees = angle - previousAngle;
        return (float) (-degrees * (180 / Math.PI));
    }

    public static float calcRectScale(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2
                              , RectF drawingBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, drawingBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, drawingBounds);
        float upx = p.x;
        float upy = p.y;

        //dpx = dpx - WhiteBoard.TEXT_BORDER_PADDING;
        //dpy = dpy - WhiteBoard.TEXT_BORDER_PADDING;
        //upx = upx + WhiteBoard.TEXT_BORDER_PADDING;
        //upy = upy + WhiteBoard.TEXT_BORDER_PADDING;

        float centerX = (dpx + upx) / 2.0f;
        float centerY = (dpy + upy) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float deltaScale = (float)(distance / preDistance);

        return deltaScale;
    }

    public void calcRectRotationAndScale(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2, float degree,
                                         float totalScale, RectF drawingBounds){
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, drawingBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, drawingBounds);
        float upx = p.x;
        float upy = p.y;

        dpx = dpx - WhiteBoard.TEXT_BORDER_PADDING;
        dpy = dpy - WhiteBoard.TEXT_BORDER_PADDING;
        upx = upx + WhiteBoard.TEXT_BORDER_PADDING;
        upy = upy + WhiteBoard.TEXT_BORDER_PADDING;

        float centerX = (dpx + upx) / 2.0f;
        float centerY = (dpy + upy) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float tempScale = totalScale;
        float deltaScale = (float)(distance / preDistance);
        totalScale = totalScale * deltaScale;

        if (totalScale > MAX_SCALE) {
            totalScale = MAX_SCALE;
            deltaScale = totalScale / tempScale;
        }
        if (totalScale < MIN_SCALE) {
            totalScale = MIN_SCALE;
            deltaScale = totalScale / tempScale;
        }

        double previousAngle = Math.atan2(preDeltaY, preDeltaX);
        double angle = Math.atan2(deltaY, deltaX);

        double degrees = angle - previousAngle;
        degree = (float) (-degrees * (180 / Math.PI));
    }

    public static boolean intersect(float x, float y, Doodle d) {
        float x1 = x - WhiteBoard.PRESSED_SCOPE;
        float y1 = y - WhiteBoard.PRESSED_SCOPE;
        float x2 = x + WhiteBoard.PRESSED_SCOPE;
        float y2 = y + WhiteBoard.PRESSED_SCOPE;

        mRect.set(x1, y1, x2, y2);
        mSelectorRegion.set((int) x1, (int) y1, (int) x2, (int) y2);

        boolean intersect = false;
        if (d instanceof Beeline) {
            LineSegment beeLineSeg = ((Beeline) d).getLineSegment(true);
            intersect = Utils.intersect(mRect, beeLineSeg);
        } else if (d instanceof Triangle) {
            LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments(true);
            for (LineSegment lineSegment : beeLineSeg) {
                intersect = Utils.intersect(mRect, lineSegment);
                if (intersect) {
                    return true;
                }
            }
        } else {
            Path originalPath = d.getOriginalPath();
            if (originalPath == null) {
                return false;
            }

            mRegion.setEmpty();
            intersect = mRegion.setPath(originalPath, mSelectorRegion);
            mSelectorRegion.setEmpty();
        }

        return intersect;

    }

    /**
     * 判断某个点是否在矩形范围内
     * @param x 按下点的x
     * @param y 按下点的y
     * @param rectP1 矩形的其中的一个角对应的点
     * @param rectP2 相对于rectP1的对角点
     * @param doodleBounds
     * @return
     */
    public static int checkRectPressed(float x, float y, PointF rectP1, PointF rectP2, RectF doodleBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, doodleBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, doodleBounds);
        float upx = p.x;
        float upy = p.y;

        return checkRectPressed(x, y, dpx, dpy, upx, upy);
    }

    /**
     * 判断某个点是否在矩形范围内
     * @param x 按下点的x
     * @param y 按下点的y
     * @return
     */
    public static int checkRectPressed(float x, float y, float dpx, float dpy, float upx, float upy) {
        float x1 = Math.min(dpx, upx);
        float x2 = Math.max(dpx, upx);
        float y1 = Math.min(dpy, upy);
        float y2 = Math.max(dpy, upy);

        x1 = x1 - WhiteBoard.PRESSED_SCOPE;
        y1 = y1 - WhiteBoard.PRESSED_SCOPE;
        x2 = x2 + WhiteBoard.PRESSED_SCOPE;
        y2 = y2 + WhiteBoard.PRESSED_SCOPE;

        mRect.set(x1, y1, x2, y2);

        return mRect.contains(x, y) ? RECT_BODY : RECT_NO_SELECTED;
    }

    /**
     * 判断某个点是否按下矩形边框
     * @param x 按下点的x
     * @param y 按下点的y
     * @param rectP1 矩形的其中的一个角对应的点
     * @param rectP2 相对于rectP1的对角点
     * @return
     */
    public static boolean checkRectFramePressed(float x, float y, PointF rectP1, PointF rectP2, RectF doodleBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, doodleBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, doodleBounds);
        float upx = p.x;
        float upy = p.y;

        return checkRectFramePressed(x, y, dpx, dpy, upx, upy);
    }

    /**
     * 判断某个点是否按下矩形边框
     * @param x 按下点的x
     * @param y 按下点的y
     * @return
     */
    public static boolean checkRectFramePressed(float x, float y, float dpx, float dpy, float upx, float upy) {
        float minX = Math.min(dpx, upx);
        float maxX = Math.max(dpx, upx);
        float minY = Math.min(dpy, upy);
        float maxY = Math.max(dpy, upy);
        int scope = WhiteBoard.PRESSED_SCOPE;

        if (Math.abs(upx - dpx) < scope || Math.abs(upy - dpy) < scope) {
            if (y > (maxY + scope) || y < (minY - scope) ||
                    x > (maxX + scope) || x < (minX - scope)) {
                return false;
            }
        } else {
            if (y > (maxY + scope) || y < (minY - scope) ||
                    x > (maxX + scope) || x < (minX - scope) ||
                    (x > (minX + scope) && x < (maxX - scope) &&
                            y > (minY + scope) && y < (maxY - scope))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOvalFramePress(float x, float y, PointF rectP1, PointF rectP2, RectF doodleBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x ,rectP1.y, doodleBounds);
        float dpx = p.x;
        float dpy = p.y;
        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, doodleBounds);
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

        float outerOval_a = a + WhiteBoard.PRESSED_SCOPE;
        float outerOval_b = b + WhiteBoard.PRESSED_SCOPE;

        float innerOval_a = a - WhiteBoard.PRESSED_SCOPE;
        float innerOval_b = b - WhiteBoard.PRESSED_SCOPE;

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

    public static int isPressedCorner(float x, float y, PointF rectP1, PointF rectP2, RectF doodleBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x ,rectP1.y, doodleBounds);
        float dpx = p.x;
        float dpy = p.y;
        p = mapDoodlePointToScreen(rectP2.x ,rectP2.y, doodleBounds);
        float upx = p.x;
        float upy = p.y;

        return isPressedCorner(x, y, dpx, dpy, upx, upy);
    }

    public static int isPressedCorner(float x, float y, float rectP1x, float rectP1y, float rectP2x, float rectP2y) {
        int mWitchCornerPressed = Utils.RECT_NO_SELECTED;
        int scope = WhiteBoard.CORNER_EDGE_SIZE;

        float minX = Math.min(rectP1x, rectP2x) - scope / 2.0f;
        float maxX = Math.max(rectP1x, rectP2x) + scope / 2.0f;
        float minY = Math.min(rectP1y, rectP2y) - scope / 2.0f;
        float maxY = Math.max(rectP1y, rectP2y) + scope / 2.0f;

        float horizontalEdgeSize = scope;
        float verticalEdgeSize = scope;

        float shapeWidth = maxX - minX;
        float shapeHeight = maxY - minY;

        if (shapeWidth / 2.0f < scope) {
            horizontalEdgeSize = shapeWidth / 2.0f;
        }
        if (shapeHeight / 2.0f < scope) {
            verticalEdgeSize = shapeHeight / 2.0f;
        }

        if (x > minX && x < (minX + horizontalEdgeSize) && y > minY && y < (minY + verticalEdgeSize)) {
            mWitchCornerPressed = LEFT_TOP_CORNER;
        }

        if (x > (maxX - horizontalEdgeSize) && x < maxX && y > minY && y < (minY + verticalEdgeSize)) {
            mWitchCornerPressed = RIGHT_TOP_CORNER;
        }

        if (x > minX && x < (minX + horizontalEdgeSize) && y > (maxY - verticalEdgeSize) && y < maxY) {
            mWitchCornerPressed = LEFT_BOTTOM_CORNER;
        }

        if (x > (maxX - horizontalEdgeSize) && x < maxX && y > (maxY - verticalEdgeSize) && y < maxY) {
            mWitchCornerPressed = RIGHT_BOTTOM_CORNER;
        }

        return mWitchCornerPressed;
    }

    //线性形状按下事件范围
    public static boolean checkShapeLinePress(float x, float y, PointF doodleDownPoint, PointF doodleUpPoint) {
        float mLineDistanceY = doodleUpPoint.y - doodleDownPoint.y;
        float mLineDistanceX = doodleUpPoint.x - doodleDownPoint.x;

        float mLineLength = (float) Math.sqrt(mLineDistanceX * mLineDistanceX + mLineDistanceY * mLineDistanceY);
        double lineAngle = 0;

        float centerX = (doodleDownPoint.x + doodleUpPoint.x) / 2.0f;
        float centerY = (doodleDownPoint.y + doodleUpPoint.y) / 2.0f;
        double actionAngle = 0;

        try {
            //屏幕坐标系和标准坐标系的Y轴方向相反
            float lineK = -mLineDistanceY / mLineDistanceX;
            lineAngle = Math.atan(lineK);
        } catch (ArithmeticException e) {
            //除数为0异常
            lineAngle = Math.PI / 2;
        }

        try {
            float actionK = -(y - centerY) / (x - centerX);
            actionAngle = Math.atan(actionK);
        } catch (ArithmeticException e) {
            //除数为0异常
            actionAngle = Math.PI / 2;
        }

        double radius = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        double deltaAngle = actionAngle - lineAngle;

        double newX = centerX + radius * Math.cos(deltaAngle);
        double newY = centerY + radius * Math.sin(deltaAngle);

        float minX = centerX - mLineLength / 2.0f;
        float maxX = centerX + mLineLength / 2.0f;
        float minY = centerY - WhiteBoard.PRESSED_SCOPE;
        float maxY = centerY + WhiteBoard.PRESSED_SCOPE;

        if (newX > minX && newX < maxX && newY > minY && newY < maxY) {
            return true;
        }
        return false;
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
        a = a + WhiteBoard.PRESSED_SCOPE;
        b = b + WhiteBoard.PRESSED_SCOPE;

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

    public static boolean checkRectWithinPhotoDisplayArea(PointF doodleDownPoint, PointF doodleUpPoint, RectF mDisplayBounds) {

        float minX = Math.min(doodleDownPoint.x, doodleUpPoint.x);
        float maxX = Math.max(doodleDownPoint.x, doodleUpPoint.x);
        float minY = Math.min(doodleDownPoint.y, doodleUpPoint.y);
        float maxY = Math.max(doodleDownPoint.y, doodleUpPoint.y);

        if (maxX < mDisplayBounds.left ||
                minX > mDisplayBounds.right ||
                maxY < mDisplayBounds.top ||
                minY > mDisplayBounds.bottom) {
            //there are no points falls within the image display area
            return false;
        }
        return true;
    }

    public static int getShapeUpPointCorner(PointF doodleDownPoint, PointF doodleUpPoint) {
        int shapeUpPointCorner = -1;

        if (doodleUpPoint.x < doodleDownPoint.x && doodleUpPoint.y < doodleDownPoint.y) {
            shapeUpPointCorner = LEFT_TOP_CORNER;
        }
        if (doodleUpPoint.x > doodleDownPoint.x && doodleUpPoint.y < doodleDownPoint.y) {
            shapeUpPointCorner = RIGHT_TOP_CORNER;
        }
        if (doodleUpPoint.x < doodleDownPoint.x && doodleUpPoint.y > doodleDownPoint.y) {
            shapeUpPointCorner = LEFT_BOTTOM_CORNER;
        }
        if (doodleUpPoint.x > doodleDownPoint.x && doodleUpPoint.y > doodleDownPoint.y) {
            shapeUpPointCorner = RIGHT_BOTTOM_CORNER;
        }

        return shapeUpPointCorner;
    }


    public static void getDiagonalPoint(int pressedCorner, int shapeUpPointCorner,
                                        PointF doodleDownPoint, PointF doodleUpPoint, PointF diagonalPoint) {
        float shapeWidth = Math.abs(doodleUpPoint.x - doodleDownPoint.x);
        float shapeHeight = Math.abs(doodleUpPoint.y - doodleDownPoint.y);

        switch (pressedCorner) {
            case LEFT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                    case RIGHT_TOP_CORNER:
                        diagonalPoint.x = doodleDownPoint.x + shapeWidth;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y + shapeHeight;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleUpPoint.x;
                        diagonalPoint.y = doodleUpPoint.y;
                        break;
                }
                break;
            case RIGHT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        diagonalPoint.x = doodleDownPoint.x - shapeWidth;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                    case RIGHT_TOP_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleUpPoint.x;
                        diagonalPoint.y = doodleUpPoint.y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y + shapeHeight;
                        break;
                }
                break;
            case LEFT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y - shapeHeight;
                        break;
                    case RIGHT_TOP_CORNER:
                        diagonalPoint.x = doodleUpPoint.x;
                        diagonalPoint.y = doodleUpPoint.y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleDownPoint.x + shapeWidth;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                }
                break;
            case RIGHT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        diagonalPoint.x = doodleUpPoint.x;
                        diagonalPoint.y = doodleUpPoint.y;
                        break;
                    case RIGHT_TOP_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y - shapeHeight;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleDownPoint.x - shapeWidth;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        diagonalPoint.x = doodleDownPoint.x;
                        diagonalPoint.y = doodleDownPoint.y;
                        break;
                }
                break;
        }
    }

    public static void changeLineDownUpPoint(float x, float y, int pressedCorner, int shapeUpPointCorner,
                                             PointF doodleDownPoint, PointF doodleUpPoint, PointF diagonalPoint) {
        switch (pressedCorner) {
            case LEFT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                }
                break;
            case RIGHT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                }
                break;
            case LEFT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                }
                break;
            case RIGHT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                }
                break;
        }
    }

    public static void changeLineDownUpPoint(int pressedCorner, int shapeUpPointCorner, float x, float y,
                                             PointF doodleDownPoint, PointF doodleUpPoint, PointF diagonalPoint) {
        switch (pressedCorner) {
            case LEFT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                }
                break;
            case RIGHT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                }
                break;
            case LEFT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                }
                break;
            case RIGHT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = y;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = diagonalPoint.x;
                        doodleDownPoint.y = y;
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = diagonalPoint.y;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = x;
                        doodleDownPoint.y = diagonalPoint.y;
                        doodleUpPoint.x = diagonalPoint.x;
                        doodleUpPoint.y = y;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleUpPoint.x = x;
                        doodleUpPoint.y = y;
                        break;
                }
                break;
        }
    }

    public static void changeRectDownPoint(int pressedCorner, int shapeUpPointCorner, PointF doodleDownPoint, PointF doodleUpPoint) {
        float shapeWidth = Math.abs(doodleUpPoint.x - doodleDownPoint.x);
        float shapeHeight = Math.abs(doodleUpPoint.y - doodleDownPoint.y);
        switch (pressedCorner) {
            case LEFT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x + shapeWidth;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.y = doodleDownPoint.y + shapeHeight;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x + shapeWidth;
                        doodleDownPoint.y = doodleDownPoint.y + shapeHeight;
                        break;
                }
                break;
            case RIGHT_TOP_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x - shapeWidth;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x - shapeWidth;
                        doodleDownPoint.y = doodleDownPoint.y + shapeHeight;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.y = doodleDownPoint.y + shapeHeight;
                        break;
                }
                break;
            case LEFT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.y = doodleDownPoint.y - shapeHeight;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x + shapeWidth;
                        doodleDownPoint.y = doodleDownPoint.y - shapeHeight;
                        break;
                    case RIGHT_BOTTOM_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x + shapeWidth;
                        break;
                }
                break;
            case RIGHT_BOTTOM_CORNER:
                switch (shapeUpPointCorner) {
                    case LEFT_TOP_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x - shapeWidth;
                        doodleDownPoint.y = doodleDownPoint.y - shapeHeight;
                        break;
                    case RIGHT_TOP_CORNER:
                        doodleDownPoint.y = doodleDownPoint.y - shapeHeight;
                        break;
                    case LEFT_BOTTOM_CORNER:
                        doodleDownPoint.x = doodleDownPoint.x - shapeWidth;
                        break;
                }
                break;
        }
    }

}
