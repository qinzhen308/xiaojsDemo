package cn.xiaojs.xma.ui.classroom.live;
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
 * Date:2017/3/20
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.ui.classroom.main.Constants;

public interface OnStreamStateChangeListener {
    /**
     * 当流已经开始
     */
    public void onStreamStarted(Constants.User user, int type, Object extra);

    /**
     * 当前流已经暂停
     */
    public void onStreamStopped(Constants.User user, int type, Object extra);

    /**
     * 当流的尺寸发生变化
     */
    public void onStreamSizeChanged(int w, int h);

}
