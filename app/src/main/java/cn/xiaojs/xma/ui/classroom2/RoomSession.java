package cn.xiaojs.xma.ui.classroom2;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class RoomSession {

    protected String ticket;
    protected ClassroomType classroomType;       //当前教室类型，是班级还是公开课等
    protected boolean liveShow;                  //是否在直播秀
    protected boolean playLiveShow;              //是否在播放直播秀
    protected boolean one2one;
    protected String csOfCurrent;
    protected ShareboardReceive shareboardData;  //白板协作时初次打开白板的初始化数据

    protected long individualStreamDuration;     //直播秀时间

    protected CtlSession ctlSession;             //当前教室的状态值

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
