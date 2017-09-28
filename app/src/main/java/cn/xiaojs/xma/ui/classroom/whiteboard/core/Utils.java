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
 * Date:2016/10/19
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Random;

import cn.xiaojs.xma.ui.classroom.socketio.ProtocolConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

public class Utils {
    public final static double ZERO = 1e-9;

    private final static RectF mRect = new RectF();
    private static PointF mPoint = new PointF();
    private final static LineSegment[] mDiagonal = new LineSegment[2];

    private final static Matrix mMapRectMatrix = new Matrix();
    private final static Matrix mMapPointMatrix = new Matrix();
    private final static float[] mPointArr = new float[2];
    public final static Paint mDelBtnPaint = buildDelBtnPaint();
    public final static Paint mControllerPaint = buildControllerPaint();

    private final static String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String CHARACTERS_LOWCASE = "0123456789abcdefghijklmnopqrstuvwxyz";

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
        Whiteboard.WhiteboardParams params = doodle.getWhiteboard().getParams();
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
        paint.setStrokeWidth(strokeWidth < 0 ? WhiteboardConfigs.PAINT_STROKE_WIDTH : strokeWidth);
        paint.setColor(color);
        return paint;
    }

    public static Paint buildDashPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH);
        p.setColor(WhiteboardConfigs.BORDER_COLOR);
        float dw = WhiteboardConfigs.BORDER_DASH_WIDTH;
        PathEffect blackEffects = new DashPathEffect(new float[]{dw, dw}, 0);
        p.setPathEffect(blackEffects);
        return p;
    }

    public static Paint buildControllerPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(WhiteboardConfigs.CONTROLLER_RADIUS);
        p.setColor(WhiteboardConfigs.BORDER_COLOR);
        return p;
    }

    public static Paint buildDelBtnPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(WhiteboardConfigs.DEL_BTN_STROKE_WIDTH);
        p.setColor(Color.WHITE);
        return p;
    }

    /**
     * Creates text writing paint for blackboard.
     */
    public static Paint createTextWritingPaint() {
        Paint paint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(WhiteboardConfigs.DEFAULT_TEXT_SIZE);
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

    /**
     * 根据2个点获取线段
     */
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

        return (crossProduct(vectorAC, vectorAD) * crossProduct(vectorBC, vectorBD) <= ZERO)
                && (crossProduct(vectorAC, vectorBC) * crossProduct(vectorAD, vectorBD) <= ZERO);

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
    public static double crossProduct(PointF vectorA, PointF vectorB) {
        return vectorA.x * vectorB.y - vectorB.x * vectorA.y;
    }

    /**
     * 两个向量点积
     */
    public static double dotProduct(PointF vectorA, PointF vectorB) {
        return vectorA.x * vectorB.x + vectorA.y * vectorB.y;
    }

    /**
     * 向量的模
     */
    public static double module(PointF vectorA) {
        return Math.sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y);
    }

    /**
     * 求向量A在向量B上的投影值A1. A1的长度计算： |A1| = |A|cosθ = |A|A.B / |A||B| = A.B / |B|
     */
    public static double vectorProjection(PointF vectorA, PointF vectorB) {
        return dotProduct(vectorA, vectorB) / module(vectorB);
    }

    /**
     * 求向量A旋转后的向量
     *
     * @param vectorA 旋转前的向量
     * @param degree  旋转角度
     */
    public static PointF mapRotateVector(PointF vectorA, float degree) {
        mMapPointMatrix.reset();
        mMapPointMatrix.postRotate(degree);
        mPointArr[0] = vectorA.x;
        mPointArr[1] = vectorA.y;
        mMapPointMatrix.mapPoints(mPointArr);

        vectorA.set(mPointArr[0], mPointArr[1]);
        return vectorA;
    }

    /**
     * 矩阵连乘
     */
    public static Matrix postConcatMatrix(Matrix... matrices) {
        mMapRectMatrix.reset();
        if (matrices != null) {
            for (Matrix matrix : matrices) {
                mMapRectMatrix.postConcat(matrix);
            }
        }
        return mMapRectMatrix;
    }

    public static Matrix transformScreenMatrix(Matrix drawingMatrix, Matrix displayMatrix) {
        mMapRectMatrix.reset();
        if (drawingMatrix != null) {
            mMapRectMatrix.postConcat(drawingMatrix);
        }
        if (displayMatrix != null) {
            mMapRectMatrix.postConcat(displayMatrix);
        }

        return mMapRectMatrix;
    }

    /*
     * 事件相交取值范围:
     * RECT_BODY 按下矩形体
     *
     * LEFT_TOP_CORNER 矩形左上角
     * RIGHT_TOP_CORNER 矩形右上角
     * LEFT_BOTTOM_CORNER 矩形左下角
     * RIGHT_BOTTOM_CORNER 矩形右下角
     *
     * TOP_EDGE 矩形上条边
     * RIGHT_EDGE 矩形右条边
     * BOTTOM_EDGE 矩形底条边
     * LEFT_EDGE 矩形左条边
     */

    /**
     * 计算变换矩阵，该矩阵用来变换图形所在区域矩形 事件相交取值范围:
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
     * 变换doodle坐标到屏幕rect
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
     * 变换doodle rect对应的坐标到屏幕rect
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
     * 通过某一个点和scope构造一个新的rect
     */
    public static RectF buildRect(RectF rectF, float x, float y, float scope) {
        return buildRect(rectF, x, y, scope, 0, 0);
    }

    public static RectF buildRect(RectF rectF, float x, float y, float scope, float offsetX, float offsetY) {
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

    /**
     * 如果两个向量点积的值大于0，他们的夹角是锐角，反之是钝角。
     */
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

    /**
     * 计算旋转角度
     */
    public static float calcRectDegrees(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2,
                                        Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rectP1, rectP2, mapMatrix);
        return calcRectDegrees(oldX, oldY, x, y, transRect);
    }

    public static float calcRectDegrees(float oldX, float oldY, float x, float y, RectF rect, Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        return calcRectDegrees(oldX, oldY, x, y, transRect);
    }

    public static float calcRectDegrees(float oldX, float oldY, float x, float y, RectF transRect) {
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

    /**
     * 计算缩放比例值
     */
    public static float calcRectScale(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2,
                                      Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rectP1, rectP2, mapMatrix);
        return calcRectScale(oldX, oldY, x, y, transRect);
    }

    public static float calcRectScale(float oldX, float oldY, float x, float y, RectF rect,
                                      Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        return calcRectScale(oldX, oldY, x, y, transRect);
    }

    public static float calcRectScale(float oldX, float oldY, float x, float y, RectF transRect) {
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

    /**
     * 计算缩放比例和旋转角度
     */
    public static float[] calcRectDegreesAndScales(float oldX, float oldY, float x, float y, PointF rectP1, PointF rectP2,
                                                   Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rectP1, rectP2, mapMatrix);
        return calcRectDegreesAndScales(oldX, oldY, x, y, transRect);
    }

    public static float[] calcRectDegreesAndScales(float oldX, float oldY, float x, float y, RectF rect,
                                                   Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        return calcRectDegreesAndScales(oldX, oldY, x, y, transRect);
    }

    public static float[] calcRectDegreesAndScales(float oldX, float oldY, float x, float y, RectF transRect) {
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

    /*public static float[] calcRectScalesXY(float oldX, float oldY, float x, float y, RectF rect,
                                                   Matrix mapMatrix) {
        RectF transRect = transformToScreenRect(rect, mapMatrix);
        return calcRectScalesXY(oldX, oldY, x, y, transRect);
    }

    public static float[] calcRectScalesXY(float oldX, float oldY, float x, float y, RectF transRect) {
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

    }*/

    public static float getDefaultTextHeight(Doodle doodle) {
        Paint paint = doodle.getPaint();
        Whiteboard.WhiteboardParams params = doodle.getWhiteboard().getParams();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        textHeight = (textHeight / params.paintScale) * params.scale;
        if (textHeight < WhiteboardConfigs.MIN_EDIT_TEXT_HEIGHT) {
            textHeight = WhiteboardConfigs.MIN_EDIT_TEXT_HEIGHT;
        }
        return textHeight;
    }

    public static void drawDelBtn(Canvas canvas, RectF borderRect, Path borderDrawingPath,
                                  Matrix matrix, Whiteboard.WhiteboardParams params) {
        borderDrawingPath.reset();
        borderDrawingPath.addOval(borderRect, Path.Direction.CCW);
        borderDrawingPath.transform(matrix);
        canvas.drawPath(borderDrawingPath, mControllerPaint);

        float w = borderRect.width() / 5;
        float h = borderRect.height() / 5;
        float cx = borderRect.centerX();
        float cy = borderRect.centerY();
        borderRect.set(cx - w, cy - h, cx + w, cy + h);
        borderDrawingPath.reset();
        borderDrawingPath.moveTo(borderRect.left, borderRect.top);
        borderDrawingPath.quadTo(borderRect.left, borderRect.top, borderRect.right, borderRect.bottom);

        borderDrawingPath.moveTo(borderRect.right, borderRect.top);
        borderDrawingPath.quadTo(borderRect.right, borderRect.top, borderRect.left, borderRect.bottom);
        borderDrawingPath.transform(matrix);
        mDelBtnPaint.setStrokeWidth(WhiteboardConfigs.DEL_BTN_STROKE_WIDTH / params.scale);
        canvas.drawPath(borderDrawingPath, mDelBtnPaint);
    }


    public static int getColor(String color, int defaultColor) {
        int c = defaultColor;
        try {
            if (TextUtils.isEmpty(color)) {
                return c;
            }

            if (color.length() == 6) {
                //not alpha
                int r = Integer.parseInt(color.substring(0, 2), 16);
                int g = Integer.parseInt(color.substring(2, 4), 16);
                int b = Integer.parseInt(color.substring(4, 6), 16);
                c = Color.argb(255, r, g, b);
            } else if (color.length() == 8) {
                int a = Integer.parseInt(color.substring(0, 2), 16);
                int r = Integer.parseInt(color.substring(2, 4), 16);
                int g = Integer.parseInt(color.substring(4, 6), 16);
                int b = Integer.parseInt(color.substring(6, 8), 16);
                c = Color.argb(a, r, g, b);
            }
        } catch (Exception e) {

        }

        return c;
    }

    public static String buildDoodleId() {
        int size = CHARACTERS_LOWCASE.length();
        StringBuilder sb = new StringBuilder(ProtocolConfigs.SHAPE_ID_LENGTH);
        Random random=new Random();
        for(int i=0;i<size;i++){
            sb.append(CHARACTERS_LOWCASE.charAt(random.nextInt(size)));
        }
        return sb.toString();
    }

}
