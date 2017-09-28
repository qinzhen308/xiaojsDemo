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
 * Date:2016/12/6
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class ActionRecord {
    public int action;
    public int groupId;
    public String id;

    public ActionRecord(String id, int groupId, int ac) {
        this.id = id;
        this.groupId = groupId;
        this.action = ac;
    }

    //translate
    public float translateX;
    public float translateY;
    //scale
    public float scaleX;
    public float scaleY;
    //rotate
    public float degree;

    //transform matrix
    public Matrix mTransMatrix;
    public Matrix mBorderTransMatrix;

    public List<PointF> mPoints;

    public RectF rect;

    //for text writing
    public String textStr;

    public void setPoints(List<PointF> points) {
        if (points != null && !points.isEmpty()) {
            if (mPoints == null) {
                mPoints = new ArrayList<PointF>();
            }

            for (PointF p : points) {
                mPoints.add(new PointF(p.x, p.y));
            }
        }
    }

    public void setMatrix(Matrix transformMatrix) {
        if (transformMatrix != null) {
            mTransMatrix = new Matrix();
            mTransMatrix.set(transformMatrix);
        }
    }

    public void setBorderMatrix(Matrix transformMatrix) {
        if (transformMatrix != null) {
            mBorderTransMatrix = new Matrix();
            mBorderTransMatrix.set(transformMatrix);
        }
    }

    public void setRect(RectF src) {
        if (rect == null) {
            rect = new RectF();
        }
        rect.set(src);
    }
}
