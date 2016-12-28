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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;

import java.util.ArrayList;
import java.util.UUID;

public class WhiteboardLayer {
    public static final float DOODLE_CANVAS_RATIO = 4 / 3.0F; // w:h = 4:3
    private String mWhiteboardId;
    private String mWhiteboardName;

    private ArrayList<Doodle> mAllDoodles;
    private ArrayList<Doodle> mReDoStack;

    public WhiteboardLayer() {
        mWhiteboardId = UUID.randomUUID().toString();
        init();
    }

    public WhiteboardLayer(String id) {
        mWhiteboardId = id;
        init();
    }

    private void init() {
        mAllDoodles = new ArrayList<Doodle>();
        mReDoStack = new ArrayList<Doodle>();
    }

    public ArrayList<Doodle> getAllDoodles() {
        return mAllDoodles;
    }

    public ArrayList<Doodle> getReDoStack() {
        return mReDoStack;
    }

    public String getWhiteboardId (){
        return mWhiteboardId;
    }

    public String getWhiteboardName() {
        return mWhiteboardName;
    }

}
