package com.benyuan.xiaojs.ui.classroom;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;

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
 * Date:2016/11/30
 * Desc:
 *
 * ======================================================================================== */

public class ClassRoomGestureDetector extends GestureDetector {
    private Context mContext;

    public ClassRoomGestureDetector(Context context, OnGestureListener listener) {
        super(context, listener);
        init(context);
    }

    public ClassRoomGestureDetector(Context context, OnGestureListener listener, Handler handler) {
        super(context, listener, handler);
        init(context);
    }

    public ClassRoomGestureDetector(Context context, OnGestureListener listener, Handler handler, boolean unused) {
        super(context, listener, handler, unused);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public int getState() {
        if (mContext instanceof ClassRoomActivity) {
            return ((ClassRoomActivity)mContext).getCurrentState();
        }

        return ClassRoomActivity.STATE_MAIN_PANEL;
    }
}