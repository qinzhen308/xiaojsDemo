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
 * Date:2016/10/18
 * Desc:
 *
 * ======================================================================================== */

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;


public abstract class GeometryShape extends Doodle {
    public final static int BEELINE = 0;
    public final static int RECTANGLE = 1;
    public final static int OVAL = 2;
    public final static int TRIANGLE = 3;
    public final static int ARROW = 4;
    public final static int DOUBLE_ARROW = 5;

    protected int mGeometryId;

    protected GeometryShape(WhiteBoard whiteBoard, int style, int geometryId) {
        super(whiteBoard, style);
        mGeometryId = geometryId;
    }

    @Override
    public void move(float x, float y) {

    }

    protected abstract void changeShape(float touchX, float touchY);

    protected abstract double computeArea();

    protected abstract double computeEdgeLength();

    public static boolean isTwoDimension(int geometryId) {
        switch (geometryId) {
            case ARROW:
            case DOUBLE_ARROW:
            case BEELINE:
            case RECTANGLE:
            case OVAL:
                return true;
        }

        return false;
    }

    public int getGeometryId() {
        return mGeometryId;
    }

}
