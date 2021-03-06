package cn.xiaojs.xma.ui.classroom.whiteboard.shape;
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

import android.graphics.Canvas;
import android.graphics.Path;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;

public class Eraser extends Doodle {

    private Eraser(Whiteboard whiteboard) {
        super(whiteboard, Doodle.STYLE_HAND_WRITING);
    }

    @Override
    public void onDrawSelf(Canvas canvas) {

    }

    @Override
    public boolean onCheckSelected(float x, float y) {
        return false;
    }

    @Override
    public Path getScreenPath() {
        return null;
    }
}
