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
 * Date:2017/3/31
 * Desc:
 *
 * ======================================================================================== */

public class StreamType {
    public final static int TYPE_STREAM_PLAY = 1; //播放流
    public final static int TYPE_STREAM_PUBLISH = 2; //直播推流
    public final static int TYPE_STREAM_PLAY_PEER_TO_PEER = 3; //一对一播放流
    public final static int TYPE_STREAM_PUBLISH_PEER_TO_PEER = 4; //一对一推送流
    public final static int TYPE_STREAM_PLAY_INDIVIDUAL = 5; //播放个人推流
    public final static int TYPE_STREAM_PUBLISH_INDIVIDUAL = 6; //个人推流
    public final static int TYPE_STREAM_RECLAIMED_INDIVIDUAL = 7; //个人推流(回收)
}
