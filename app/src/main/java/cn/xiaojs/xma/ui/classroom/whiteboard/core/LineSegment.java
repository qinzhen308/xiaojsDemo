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
 * Date:2016/10/27
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.PointF;

public class LineSegment {
    public PointF point1;
    public PointF point2;

    public LineSegment() {
        point1 = new PointF();
        point2 = new PointF();
    }

    public double getLength() {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) +
                (point1.y - point2.y) * (point1.y - point2.y));
    }

    @Override
    public String toString() {
        return "LineSegment(" + point1.x + ", " + point1.y + " = "
                + point2.x + ", " + point2.y + ")";
    }
}
