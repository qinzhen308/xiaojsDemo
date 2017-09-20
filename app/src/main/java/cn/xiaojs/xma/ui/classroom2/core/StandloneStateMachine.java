package cn.xiaojs.xma.ui.classroom2.core;

import android.content.Context;
import android.os.Message;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.State;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;

/**
 * Created by maxiaobao on 2017/7/4.
 * 公开课状态机
 */

public class StandloneStateMachine extends ClassroomStateMachine {

    private State defaultState = new DefaultState();
    private State scheduledState = new ScheduledState();
    private State pending4JoinState = new Pending4JoinState();
    private State liveState = new LiveState();
    private State delayedState = new DelayedState();
    private State restState = new RestState();
    private State finishState = new FinishedState();
    private State liveShowState = new LiveShowState();

    public StandloneStateMachine(Context context, RoomSession session) {
        super(context, "StandloneStateMachine", session);
        initStateTree();
        initialState();
    }

    public void initStateTree() {
        addState(defaultState);
        addState(scheduledState, defaultState);
        addState(pending4JoinState, defaultState);
        addState(finishState, defaultState);
        addState(liveState, defaultState);
        addState(liveShowState, defaultState);
        addState(delayedState, liveState);
        addState(restState, liveState);

    }


    public void initialState() {
        String state = getLiveState();
        State initState = getStateBySession(state);
        setInitialState(initState);
    }

    @Override
    public String getLiveState() {
        return getSession().ctlSession.state;
    }

    @Override
    protected String getTitle() {
        return getSession().ctlSession.ctl.title;
    }


    @Override
    protected boolean canForceIndividual() {
        return hasTeachingAbility();
    }

    @Override
    protected boolean canIndividualByState() {
        String state = getLiveState();
        if (Live.LiveSessionState.SCHEDULED.equals(state) ||
                Live.LiveSessionState.FINISHED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public CTLConstant.UserIdentity getIdentity() {
        CtlSession session = getSession().ctlSession;
        String psType = session.psType;
        return getUserIdentity(psType);
    }


    @Override
    protected CTLConstant.UserIdentity getIdentityInLesson() {
        return CTLConstant.UserIdentity.NONE;
    }


    @Override
    public boolean hasTeachingAbility() {
        CTLConstant.UserIdentity identity = getIdentity();
        return identity == CTLConstant.UserIdentity.LEAD
                || identity == CTLConstant.UserIdentity.ASSISTANT
                || identity == CTLConstant.UserIdentity.REMOTE_ASSISTANT;

    }

    @Override
    protected void switchStateWhenReceiveSyncState(String state) {
        super.switchStateWhenReceiveSyncState(state);
        sendTransitionMessage(getStateBySession(state));
    }

    private State getStateBySession(String sessionState) {
        State state = defaultState;
        if (Live.LiveSessionState.SCHEDULED.equals(sessionState)) {
            state = scheduledState;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(sessionState)) {
            state = pending4JoinState;
        } else if (Live.LiveSessionState.LIVE.equals(sessionState)) {
            state = liveState;
        } else if (Live.LiveSessionState.FINISHED.equals(sessionState)) {
            state = finishState;
        } else if (Live.LiveSessionState.RESET.equals(sessionState)) {
            state = restState;
        } else if (Live.LiveSessionState.DELAY.equals(sessionState)) {
            state = delayedState;
        }

        return state;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 处理event事件
    //
    protected void closeMedia(CloseMediaReceive message) {

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 定义状态类
    //

    class DefaultState extends State {

        @Override
        public boolean processMessage(Message msg) {
            return NOT_HANDLED;
        }
    }

    class ScheduledState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("ScheduledState enter...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("ScheduledState exit...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {

            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.JOIN_AVAILABLE:                      //进入pending for jion状态
                    transitionTo(pending4JoinState);
                    return true;
                case CTLConstant.StandloneChannel.START_LIVE_SHOW:                     //开始直播秀
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
                Logger.d("Pending4JoinState enter...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("Pending4JoinState exit...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.START_LESSON:                        //开始上课
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
                Logger.d("LiveState enter...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("LiveState exit...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.DELAY_LESSON:                       //拖堂
                    transitionTo(delayedState);
                    return HANDLED;
                case CTLConstant.StandloneChannel.PAUSE_LESSON:                       //课间休息
                {
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.state = Live.LiveSessionState.RESET;
                    ctlSession.publishUrl = null;
                    ctlSession.playUrl = null;
                    transitionTo(restState);
                    return HANDLED;
                }
                case CTLConstant.StandloneChannel.FINISH_LESSON:                      //下课
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = null;
                    //如果下课，相应的1对1播放地址也要重制为空
                    ctlSession.playUrl = "";
                    ctlSession.state = Live.LiveSessionState.FINISHED;
                    ctlSession.streamType = Live.StreamType.NONE;
                    //TODO 处理csOfCurrent
                    transitionTo(finishState);
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    class DelayedState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("DelayedState enter...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("DelayedState exit...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.FINISH_LESSON:                        //下课
                    CtlSession ctlSession = getSession().ctlSession;
                    ctlSession.publishUrl = null;
                    //如果下课，相应的1对1播放地址也要重制为空
                    ctlSession.playUrl = "";
                    ctlSession.state = Live.LiveSessionState.FINISHED;
                    ctlSession.streamType = Live.StreamType.NONE;

                    //TODO 处理csOfCurrent
                    transitionTo(finishState);
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    class RestState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("RestState enter...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("RestState exit...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.RESUME_LESSON:                        //恢复上课

                    CtlSession ctlSession = getSession().ctlSession;
                    ClassResponse response = (ClassResponse) msg.obj;
                    if (response!=null) {
                        ctlSession.publishUrl = response.publishUrl;
                    }
                    ctlSession.state = Live.LiveSessionState.LIVE;
                    transitionTo(liveState);
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    class FinishedState extends State {

        @Override
        public void enter() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("enter FinishedState...");
            }
        }

        @Override
        public void exit() {
            if (XiaojsConfig.DEBUG) {
                Logger.d("exit FinishedState...");
            }
        }

        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case CTLConstant.BaseChannel.TRANSITION_STATE:
                    transitionTo((State)msg.obj);
                    return HANDLED;
                case CTLConstant.StandloneChannel.START_LIVE_SHOW:                     //开始直播秀
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
