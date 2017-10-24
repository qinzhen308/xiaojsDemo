package cn.xiaojs.xma.ui.classroom2.core;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.State;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse;

/**
 * Created by maxiaobao on 2017/7/4.
 * 班级状态机
 */

public class ClassStateMachine extends ClassroomStateMachine{

    private State idleState = new IdleState();
    private State pending4LiveState = new Pending4LiveState();
    private State scheduledState = new ScheduledState();
    private State pending4JoinState = new Pending4JoinState();
    private State liveState = new LiveState();
    private State liveShowState = new LiveShowState();




    public ClassStateMachine(Context context,RoomSession session) {
        super(context, "ClassStateMachine",session);
        initStateTree();
        initialState();

        if (session.ctlSession.finishOn >0 &&
                (session.ctlSession.streamType == Live.StreamType.INDIVIDUAL
                        || session.ctlSession.streamType == Live.StreamType.LIVE)) {

            liveTimerObserver.startCounter();

        }
    }

    public void initStateTree() {
        addState(idleState);
        addState(pending4LiveState, idleState);
        addState(scheduledState, idleState);
        addState(pending4JoinState,idleState);
        addState(liveShowState, idleState);
        addState(liveState, idleState);
    }

    public void initialState() {
        String state = getLiveState();
        State initState = getStateBySession(state);
        setInitialState(initState);
    }

    @Override
    public String getLiveState() {

        CtlSession session = getSession().ctlSession;
        if (session.ctl != null) {
            return session.state;
        } else {
            return session.cls.state;
        }
    }

    @Override
    protected String getTitle() {
        CtlSession session = getSession().ctlSession;
        if (session.ctl != null) {
            return session.ctl.title;
        }
        return session.cls.title;
    }

    @Override
    protected boolean canForceIndividual() {
        return hasTeachingAbility()
                || getIdentity() == CTLConstant.UserIdentity.ADVISER;
    }

    @Override
    protected boolean canIndividualByState() {

        String state = getLiveState();
        if (Live.LiveSessionState.IDLE.equals(state)
                || Live.LiveSessionState.SCHEDULED.equals(state)
                || Live.LiveSessionState.FINISHED.equals(state)) {
            return true;
        }

        return false;
    }

    @Override
    public CTLConstant.UserIdentity getIdentity(){
        CtlSession session = getSession().ctlSession;
        String psType = TextUtils.isEmpty(session.psTypeInLesson)?
                session.psType : session.psTypeInLesson;
        return getUserIdentity(psType);
    }

    @Override
    protected CTLConstant.UserIdentity getIdentityInLesson() {
        String psTypeInLesson = getSession().ctlSession.psTypeInLesson;
        return getUserIdentity(psTypeInLesson);
    }

    @Override
    public boolean hasTeachingAbility() {
        CTLConstant.UserIdentity identity = getIdentity();
        CTLConstant.UserIdentity identityInLesson = getIdentityInLesson();
        return identity == CTLConstant.UserIdentity.LEAD
                || identity == CTLConstant.UserIdentity.ASSISTANT
                || identity == CTLConstant.UserIdentity.REMOTE_ASSISTANT
                || identityInLesson == CTLConstant.UserIdentity.LEAD
                || identityInLesson == CTLConstant.UserIdentity.ASSISTANT
                || identityInLesson == CTLConstant.UserIdentity.REMOTE_ASSISTANT;

    }

    @Override
    protected void switchStateWhenReceiveSyncState(String state) {
        super.switchStateWhenReceiveSyncState(state);
        //transitionTo(getStateBySession(state));
        sendTransitionMessage(getStateBySession(state));
    }

    @Override
    protected void dealReceiveSyncClassState(SyncClassStateReceive syncState) {
        if (syncState == null)
            return;

        if (XiaojsConfig.DEBUG) {
            Logger.d("SyncClassState...................");
        }

        CtlSession ctlSession = getSession().ctlSession;
        ctlSession.cls.state = syncState.to;

        if (Live.LiveSessionState.IDLE.equals(syncState.to)) {
            ctlSession.ctl = null;
        }else if (syncState.current != null
                && Live.LiveSessionState.PENDING_FOR_LIVE.equals(syncState.to)) {
            if(ctlSession.ctl == null || !ctlSession.ctl.id.equals(syncState.current.id)) {
                CtlSession.Ctl newCtl = new CtlSession.Ctl();
                newCtl.title = syncState.current.title;
                newCtl.id = syncState.current.id;
                newCtl.subtype = syncState.current.typeName;
                newCtl.duration = syncState.current.schedule.duration;
                //newCtl.startedOn = syncState.current.schedule.start.toGMTString();
                ctlSession.ctl = newCtl;
            }
        }

        if (syncState.volatiles != null && syncState.volatiles.length > 0) {

            String mid = AccountDataManager.getAccountID(getContext());
            if (!TextUtils.isEmpty(mid)) {
                for (SyncClassStateResponse.Volatiles volatil : syncState.volatiles) {
                    if (mid.equals(volatil.accountId)) {
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("============my pstyp changed=========");
                        }

                        ctlSession.psTypeInLesson = volatil.psType;

                        break;
                    }
                }
            }


        }


