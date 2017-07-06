package cn.xiaojs.xma.ui.classroom2;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.CtlSession;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class RoomSession {

    public ClassroomType classroomType;       //当前教室类型，是班级还是公开课等
    public boolean liveShow;                  //是否在直播秀
    public boolean playLiveShow;              //是否在播放直播秀
    public String csOfCurrent;

    public CtlSession ctlSession;             //当前教室的状态值

    public RoomSession(CtlSession session) {
        ctlSession = session;

        if (ctlSession.cls != null) {
            classroomType = ClassroomType.ClassLesson;
        } else {
            classroomType = ClassroomType.StandaloneLesson;
        }

        //FIXME 有问题么？
        if (Live.StreamType.INDIVIDUAL == session.streamType) {
            playLiveShow = true;
        }

    }
}
