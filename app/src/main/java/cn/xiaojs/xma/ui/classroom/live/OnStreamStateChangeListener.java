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

import cn.xiaojs.xma.ui.classroom.Constants;

public interface OnStreamStateChangeListener {
    public final static int TYPE_STREAM_PLAYING = 1; //播放流
    public final static int TYPE_STREAM_PUBLISH = 2; //直播推流
    public final static int TYPE_STREAM_INDIVIDUAL = 3; //个人推流

    public void onStreamStarted(Constants.User user, int type);

    public void onStreamStopped(Constants.User user, int type);

}