        sendTransitionMessage(getStateBySession(syncState.to));

    }

    private State getStateBySession(String sessionState) {
        State state = idleState;
        if (Live.LiveSessionState.IDLE.equals(sessionState)) {
            state = idleState;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(sessionState)) {
            state = pending4JoinState;
        } else if (Live.LiveSessionState.PENDING_FOR_LIVE.equals(sessionState)) {
            state = pending4LiveState;
        } else if (Live.LiveSessionState.LIVE.equals(sessionState)) {
            state = liveState;
        } else if (Live.LiveSessionState.SCHEDULED.equals(sessionState)) {
            state = scheduledState;
        }
        return state;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 定义状态类
    //
    class IdleState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("enter IdleState...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("eixt IdleState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {

            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.ClassChannel.ENTER_PENDING_4_LIVE_SATE:           //进入pending for live状态
                    transitionTo(pending4LiveState);
                    return HANDLED;
                case CTLConstant.ClassChannel.ENTER_SCHEDULED:                     //schdeule
                    transitionTo(scheduledState);
                    return HANDLED;
                case CTLConstant.ClassChannel.JOIN_AVAILABLE:                      //进入pending for join
                    transitionTo(pending4JoinState);
                    return HANDLED;
                case CTLConstant.ClassChannel.START_LIVE_SHOW:                     //开始直播秀
                    ClaimReponse claimReponse = (ClaimReponse) msg.obj;
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = claimReponse.publishUrl;
                    ctlSession.finishOn = claimReponse.finishOn;
                    transitionTo(liveShowState);
                    return HANDLED;
            }

            return NOT_HANDLED;
        }


    }

    class Pending4LiveState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("enter Pending4LiveState...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("eixt Pending4LiveState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {

            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.ClassChannel.JOIN_AVAILABLE:                      //进入pending for join状态
                    transitionTo(pending4JoinState);
                    return HANDLED;
                case CTLConstant.ClassChannel.START_LIVE_SHOW:                     //开始直播秀
                {
                    ClaimReponse claimReponse = (ClaimReponse) msg.obj;
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = claimReponse.publishUrl;
                    ctlSession.finishOn = claimReponse.finishOn;
                    transitionTo(liveShowState);
                    return HANDLED;
                }
                case CTLConstant.ClassChannel.START_LESSON:                        //开始上课
                    ClassResponse response = (ClassResponse) msg.obj;
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = response.publishUrl;
                    ctlSession.state = Live.LiveSessionState.LIVE;
                    transitionTo(liveState);
                    return HANDLED;
            }

            return NOT_HANDLED;
        }
    }

    class ScheduledState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("enter ScheduledState...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("exit ScheduledState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {

            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.ClassChannel.JOIN_AVAILABLE:                      //进入pending for jion状态
                    transitionTo(pending4JoinState);
                    return HANDLED;
                case CTLConstant.ClassChannel.START_LIVE_SHOW:                     //开始直播秀
                    ClaimReponse claimReponse = (ClaimReponse) msg.obj;
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = claimReponse.publishUrl;
                    ctlSession.finishOn = claimReponse.finishOn;
                    transitionTo(liveShowState);
                    return HANDLED;
            }

            return NOT_HANDLED;
        }
    }


    class Pending4JoinState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("enter Pending4JoinState...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("eixt Pending4JoinState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.ClassChannel.START_LESSON:                        //开始上课
                    ClassResponse response = (ClassResponse) msg.obj;
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = response.publishUrl;
                    ctlSession.state = Live.LiveSessionState.LIVE;
                    transitionTo(liveState);
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }


    class LiveState extends State {


        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("enter LiveState...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("eixt LiveState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.ClassChannel.FINISH_LESSON:                      //下课
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = null;
                    //如果下课，相应的1对1播放地址也要重制为空
                    ctlSession.playUrl = "";
                    ctlSession.streamType = Live.StreamType.NONE;
                    //TODO 处理csOfCurrent
                    //TODO 回到Pending4Live、IDLE。。。。。
                    transitionTo(getStateBySession(getLiveState()));
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    /**
     * 个人推流状态
     */
    class LiveShowState extends State {

        @Override
        public void enter() {
            getSession().liveShow = true;

            if (XiaojsConfig.DEBUG) {
                Logger.d("enter LiveShowState...");
            }
        }

        @Override
        public void exit() {
            getSession().liveShow = false;

            if (XiaojsConfig.DEBUG) {
                Logger.d("exit LiveShowState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.STOP_LIVE_SHOW:                      //停止直播秀
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("LiveShowState: received STOP_LIVE_SHOW");
                    }
                    RoomSession session = getSession();
                    session.ctlSession.publishUrl = "";
                    //如果停止直播秀，相应的1对1播放地址也要重制为空
                    session.ctlSession.playUrl = "";
                    session.ctlSession.finishOn = 0;
                    //回到之前状态
                    transitionTo(getStateBySession(getLiveState()));
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }


}
