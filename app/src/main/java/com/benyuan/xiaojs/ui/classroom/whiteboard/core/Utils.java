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
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Beeline;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.TextWriting;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Triangle;

public class Utils {
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

    private final float MIN_SCALE = 1 / 2.0f;
    private final float MAX_SCALE = 4.0f;

    public final static String SHARED_NAME = "blackboard";
    public final static double ZERO = 1e-9;

    public final static int DEFAULT_COLOR = Color.BLACK;
    public final static float DEFAULT_STROKE_WIDTH = 20;
    public final static float DEFAULT_TEXT_SIZE = 60;

    public static int DEFAULT_BORDER_WIDTH = 5;
    public static int DEFAULT_DASH_WIDTH = 20;
    public static int DEFAULT_CONTROLLER_RADIUS = 50;
    private static int DEFAULT_BORDER_COLOR = 0XFF0076FF;

    private final static RectF mRect = new RectF();
    private final static RectF mTransRect = new RectF();
    private final static Region mRegion = new Region();
    private final static Region mSelectorRegion = new Region();

    private static PointF mPoint = new PointF();
    private final static LineSegment[] mDiagonal = new LineSegment[2];

    private final static Matrix mMapRectMatrix = new Matrix();
    private final static Matrix mMapPointMatrix = new Matrix();
    private final static float[] mPointArr = new float[2];

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
    public static Paint createPaint(int color, float strokeWidth, Paint.Style style) {
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(style);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth < 0 ? DEFAULT_STROKE_WIDTH : strokeWidth);
        paint.setColor(color);
        return paint;
    }

    public static Paint buildDashPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(DEFAULT_BORDER_WIDTH);
        p.setColor(DEFAULT_BORDER_COLOR);
        PathEffect blackEffects = new DashPathEffect(new float[]{DEFAULT_DASH_WIDTH, DEFAULT_DASH_WIDTH}, 0);
        p.setPathEffect(blackEffects);
        return p;
    }

    public static Paint buildControllerPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(DEFAULT_CONTROLLER_RADIUS);
        p.setColor(Color.RED);
        return p;
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

    public static LineSegment getLineSegment(PointF p1, PointF p2, Matrix mapMatrix) {
        return getLineSegment(p1, p2, mapMatrix, null);
    }

    /**
     * 根据2个点获取线段
     */
    public static LineSegment getLineSegment(PointF p1, PointF p2,
                                             Matrix mapMatrix, LineSegment lineSegment) {
        if (lineSegment == null) {
            lineSegment = new LineSegment();
        }

        PointF p = transformToScreenPoint(p1, mapMatrix);
        lineSegment.point1.set(p.x, p.y);
        p = transformToScreenPoint(p2, mapMatrix);
        lineSegment.point2.set(p.x, p.y);

        return lineSegment;
    }

    /**
     * 根据2个点的坐标获取线段
     */
    public static LineSegment getLineSegment(float p1x, float p1y, float p2x, float p2y,
                                             Matrix mapMatrix, LineSegment lineSegment) {
        if (lineSegment == null) {
            lineSegment = new LineSegment();
        }

        PointF p = transformToScreenPoint(p1x, p1y, mapMatrix);
        lineSegment.point1.set(p.x, p.y);
        p = transformToScreenPoint(p2x, p2y, mapMatrix);
        lineSegment.point2.set(p.x, p.y);

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

    public static boolean intersect(float x, float y, Doodle d) {
        int scope = WhiteBoard.PRESSED_SCOPE;
        buildRect(mRect, x, y, scope);

        mSelectorRegion.set((int) mRect.left, (int) mRect.top, (int) mRect.right, (int) mRect.bottom);

        boolean intersect = false;
        if (d instanceof Beeline) {
            LineSegment beeLineSeg = ((Beeline) d).getLineSegment();
            intersect = Utils.intersect(mRect, beeLineSeg);
        } else if (d instanceof Triangle) {
            LineSegment[] beeLineSeg = ((Triangle) d).getLineSegments();
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

    public static Matrix transformScreenMatrix(Matrix drawingMatrix, Matrix displayMatrix) {
        mMapRectMatrix.reset();
        mMapRectMatrix.postConcat(drawingMatrix);
        mMapRectMatrix.postConcat(displayMatrix);
        return mMapRectMatrix;
    }


    /**
     * 计算变换矩阵，该矩阵用来变换图形所在区域矩形
     * 事件相交取值范围:
     *      RECT_BODY 按下矩形体
     *
     *      LEFT_TOP_CORNER 矩形左上角
     *      RIGHT_TOP_CORNER 矩形右上角
     *      LEFT_BOTTOM_CORNER 矩形左下角
     *      RIGHT_BOTTOM_CORNER 矩形右下角
     *
     *      TOP_EDGE 矩形上条边
     *      RIGHT_EDGE 矩形右条边
     *      BOTTOM_EDGE 矩形底条边
     *      LEFT_EDGE 矩形左条边
     *
     * @param drawingMatrix
     * @param displayMatrix
     * @param center
     * @param degrees
     * @return
     */
    public static Matrix transformMatrix(Matrix drawingMatrix, Matrix displayMatrix, float[] center, float degrees) {
        mMapRectMatrix.reset();
        if (drawingMatrix != null) {
            mMapRectMatrix.postConcat(drawingMatrix);
        }
        if (displayMatrix != null) {
            mMapRectMatrix.postConcat(displayMatrix);
        }

        if (degrees != 0) {
            //映射到旋转前, 把矩形置为水平，便于判断点是否和矩阵是否有相交事件
            mMapRectMatrix.postRotate(-degrees, center[0], center[1]);
        }

        return mMapRectMatrix;
    }

    /**
     * 为了判断某个点是否在矩形内，需要把矩形置为水平，并且把点知映射到旋转前
     * @param x
     * @param y
     * @param center
     * @param degrees
     * @return
     */
    public static PointF transformPoint(float x, float y, float[] center, float degrees) {
        if (degrees == 0) {
            mPoint.set(x, y);
            return mPoint;
        }

        mMapPointMatrix.reset();
        //映射到旋转前, 把点置为初始状态
        mMapPointMatrix.postRotate(-degrees, center[0], center[1]);
        mPointArr[0] = x;
        mPointArr[1] = y;
        mMapPointMatrix.mapPoints(mPointArr);
        mPoint.set(mPointArr[0], mPointArr[1]);

        return mPoint;
    }

    /**
     * @param x             按下点的x
     * @param y             按下点的y
     * @param rectP1        矩形的其中的一个角对应的点
     * @param rectP2        相对于rectP1的对角点
     * @param mapMatrix     映射矩阵
     */
    public static int checkRectPressed(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        RectF rect = transformToScreenRect(rectP1, rectP2, mapMatrix);
        int scope = WhiteBoard.PRESSED_SCOPE;
        rect.set(rect.left - scope, rect.top - scope, rect.right + scope, rect.bottom + scope);
        return rect.contains(x, y) ? RECT_BODY : RECT_NO_SELECTED;
    }

    /**
     * @param x             按下点的x
     * @param y             按下点的y
     * @param rect          变换前的矩形
     * @param mapMatrix     映射矩阵
     */
    public static int checkRectPressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        int scope = WhiteBoard.PRESSED_SCOPE;

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
        RectF rect = transformToScreenRect(rectP1, rectP2, mapMatrix);
        int edge = whichEdgePressed(x, y, rect.left, rect.top, rect.right, rect.bottom);
        return edge != RECT_NO_SELECTED;
    }

    public static boolean isRectFramePressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        int edge = whichEdgePressed(x, y, transRect.left, transRect.top, transRect.right, transRect.bottom);
        return edge != RECT_NO_SELECTED;
    }

    public static int whichEdgePressed(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        RectF rect = transformToScreenRect(rectP1, rectP2, mapMatrix);
        return whichEdgePressed(x, y, rect.left, rect.top, rect.right, rect.bottom);
    }

    public static int whichEdgePressed(float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        return whichEdgePressed(x, y, transRect.left, transRect.top, transRect.right, transRect.bottom);
    }

    /**
     * 判断是按下矩形的哪一条边
     *
     * @param x 按下点的x
     * @param y 按下点的y
     */
    public static int whichEdgePressed(float x, float y, float left, float top, float right, float bottom) {
        int scope = WhiteBoard.PRESSED_SCOPE;
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
    public static int isPressedCorner(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        transformToScreenRect(rectP1, rectP2, mapMatrix);
        return whichCornerPressed(x, y, mRect);
    }

    /**
     * 判断按下的某个点是否在矩形的某个角上
     *
     * @param x 按下点x
     * @param y 按下点y
     */
    public static int isPressedCorner(float x, float y, RectF rect, Matrix mapMatrix) {
        transformToScreenRect(rect, mapMatrix);
        return whichCornerPressed(x, y, mRect);
    }

    public static PointF transformToScreenPoint(PointF p, Matrix mapMatrix) {
        return transformToScreenPoint(p.x, p.y, mapMatrix);
    }

    public static PointF transformToScreenPoint(float x, float y, Matrix mapMatrix) {
        if (mapMatrix == null) {
            mPoint.set(x, y);
            return mPoint;
        }

        mPointArr[0] = x;
        mPointArr[1] = y;
        mapMatrix.mapPoints(mPointArr);
        mPoint.set(mPointArr[0], mPointArr[1]);
        return mPoint;
    }

    /**
     * 变换doodle坐标到屏幕坐标
     */
    public static RectF transformToScreenRect(PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        float minX = Math.min(rectP1.x, rectP2.x);
        float minY = Math.min(rectP1.y, rectP2.y);
        float maxX = Math.max(rectP1.x, rectP2.x);
        float maxY = Math.max(rectP1.y, rectP2.y);
        mRect.set(minX, minY, maxX, maxY);

        if (mapMatrix == null) {
            return mRect;
        }

        mPointArr[0] = mRect.left;
        mPointArr[1] = mRect.top;
        mapMatrix.mapPoints(mPointArr);
        float left = mPointArr[0];
        float top = mPointArr[1];

        mPointArr[0] = mRect.right;
        mPointArr[1] = mRect.bottom;
        mapMatrix.mapPoints(mPointArr);
        float right = mPointArr[0];
        float bottom = mPointArr[1];
        mRect.set(left, top, right, bottom);

        return mRect;
    }

    /**
     * 变换doodle rect对应的坐标到屏幕坐标
     */
    public static RectF transformToScreenRect(RectF rect, Matrix mapMatrix) {
        mRect.set(rect);

        if (mapMatrix == null) {
            return mRect;
        }

        mPointArr[0] = mRect.left;
        mPointArr[1] = mRect.top;
        mapMatrix.mapPoints(mPointArr);
        float left = mPointArr[0];
        float top = mPointArr[1];

        mPointArr[0] = mRect.right;
        mPointArr[1] = mRect.bottom;
        mapMatrix.mapPoints(mPointArr);
        float right = mPointArr[0];
        float bottom = mPointArr[1];
        mRect.set(left, top, right, bottom);

        return mRect;
    }

    /**
     * 判断哪个矩形的哪个角被按下了
     */
    private static int whichCornerPressed(float x, float y, RectF rect) {
        float scope = WhiteBoard.CORNER_EDGE_SIZE;
        float offset = scope * 0.6f;
        //left top corner
        buildRect(mTransRect, rect.left, rect.top, scope, -offset, -offset);
        if (mTransRect.contains(x, y)) {
            return LEFT_TOP_CORNER;
        }

        //right top corner
        buildRect(mTransRect, rect.right, rect.top, scope, offset, -offset);
        if (mTransRect.contains(x, y)) {
            return RIGHT_TOP_CORNER;
        }

        //left bottom corner
        buildRect(mTransRect, rect.left, rect.bottom, scope, -offset, offset);
        if (mTransRect.contains(x, y)) {
            return LEFT_BOTTOM_CORNER;
        }

        //right bottom corner
        buildRect(mTransRect, rect.right, rect.bottom, scope, offset, offset);
        if (mTransRect.contains(x, y)) {
            return RIGHT_BOTTOM_CORNER;
        }

        return RECT_NO_SELECTED;
    }

    /**
     * 通过某一个点和scope构造一个新的rect
     */
    private static RectF buildRect(RectF rectF, float x, float y, float scope) {
        return buildRect(rectF, x, y, scope, 0, 0);
    }

    private static RectF buildRect(RectF rectF, float x, float y, float scope, float offsetX, float offsetY) {
        if (rectF == null) {
            rectF = new RectF();
        }

        if (scope <= 0) {
            return rectF;
        }

        rectF.set(x - scope, y - scope, x + scope, y + scope);
        rectF.offset(offsetX, offsetY);
        return rectF;
    }

    public static boolean checkOvalFramePress(float x, float y, PointF rectP1, PointF rectP2, Matrix mapMatrix) {
        PointF p = transformToScreenPoint(rectP1.x, rectP1.y, mapMatrix);
        float dpx = p.x;
        float dpy = p.y;
        p = transformToScreenPoint(rectP2.x, rectP2.y, mapMatrix);
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
        PointF p = transformToScreenPoint(rect.left, rect.top, mapMatrix);
        float dpx = p.x;
        float dpy = p.y;
        p = transformToScreenPoint(rect.right, rect.bottom, mapMatrix);
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
     * @param x
     * @param y
     * @param rectP1x
     * @param rectP1y
     * @param rectP2x
     * @param rectP2y
     * @return
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

    //如果两个向量点积的值大于0，他们的夹角是锐角，反之是钝角。
    private float dotProduct(float x, float y, PointF rectP1, PointF rectP2, float degree, RectF drawingBounds) {
        PointF p = mapDoodlePointToScreen(rectP1.x, rectP1.y, drawingBounds);
        float dpx = p.x;
        float dpy = p.y;

        p = mapDoodlePointToScreen(rectP2.x, rectP2.y, drawingBounds);
        float upx = p.x;
        float upy = p.y;

        float centerX = (dpx + upx) / 2.0f;
        float centerY = (dpy + upy) / 2.0f;

        //文本框水平中线向量 horizontalVector,因为是水平向量，y值为0
        float horizontalVector[] = {upx - centerX, 0};

        //映射到旋转前
        float[] pressedP = {x, y};
        mMapRectMatrix.reset();
        mMapRectMatrix.setRotate(-degree, centerX, centerY);
        mMapRectMatrix.mapPoints(pressedP);

        //文本框中心(起点)与手势按下点(终点)所成的向量
        float[] pressedVector = {pressedP[0] - centerX, pressedP[1] - centerY};

        //2个向量的点积
        return horizontalVector[0] * pressedVector[0] + horizontalVector[1] * pressedVector[1];
    }

    public static float calcRectDegrees(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2,
                                        Matrix mapMatrix) {
        RectF rect = transformToScreenRect(rectP1, rectP2, mapMatrix);

        float centerX = (rect.left + rect.right) / 2.0f;
        float centerY = (rect.top + rect.bottom) / 2.0f;

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

    public static float calcRectDegrees(float oldX, float oldY, float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);

        float centerX = (transRect.left + transRect.right) / 2.0f;
        float centerY = (transRect.top + transRect.bottom) / 2.0f;

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

    public static float calcRectScale(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2,
                                      Matrix mapMatrix) {
        RectF rect = transformToScreenRect(rectP1, rectP2, mapMatrix);

        float centerX = (rect.left + rect.right) / 2.0f;
        float centerY = (rect.top + rect.bottom) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float deltaScale = (float) (distance / preDistance);

        return deltaScale;
    }

    public static float calcRectScale(float oldX, float oldY, float x, float y, RectF rect,
                                      Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);

        float centerX = (transRect.left + transRect.right) / 2.0f;
        float centerY = (transRect.top + transRect.bottom) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float deltaScale = (float) (distance / preDistance);

        return deltaScale;
    }

    public static float[] calcRectDegreesAndScales(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2,
                                         Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rectP1, rectP2, mapMatrix);

        float centerX = (transRect.left + transRect.right) / 2.0f;
        float centerY = (transRect.top + transRect.bottom) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float deltaScale = (float) (distance / preDistance);

        double previousAngle = Math.atan2(preDeltaY, preDeltaX);
        double angle = Math.atan2(deltaY, deltaX);

        double degrees = angle - previousAngle;
        float radian = (float) (-degrees * (180 / Math.PI));

        mPointArr[0] = deltaScale;
        mPointArr[1] = radian;

        return mPointArr;
    }

    public static float[] calcRectDegreesAndScales(float oldX, float oldY, float x, float y, RectF rect,
                                            Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);

        float centerX = (transRect.left + transRect.right) / 2.0f;
        float centerY = (transRect.top + transRect.bottom) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float deltaScale = (float) (distance / preDistance);

        double previousAngle = Math.atan2(preDeltaY, preDeltaX);
        double angle = Math.atan2(deltaY, deltaX);

        double degrees = angle - previousAngle;
        float radian = (float) (-degrees * (180 / Math.PI));

        mPointArr[0] = deltaScale;
        mPointArr[1] = radian;

        return mPointArr;
    }

    public static float getDefaultTextHeight(Doodle doodle) {
        Paint paint = doodle.getPaint();
        WhiteBoard.BlackParams params = doodle.getWhiteboard().getBlackParams();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        textHeight = (textHeight / params.paintScale) * params.scale;
        if (textHeight < TextWriting.MIN_EDIT_TEXT_HEIGHT) {
            textHeight = TextWriting.MIN_EDIT_TEXT_HEIGHT;
        }
        return textHeight;
    }

}
