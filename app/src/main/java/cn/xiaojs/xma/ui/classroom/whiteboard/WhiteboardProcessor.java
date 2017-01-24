package cn.xiaojs.xma.ui.classroom.whiteboard;
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
 * Date:2017/1/24
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;

import java.util.List;

import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;

public class WhiteboardProcessor {
    private final static Object LOCK = new Object();

    public static Bitmap process(WhiteboardCollection wbColl, int width, int height) {
        if (wbColl == null) {
            return null;
        }

        List<WhiteboardLayer> layers = wbColl.getWhiteboardLayer();
        if (layers == null || layers.isEmpty()) {
            return null;
        }

        WhiteboardLayer layer = layers.size() == 1 ? layers.get(0) : layers.get(wbColl.getCurrIndex());
        List<Doodle> doodles = layer.getAllDoodles();
        if (doodles == null || doodles.isEmpty()) {
            return null;
        }


        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        //draw bg
        canvas.drawColor(Color.WHITE);

        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0, 0, 1, 1), new RectF(0, 0, width, height), Matrix.ScaleToFit.FILL);

        synchronized (LOCK) {
            float paintScale = width / (float)layer.getWidth();
            for (Doodle doodle : doodles) {
                if (doodle instanceof TextWriting) {
                    float oldPaintSize = doodle.getPaint().getTextSize();
                    doodle.getPaint().setTextSize(oldPaintSize * paintScale);
                    doodle.setDrawScale(paintScale);
                    doodle.setDrawingMatrix(matrix);
                    doodle.drawSelf(canvas);
                    //reset
                    doodle.setDrawScale(1);
                    doodle.getPaint().setTextSize(oldPaintSize);
                } else {
                    float oldPaintSize = doodle.getPaint().getStrokeWidth();
                    doodle.getPaint().setStrokeWidth(oldPaintSize * paintScale);
                    doodle.setDrawingMatrix(matrix);
                    doodle.drawSelf(canvas);
                    //reset
                    doodle.getPaint().setStrokeWidth(oldPaintSize);
                }
            }
        }

        return bmp;
    }
}
