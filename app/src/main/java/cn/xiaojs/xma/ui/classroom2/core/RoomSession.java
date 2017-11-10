package cn.xiaojs.xma.ui.classroom2.core;

import java.util.Map;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public class RoomSession {

    protected String ticket;
    protected ClassroomType classroomType;                            //当前教室类型，是班级还是公开课等
    protected boolean liveShow;                                       //是否在直播秀
    protected boolean playLiveShow;                                   //是否在播放直播秀
    protected boolean one2one;
    protected String csOfCurrent;

    protected long individualStreamDuration;                          //直播秀时间

    protected CtlSession ctlSession;                                  //当前教室的状态值

    protected Map<String, Attendee> classMembers;                     //教室中的成员
    protected Attendee adviser;                                       //班主任信息，可能为空
    protected boolean preview;                                        //是否为预览模式


    public RoomSession(CtlSession session) {
        ctlSession = session;

        if (ctlSession.cls != null) {
            classroomType = ClassroomType.ClassLesson;
        } else {
            classroomType = ClassroomType.StandaloneLesson;
        }

        if (session.mode == Live.ClassroomMode.PREVIEW) {
            preview = true;
        }


        //FIXME 有问题么？
        if (Live.StreamType.INDIVIDUAL == session.streamType) {
            playLiveShow = true;
        }

    }
}
