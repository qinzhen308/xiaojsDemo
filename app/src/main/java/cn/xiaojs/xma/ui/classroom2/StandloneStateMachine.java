package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;
import android.os.Message;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.State;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import cn.xiaojs.xma.ui.classroom2.CTLConstant.StandloneChannel;

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
        super(context,"StandloneStateMachine", session);
        initStateTree();
        initialState();
    }

    public void initStateTree() {
        addState(defaultState);
        addState(scheduledState, defaultState);
        addState(pending4JoinState, defaultState);
        addState(finishState,defaultState);
        addState(liveState,defaultState);
        addState(liveShowState,defaultState);
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

    private State getStateBySession(String sessionState) {
        State state = defaultState;
        if (Live.LiveSessionState.SCHEDULED.equals(sessionState)) {
            state = scheduledState;
        }else if(Live.LiveSessionState.PENDING_FOR_JOIN.equals(sessionState)) {
            state = pending4JoinState;
        }else if(Live.LiveSessionState.LIVE.equals(sessionState)) {
            state = liveState;
        }else if(Live.LiveSessionState.FINISHED.equals(sessionState)) {
            state = finishState;
        }else if(Live.LiveSessionState.RESET.equals(sessionState)) {
            state = restState;
        }else if(Live.LiveSessionState.DELAY.equals(sessionState)) {
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
        public boolean processMessage(Message msg) {

            switch (msg.what) {
                case StandloneChannel.JOIN_AVAILABLE:                      //进入pending for jion状态
                    return true;
            }

            //TODO 进入直播秀状态
            return NOT_HANDLED;
        }
    }

    class Pending4JoinState extends State{
        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case StandloneChannel.START_LESSON:                        //开始上课
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    class LiveState extends State{
        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case StandloneChannel.DELAY_LESSON:                       //拖堂
                    return HANDLED;
                case StandloneChannel.RESET_LESSON:                       //课间休息
                    return HANDLED;
                case StandloneChannel.FINISH_LESSON:                      //下课
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    class DelayedState extends State{
        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case StandloneChannel.START_LESSON:                        //开始上课
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }

    class RestState extends State {
        @Override
        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case StandloneChannel.START_LESSON:                        //开始上课
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
                case StandloneChannel.START_LESSON:                        //开始上课
                    return HANDLED;
                case StandloneChannel.START_LIVE_SHOW:                     //开始直播秀
                    transitionTo(liveShowState);
                    return HANDLED;
                case StandloneChannel.START_PLAY_LIVE_SHOW:                //开始播放直播秀
                    return HANDLED;
                case StandloneChannel.STOP_PLAY_LIVE_SHOW:                 //结束播放直播秀
                    //do nothing
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
                case StandloneChannel.STOP_LIVE_SHOW:                      //停止直播秀
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("LiveShowState: received STOP_LIVE_SHOW");
                    }
                    RoomSession session = getSession();
                    session.ctlSession.publishUrl = "";
                    session.ctlSession.finishOn = 0;
                    //回到之前状态
                    transitionTo(getStateBySession(getLiveState()));
                    return HANDLED;
            }
            return NOT_HANDLED;
        }
    }
}
