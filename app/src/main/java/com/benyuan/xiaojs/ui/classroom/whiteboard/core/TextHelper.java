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
 * Date:2016/10/20
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.TextWriting;

import java.util.ArrayList;

public class TextHelper {
    private static PointF mPoint = new PointF();
    private static Matrix mTextMapMatrix = new Matrix();
    private static ArrayList<String> mMultiLineText = new ArrayList<String>();

    protected static PointF mDownPoint4Screen = new PointF();
    protected static PointF mUpPoint4Screen = new PointF();


    public static PointF measureTextSize(TextWriting doodle) {
        Paint paint = doodle.getPaint();
        String text = doodle.getTextString();
        WhiteBoard.BlackParams params = doodle.getWhiteboard().getBlackParams();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textWidth = 0;
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        ArrayList<String> multiLineText = getMultiLineText(text);
        int line = Math.max(1, multiLineText.size());

        String singleLineText;
        float w = 0;
        for (int i = 0; i < line; i++) {
            singleLineText = multiLineText.get(i);
            if (!TextUtils.isEmpty(singleLineText)) {
                w = paint.measureText(singleLineText);
                if (w > textWidth) {
                    textWidth = w;
                }
            }
        }

        textWidth = (textWidth / params.paintScale) * params.scale;
        textHeight = ((textHeight * line) / params.paintScale) * params.scale;

        textHeight = textHeight * line;

        mPoint.set(textWidth, textHeight);
        return mPoint;
    }

    public static ArrayList<String> getMultiLineText(String text) {
        mMultiLineText.clear();
        StringBuffer sb = new StringBuffer();
        int size = text.length();
        for (int i = 0; i < size; i++) {
            char c = text.charAt(i);
            if (c != 13 && c != 10) {
                sb.append(c);
            } else {
                mMultiLineText.add(sb.toString());
                sb.delete(0, sb.toString().length());
            }

            if (i == size - 1) {
                mMultiLineText.add(sb.toString());
                sb.delete(0, sb.toString().length());
            }
        }

        return mMultiLineText;
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

    public PointF getTextRotateCenterPoint(TextWriting textWriting) {
        PointF dp = textWriting.getDownPoint();
        PointF up = textWriting.getUpPoint();
        WhiteBoard.BlackParams params = textWriting.getWhiteboard().getBlackParams();
        float centerX = (dp.x + up.x) / 2.0f * params.originalWidth;
        float centerY = (dp.y + up.y) / 2.0f * params.originalHeight;
        mPoint.set(centerX, centerY);
        return mPoint;
    }

    public float calcRectRotation(float oldX, float oldY, float x, float y, TextWriting textWriting) {
        PointF dp = mDownPoint4Screen;
        PointF up = mUpPoint4Screen;
        Utils.mapDoodleToScreen(textWriting, dp, up);

        dp.set(dp.x, dp.y);
        up.set(up.x, up.y);

        float centerX = (dp.x + up.x) / 2.0f;
        float centerY = (dp.y + up.y) / 2.0f;

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

    public float calcRectScale(float oldX, float oldY, float x, float y, TextWriting textWriting) {
        PointF dp = mDownPoint4Screen;
        PointF up = mUpPoint4Screen;
        Utils.mapDoodleToScreen(textWriting, dp, up);

        dp.set(dp.x, dp.y);
        up.set(up.x, up.y);

        float centerX = (dp.x + up.x) / 2.0f;
        float centerY = (dp.y + up.y) / 2.0f;

        //屏幕坐标系和标准坐标系的Y轴方向相反
        float preDeltaX = oldX - centerX;
        float preDeltaY = -(oldY - centerY);
        float deltaX = x - centerX;
        float deltaY = -(y - centerY);

        double preDistance = Math.sqrt(preDeltaX * preDeltaX + preDeltaY * preDeltaY);
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float tempScale = textWriting.getTotalScale();
        float deltaScale = (float) (distance / preDistance);
        float totalScale = tempScale * deltaScale;

        if (totalScale > TextWriting.MAX_SCALE) {
            totalScale = TextWriting.MAX_SCALE;
            deltaScale = totalScale / tempScale;
        }
        if (totalScale < TextWriting.MIN_SCALE) {
            totalScale = TextWriting.MIN_SCALE;
            deltaScale = totalScale / tempScale;
        }
        textWriting.setTotalScale(deltaScale);

        float[] xArr = {x, 0};
        float[] oldXArr = {oldX, 0};
        mTextMapMatrix.reset();
        mTextMapMatrix.setRotate(-textWriting.getDegree(), centerX, centerY);
        mTextMapMatrix.mapPoints(xArr);
        mTextMapMatrix.mapPoints(oldXArr);

        //to be optimized
        if ((deltaScale > 1 && (xArr[0] - oldXArr[0]) < 0 || (deltaScale < 1) && (xArr[0] - oldXArr[0]) > 0)) {
            textWriting.setTotalScale(tempScale);
            return 1.0f;
        }

        return deltaScale;
    }

    public void changeArea(float oldX, float oldY, float x, float y, TextWriting textWriting) {
        if (dotProduct(x, y, textWriting) < 0) {
            //do nothing
            return;
        }
        float deltaScale = calcRectScale(oldX, oldY, x, y, textWriting);
        float totalScale = textWriting.getTotalScale();
        if (totalScale >= TextWriting.MIN_SCALE && totalScale <= TextWriting.MAX_SCALE) {
            Paint paint = textWriting.getPaint();
            paint.setTextSize(paint.getTextSize() * totalScale);
        }
    }

    //如果两个向量点积的值大于0，他们的夹角是锐角，反之是钝角。
    private float dotProduct(float x, float y, TextWriting textWriting) {
        PointF dp = mDownPoint4Screen;
        PointF up = mUpPoint4Screen;
        Utils.mapDoodleToScreen(textWriting, dp, up);
        float centerX = (dp.x + up.x) / 2.0f;
        float centerY = (dp.y + up.y) / 2.0f;

        //文本框水平中线向量 horizontalVector,因为是水平向量，y值为0
        float horizontalVector[] = {up.x - centerX, 0};

        //映射到旋转前
        float[] pressedP = {x, y};
        mTextMapMatrix.reset();
        mTextMapMatrix.setRotate(textWriting.getDegree(), centerX, centerY);
        mTextMapMatrix.mapPoints(pressedP);

        //文本框中心(起点)与手势按下点(终点)所成的向量
        float[] pressedVector = {pressedP[0] - centerX, pressedP[1] - centerY};

        //2个向量的点积
        return horizontalVector[0] * pressedVector[0] + horizontalVector[1] * pressedVector[1];
    }
}
