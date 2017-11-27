package cn.xiaojs.xma.ui.classroom2.core;

import java.util.Map;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.ctl.ClassInfo;
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
    protected ClassInfo classInfo;                                    //当前的班级信息


    public RoomSession(BootObservable.BootSession bootSession) {
        this.ctlSession = bootSession.ctlSession;
        this.classInfo = bootSession.classInfo;
        this.classMembers = bootSession.classMembers;
        this.adviser = bootSession.adviser;

        if (this.ctlSession.cls != null) {
            this.classroomType = ClassroomType.ClassLesson;
        } else {
            this.classroomType = ClassroomType.StandaloneLesson;
        }

        if (this.ctlSession.mode == Live.ClassroomMode.PREVIEW) {
            this.preview = true;
        }


        //FIXME 有问题么？
        if (Live.StreamType.INDIVIDUAL == this.ctlSession.streamType) {
            this.playLiveShow = true;
        }

    }
}
