package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;
import android.text.TextUtils;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.classroom2.state.IdleState;
import cn.xiaojs.xma.ui.classroom2.state.LiveState;
import cn.xiaojs.xma.ui.classroom2.state.Pending4LiveState;

/**
 * Created by maxiaobao on 2017/7/4.
 * 班级状态机
 */

public class ClassStateMachine extends ClassroomStateMachine{

    private IdleState idleState = new IdleState();
    private Pending4LiveState pending4LiveState = new Pending4LiveState();
    private LiveState liveState = new LiveState();

    public ClassStateMachine(Context context,RoomSession session) {
        super(context, "ClassStateMachine",session);
        initStateTree();
        initialState();
    }

    public void initStateTree() {
        addState(idleState);
        addState(pending4LiveState, idleState);
        addState(liveState, idleState);
    }

    public void initialState() {
        setInitialState(idleState);
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
        if (Live.LiveSessionState.IDLE.equals(state)) {
            return true;
        }

        return false;
    }

    @Override
    public CTLConstant.UserIdentity getIdentity(){
        CtlSession session = getSession().ctlSession;
        String psType = TextUtils.isEmpty(session.psTypeInLesson)? session.psType : session.psTypeInLesson;
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


}
