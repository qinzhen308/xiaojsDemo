package cn.xiaojs.xma.ui.classroom2.core;

import android.content.Context;
import android.os.Message;


import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.State;
import cn.xiaojs.xma.common.statemachine.StateMachine;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.FinishClassResponse;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import cn.xiaojs.xma.model.socket.room.ClosePreviewReceive;
import cn.xiaojs.xma.model.socket.room.ConstraintKickoutReceive;
import cn.xiaojs.xma.model.socket.room.EmptyReceive;
import cn.xiaojs.xma.model.socket.room.LogoutKickoutReceive;
import cn.xiaojs.xma.model.socket.room.MediaAbortedReceive;
import cn.xiaojs.xma.model.socket.room.MediaDeviceRefreshReceive;
import cn.xiaojs.xma.model.socket.room.MediaFeedbackReceive;
import cn.xiaojs.xma.model.socket.room.ModeSwitchReceive;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.ReclaimedReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardAckReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StopShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StreamExpirationReceive;
import cn.xiaojs.xma.model.socket.room.StreamQualityChangedReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/7/4.
 */

public abstract class ClassroomStateMachine extends StateMachine {


    private Context context;
    private RoomSession roomSession;

    public ClassroomStateMachine(Context context, String name, RoomSession session) {
        super(name);
        this.context = context.getApplicationContext();
        this.roomSession = session;
        setDbg(XiaojsConfig.DEBUG);

        //加载教室成员
        loadMemberList();

    }




    public void destoryAndQuitNow() {
        quitNow();
    }

    protected Context getContext() {
        return context;
    }

    public RoomSession getSession() {
        return this.roomSession;
    }


    public abstract String getLiveState();

    public String getPublishUrl() {
        return roomSession.ctlSession.publishUrl;
    }

    public String getPlayUrl() {
        return roomSession.ctlSession.playUrl;
    }

    public int getLiveMode() {
        return roomSession.ctlSession.mode;
    }

    protected CTLConstant.UserIdentity getUserIdentity(String psType) {

        if ("LeadSession".equals(psType)) {
            return CTLConstant.UserIdentity.LEAD;
        } else if ("TeacherSession".equals(psType)) {
            return CTLConstant.UserIdentity.TEACHER2;
        } else if ("AssistantSession".equals(psType)) {
            return CTLConstant.UserIdentity.ASSISTANT;
        } else if ("RemoteAssistantSession".equals(psType)) {
            return CTLConstant.UserIdentity.REMOTE_ASSISTANT;
        } else if ("StudentSession".equals(psType)) {
            return CTLConstant.UserIdentity.STUDENT;
        } else if ("ManagerSession".equals(psType)) {
            return CTLConstant.UserIdentity.MANAGER;
        } else if ("AuditorSession".equals(psType)) {
            return CTLConstant.UserIdentity.AUDITOR;
        } else if ("AuditorSession".equals(psType)) {
            return CTLConstant.UserIdentity.ADMINISTRATOR;
        } else if ("AdviserSession".equals(psType)) {
            return CTLConstant.UserIdentity.ADVISER;
        }

        return CTLConstant.UserIdentity.NONE;
    }

    protected void sendTransitionMessage(State state) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.TRANSITION_STATE;
        message.obj = state;
        sendMessage(message);
    }


    /**
     * 开始上课
     */
    public void startLesson(ClassResponse response) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.START_LESSON;
        message.obj = response;
        sendMessage(message);
    }

    /**
     * 开始课间休息
     */
    public void pauseLesson() {
        sendMessage(CTLConstant.StandloneChannel.PAUSE_LESSON);
    }

    /**
     * 恢复上课
     */
    public void resumeLesson(ClassResponse response) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.RESUME_LESSON;
        message.obj = response;
        sendMessage(message);
    }

    /**
     * 下课
     */
    public void finishLesson(FinishClassResponse response) {
        Message message = new Message();
        message.what = CTLConstant.BaseChannel.FINISH_LESSON;
        message.obj = response;
        sendMessage(message);
    }

    /**
     * 开始直播秀
     */
    public void startLiveShow(ClaimReponse claimReponse) {
        if (claimReponse != null && claimReponse.result) {
            Message message = new Message();
            message.what = CTLConstant.BaseChannel.START_LIVE_SHOW;
            message.obj = claimReponse;
            sendMessage(message);
        }
    }

    /**
     * 结束直播秀
     */
    public void stopLiveShow() {
        sendMessage(CTLConstant.BaseChannel.STOP_LIVE_SHOW);
    }

    /**
     * 开始播放直播秀
     */
