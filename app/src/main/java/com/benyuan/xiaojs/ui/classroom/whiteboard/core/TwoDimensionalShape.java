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

public abstract class TwoDimensionalShape extends GeometryShape {

    protected TwoDimensionalShape(WhiteBoard whiteBoard, int geometryId) {
        super(whiteBoard, Doodle.STYLE_GEOMETRY, geometryId);
    }
}