//    public void startPlayLiveShow() {
//        sendMessage(CTLConstant.BaseChannel.START_PLAY_LIVE_SHOW);
//    }

    /**
     * 停止播放直播秀
     */
//    public void stopPlayLiveShow() {
//        sendMessage(CTLConstant.BaseChannel.STOP_PLAY_LIVE_SHOW);
//    }



    public void addMember(Attendee attendee) {
        if (roomSession.classMembers == null) {
            roomSession.classMembers = new HashMap<>();
        }

        roomSession.classMembers.put(attendee.accountId, attendee);

    }

    public void removeMember(String accountId) {
        if (roomSession.classMembers != null) {
            roomSession.classMembers.remove(accountId);
        }
    }

    public Attendee getMember(String accountId) {
        if (roomSession.classMembers != null) {
            return roomSession.classMembers.get(accountId);
        }

        return null;
    }



    protected void switchStateWhenReceiveSyncState(String state) {

        if (Live.LiveSessionState.FINISHED.equals(state)) {
            getSession().ctlSession.publishUrl = null;
            getSession().ctlSession.playUrl = null;
            getSession().ctlSession.streamType = Live.StreamType.NONE;
        }


    }

    protected void dealReceiveSyncClassState(SyncClassStateReceive message) {

    }

    /**
     * 获取教室的标题
     */
    protected abstract String getTitle();

    protected abstract CTLConstant.UserIdentity getIdentity();

    protected abstract CTLConstant.UserIdentity getIdentityInLesson();


    /**
     * 是否能进行强制推流
     */
    protected abstract boolean canForceIndividual();

    /**
     * 是否可以直播秀状态
     */
    protected abstract boolean canIndividualByState();

    /**
     * 是否有教学能力
     */
    protected abstract boolean hasTeachingAbility();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 事件响应方法
    //

    protected void closeMedia(CloseMediaReceive message) {
        getSession().one2one = false;
    }

    protected void streamStopped(StreamStopReceive message) {
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.playUrl = null;

        if (message.streamType == Live.StreamType.INDIVIDUAL) {

            ctlSession.finishOn = 0;
            ctlSession.streamType = Live.StreamType.NONE;
            ctlSession.claimedBy = null;

        } else if (message.streamType == Live.StreamType.LIVE) {

        }
    }

    protected void streamStartted(StreamStartReceive message) {
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;

        ctlSession.playUrl = message.RTMPPlayUrl;
        ctlSession.finishOn = message.finishOn;
        ctlSession.streamType = message.streamType;
        ctlSession.mode = message.mode;
        ctlSession.claimedBy = message.claimedBy;

        if (message.streamType == Live.StreamType.INDIVIDUAL) {

            roomSession.individualStreamDuration = message.finishOn;

        } else if (message.streamType == Live.StreamType.LIVE) {

        }
    }

    protected void streamReclaimed(ReclaimedReceive message) {
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.publishUrl = null;
        ctlSession.finishOn = 0;
        ctlSession.streamType = Live.StreamType.NONE;
        ctlSession.claimedBy = null;

        stopLiveShow();
    }

    protected void streamExpiration(StreamExpirationReceive message) {
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.publishUrl = null;
        ctlSession.finishOn = 0;
        ctlSession.streamType = Live.StreamType.NONE;

        if (message.streamType == Live.StreamType.INDIVIDUAL) {
            stopLiveShow();
        } else if (message.streamType == Live.StreamType.LIVE) {
            finishLesson(null);
        }

    }

    protected void modeSwitch(ModeSwitchReceive message) {
        if (message == null) {
            return;
        }

        CtlSession ctlSession = roomSession.ctlSession;
        ctlSession.mode = message.to;

    }

    protected void closePreviewByClassover(ClosePreviewReceive message) {
        if (message == null) {
            return;
        }
        //TODO
    }

    protected void remindFinal(EmptyReceive message) {
        if (message == null) {
            return;
        }
    }

    protected void join(Attendee message) {
        if (message == null) {
            return;
        }
    }

    protected void leave(Attendee message) {
        if (message == null) {
            return;
        }
    }

    protected void constraintKickout(ConstraintKickoutReceive message) {
        if (message == null) {
            return;
        }

        //TODO
    }

    protected void logoutKickout(LogoutKickoutReceive message) {
        if (message == null) {
            return;
        }
        //TODO

    }

    protected void mediaAborted(MediaAbortedReceive message) {
        if (message == null) {
            return;
        }
        //TODO
    }

    protected void mediaDeviceRefresh(MediaDeviceRefreshReceive message) {
        if (message == null) {
            return;
        }
        //TODO
    }

    protected void mediaFeedback(MediaFeedbackReceive message) {
        if (message == null) {
            return;
        }

        if (message.status == Live.MediaStatus.READY) {
            getSession().ctlSession.playUrl = message.playUrl;
            getSession().one2one = true;
        } else {
            getSession().ctlSession.playUrl = null;
            getSession().one2one = false;
        }

    }

    protected void openMeidaRecevied(OpenMediaReceive message) {
        if (message == null) {
            return;
        }

        getSession().ctlSession.publishUrl = message.publishUrl;
        getSession().one2one = true;
    }

    protected void streamQualityChangedRecevied(StreamQualityChangedReceive message) {
        if (message == null) {
            return;
        }
    }

    protected void syncClassStateRecevied(SyncClassStateReceive message) {
        if (message == null) {
            return;
        }

        dealReceiveSyncClassState(message);

    }

    protected void syncStateRecevied(SyncStateReceive message) {
        if (message == null) {
            return;
        }

        getSession().ctlSession.state = message.to;
        switchStateWhenReceiveSyncState(message.to);
    }

    protected void talkRecevied(Talk message) {
        if (message == null) {
            return;
        }

        //TODO
    }


    protected void shareboardReceived(final ShareboardReceive message) {
    }

    protected void shareboardAckReceived(ShareboardAckReceive message) {

    }

    protected void stopShareboardReceived(StopShareboardReceive message) {

    }


    protected void syncBoardReceived(SyncBoardReceive message) {

    }

    private void loadMemberList() {

        LiveManager.getAttendees(getContext(), roomSession.ticket,
                new APIServiceCallback<LiveCollection<Attendee>>() {
                    @Override
                    public void onSuccess(LiveCollection<Attendee> liveCollection) {

                        if (liveCollection != null
                                && liveCollection.attendees != null
                                && liveCollection.attendees.size()>0) {

                            addMembersInSession(liveCollection.attendees);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        if (XiaojsConfig.DEBUG) {
                            Logger.e("load class members failed....");
                        }
                    }
                });
    }

    private void addMembersInSession(ArrayList<Attendee> attendees){
        Observable.fromArray(attendees)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<Attendee>>() {
                    @Override
                    public void accept(ArrayList<Attendee> attendees) throws Exception {
                        if(roomSession.classMembers == null) {
                            roomSession.classMembers = new HashMap<>();
                        }

                        for (Attendee attendee : attendees) {
                            roomSession.classMembers.put(attendee.accountId, attendee);
                        }

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("load class members over....");
                        }
                    }
                });
    }

}
