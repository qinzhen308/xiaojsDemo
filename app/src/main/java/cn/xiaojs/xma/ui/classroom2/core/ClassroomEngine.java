package cn.xiaojs.xma.ui.classroom2.core;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;


import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.xms.XMSEventObservable;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.live.BoardSaveParams;
import cn.xiaojs.xma.model.live.BoardSaveTitleParams;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.KickoutByLeftReceived;
import cn.xiaojs.xma.model.socket.SyncClassesReceived;
import cn.xiaojs.xma.model.socket.room.ChangeNotifyReceived;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaReceive;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.ClosePreviewReceive;
import cn.xiaojs.xma.model.socket.room.ConstraintKickoutReceive;
import cn.xiaojs.xma.model.socket.room.EmptyReceive;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.LogoutKickoutReceive;
import cn.xiaojs.xma.model.socket.room.MediaAbortedReceive;
import cn.xiaojs.xma.model.socket.room.MediaDeviceRefreshReceive;
import cn.xiaojs.xma.model.socket.room.MediaFeedbackReceive;
import cn.xiaojs.xma.model.socket.room.ModeSwitchReceive;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.ReclaimedReceive;
import cn.xiaojs.xma.model.socket.room.RequestShareboard;
import cn.xiaojs.xma.model.socket.room.ShareboardAckReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StopShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StreamExpirationReceive;
import cn.xiaojs.xma.model.socket.room.StreamQualityChangedReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public final class ClassroomEngine {

    private volatile static ClassroomEngine classroomEngine;

    private Context context;
    private RoomRequest roomRequest;
    private ClassroomStateMachine stateMachine;
    private EventObservable eventObservable;

    private EventListener eventListener;

    private Disposable classObserver;


    //此类可以不用单利模式，用单例主要是方便在不同Fragment中调用。
    public static ClassroomEngine getEngine() {

        if (classroomEngine == null) {
            synchronized (ApiManager.class) {
                if (classroomEngine == null) {
                    classroomEngine = new ClassroomEngine();
                }
            }
        }
        return classroomEngine;
    }

    public void init(Context context, String ticket, RoomSession session) {
        reset();

        this.context = context;
        session.ticket = ticket;

        ClassroomType ctype = session == null ? ClassroomType.Unknown : session.classroomType;
        if (ctype == ClassroomType.ClassLesson) {
            stateMachine = new ClassStateMachine(context, session);
        } else {
            stateMachine = new StandloneStateMachine(context, session);
        }
        stateMachine.start();

        eventObservable = EventObservable.createEventObservable();
        observerAllEvent();
        roomRequest = new RoomRequest(context, stateMachine);

        classObserver = XMSEventObservable.observeClassSession(context, classesConsumer);


    }

    /**
     * 销毁engine
     */
    public void destoryEngine() {
        reset();
        context = null;
        classroomEngine = null;
    }

    /**
     * 获取教室类型
     */
    public ClassroomType getClassroomType() {
        RoomSession roomSession = stateMachine.getSession();
        return roomSession == null ? ClassroomType.Unknown : roomSession.classroomType;
    }


    public int getClassJoin() {
        return stateMachine.getSession().ctlSession.cls.join;
    }

    public void setClassJoinMode(int join) {
        stateMachine.getSession().ctlSession.cls.join = join;
    }


    public ClassInfo getClassInfo() {
        return stateMachine.getSession().classInfo;
    }

    public void setClassInfo(ClassInfo info) {
        stateMachine.getSession().classInfo = info;
    }

    public boolean classManageable() {
        ClassInfo classInfo = getClassInfo();
        if (classInfo == null)
            return false;

        String mid = AccountDataManager.getAccountID(context);

        //创建者
        if (mid.equals(classInfo.owner.getId())) {
            return true;
        }

        //班主任
        if (classInfo.advisers != null && classInfo.advisers.length > 0) {
            for (Account account : classInfo.advisers) {
                if (mid.equals(account.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void observeSessionData(SessionDataObserver observer) {
        stateMachine.registesObserver(observer);
    }

    public void unObserveSessionData(SessionDataObserver observer) {
        stateMachine.unregistesObserver(observer);
    }

    public Context getContext() {
        return context;
    }

    public String getLiveState() {
        return stateMachine.getLiveState();
    }

    public CtlSession getCtlSession() {
        RoomSession roomSession = stateMachine.getSession();
        return roomSession.ctlSession;
    }

    public String getPublishUrl() {
        return stateMachine.getPublishUrl();
    }

    public String getPlayUrl() {
        return stateMachine.getPlayUrl();
    }

    public int getLiveMode() {
        return stateMachine.getLiveMode();
    }

    public String getCsOfCurrent() {
        return stateMachine.getSession().csOfCurrent;
    }

    public void setCsOfCurrent(String csOfCurrent) {
        stateMachine.getSession().csOfCurrent = csOfCurrent;
    }

    public boolean isPreview() {
        return stateMachine.getSession().preview;
    }

    public String getRoomTitle() {
        return stateMachine.getTitle();
    }

    public String getClassTitle() {
        return stateMachine.getClassTitle();
    }

    public boolean canForceIndividual() {
        return stateMachine.canForceIndividual();
    }

    public boolean canIndividualByState() {
        return stateMachine.canIndividualByState();
    }

    public CTLConstant.UserIdentity getIdentityInLesson() {
        return stateMachine.getIdentityInLesson();
    }

    public CTLConstant.UserIdentity getIdentity() {
        return stateMachine.getIdentity();
    }

    public CTLConstant.UserIdentity getUserIdentity(String psType) {
        return stateMachine.getUserIdentity(psType);
    }

    public CTLConstant.UserIdentity getChatIdentity(String accountId) {

        Attendee attendee = getMember(accountId);
        if (attendee == null) {
            return CTLConstant.UserIdentity.NONE;
        }

        String realType = TextUtils.isEmpty(attendee.psTypeInLesson) ?
                attendee.psType : attendee.psTypeInLesson;

        return getUserIdentity(realType);

    }


    //获取班主任的信息，可能为空
    public Attendee getClassAdviser() {
        return stateMachine.getSession().adviser;
    }

    //获取班级中的当前课的老师信息，可能为空
    public CtlSession.CtlLead getCurrentLessonLead() {
        return getCtlSession().ctl == null ? null : getCtlSession().ctl.lead;
    }

    public boolean isVistor() {
        return getCtlSession().visitor == null ? false : true;
    }

    public CtlSession.Visitor getVistor() {
        return getCtlSession().visitor;
    }

    public boolean hasTeachingAbility() {
        return stateMachine.hasTeachingAbility();
    }

    public boolean one2one() {
        return stateMachine.getSession().one2one;
    }

    public void setOne2one(boolean one2one) {
        stateMachine.getSession().one2one = one2one;
    }

    public String getTicket() {
        return stateMachine.getSession().ticket;
    }

    public boolean liveShow() {
        return stateMachine.getSession().liveShow;
    }

    public long getIndividualStreamDuration() {
        return stateMachine.getSession().individualStreamDuration;
    }

    public Attendee getMember(String accountId) {
        return stateMachine.getMember(accountId);
    }

    public Map<String, Attendee> getMembers() {
        return stateMachine.getSession().classMembers;
    }

    public int getOnlineMemberCount() {
        int count = 0;

        Map<String, Attendee> membersMap = getMembers();
        if (membersMap != null && membersMap.size() > 0) {

            for (Attendee attendee : membersMap.values()) {
                if (attendee.xa != 0) {
                    count++;
                }
            }

        }
        return count;
    }



    /**
     * 请求开始直播秀
     */
    public void claimStream(int mode, final EventCallback<ClaimReponse> callback) {
        if (roomRequest != null) {
            roomRequest.claimStream(mode, callback);
        }
    }

    /**
     * 开始推流
     */
    public void startStreaming(final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.startStreaming(callback);
        }
    }

    /**
     * 停止推流
     */
    public void stopStreaming(int streamType, String csOfCurrent,
                              final EventCallback<StreamStoppedResponse> callback) {
        if (roomRequest != null) {
            roomRequest.stopStreaming(streamType, csOfCurrent, callback);
        }
    }


    /**
     * 发送申请一对一
     */
    public void openMedia(String to, final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.openMedia(to, callback);
        }
    }

    /**
     * 关闭1对1
     */
    public void closeMedia(String to, final EventCallback<CloseMediaResponse> callback) {
        if (roomRequest != null) {
            roomRequest.closeMedia(to, callback);
        }
    }

    /**
     * 一对一推流时候，推流成功后发送的socket事件
     */
    public void mediaFeedback(int mediaStatus,
                              final EventCallback<EventResponse> callback) {


        if (roomRequest != null) {
            roomRequest.mediaFeedback(mediaStatus, callback);
        }
    }


    /**
     * 发送是否接受白板协作反馈
     */
    public void shareboardAck(String to, boolean accept, String board,
                              final EventCallback<EventResponse> callback) {


        if (roomRequest != null) {
            roomRequest.shareboradFeedback(to, accept, board, callback);
        }
    }


    /**
     * 请求白板协作
     */
    public void requestShareboard(RequestShareboard shareboard,
                                  EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.requestShareboard(context, shareboard, callback);
        }
    }


    /**
     * 停止白板协作
     */
    public void stopShareboard(String board,
                               String[] to, final EventCallback<EventResponse> callback) {

        if (roomRequest != null) {
            roomRequest.stopShareboard(board, to, callback);
        }
    }

    /**
     * 上传白板同步数据
     */
    public void syncBoard(String data, final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.syncBoard(data, callback);
        }
    }

    /**
     * 发送交流
     */
    public void sendTalk(Talk talk, final EventCallback<TalkResponse> callback) {
        if (roomRequest != null) {
            roomRequest.sendTalk(talk, callback);
        }
    }

    /**
     * 开始上课
     */
    public void beginClass(String ticket, final APIServiceCallback<ClassResponse> callback) {
        if (roomRequest != null) {
            roomRequest.beginClass(ticket, callback);
        }
    }

    /**
     * 下课
     */
    public void finishClass(String ticket, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            roomRequest.finishClass(ticket, callback);
        }
    }

    /**
     * 恢复上课
     */
    public void resumeClass(String ticket, final APIServiceCallback<ClassResponse> callback) {
        if (roomRequest != null) {
            roomRequest.resumeClass(ticket, callback);
        }
    }

    /**
     * 暂停上课
     */
    public void pauseClass(String ticket, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            roomRequest.pauseClass(ticket, callback);
        }
    }

    public void getMembers(String ticket, APIServiceCallback<LiveCollection<Attendee>> callback) {
        if (roomRequest != null) {
            roomRequest.getMembers(ticket, callback);
        }
    }


    public static <T> T parseSocketBean(Object obj, Class<T> valueType) {
        if (obj == null) {
            return null;
        }

        try {
            String result = null;
            if (obj instanceof JSONObject) {
                result = obj.toString();
            } else if (obj instanceof String) {
                result = (String) obj;
            } else {
                result = obj.toString();
            }

            if (TextUtils.isEmpty(result)) {
                return null;
            }


            if (XiaojsConfig.DEBUG) {
                Logger.d("socket callback: " + result);
            }


            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Event Observable
    //

    public EventListener.Syncboard observerSyncboard(Consumer<EventReceived> consumer) {
        EventListener.Syncboard syncboard = new EventListener.Syncboard(context);
        eventObservable.eventListener(syncboard)
                .doAfterNext(new Consumer<EventReceived>() {
                    @Override
                    public void accept(EventReceived eventReceived) throws Exception {
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("Syncboard doAfterNext acceptted");
                        }
                        switch (eventReceived.eventType) {
                            case Su.EventType.SYNC_BOARD:
                                SyncboardHelper.handleSyncEvent((SyncBoardReceive) eventReceived.t);
                                break;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

        return syncboard;
    }


    public EventListener.ELIdle observerIdle(Consumer<EventReceived> consumer) {
        EventListener.ELIdle elIdle = new EventListener.ELIdle(context);
        eventObservable.eventListener(elIdle)
                .doAfterNext(new Consumer<EventReceived>() {
                    @Override
                    public void accept(EventReceived eventReceived) throws Exception {

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("observerIdle doAfterNext acceptted");
                        }

                        switch (eventReceived.eventType) {
                            case Su.EventType.SHARE_BOARD:
                                SyncboardHelper.handleShareBoardData(
                                        (ShareboardReceive) eventReceived.t);
                                break;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return elIdle;
    }


    public EventListener.ELPlaylive observerPlaylive(Consumer<EventReceived> consumer) {
        EventListener.ELPlaylive playlive = new EventListener.ELPlaylive(context);
        eventObservable.eventListener(playlive)
                .doAfterNext(new Consumer<EventReceived>() {
                    @Override
                    public void accept(EventReceived eventReceived) throws Exception {

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("playlive doAfterNext acceptted");
                        }

                        switch (eventReceived.eventType) {
                            case Su.EventType.SHARE_BOARD:
                                SyncboardHelper.handleShareBoardData(
                                        (ShareboardReceive) eventReceived.t);
                                break;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return playlive;
    }

    public EventListener.ELLiving observerLiving(Consumer<EventReceived> consumer) {
        EventListener.ELLiving living = new EventListener.ELLiving(context);
        eventObservable.eventListener(living)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return living;
    }

    public EventListener.ELPVideoControl observerPVControl(Consumer<EventReceived> consumer) {
        EventListener.ELPVideoControl pVideoControl = new EventListener.ELPVideoControl(context);
        eventObservable.eventListener(pVideoControl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return pVideoControl;
    }

    public EventListener.ELLiveControl observerLiveControl(Consumer<EventReceived> consumer) {
        EventListener.ELLiveControl elLiveControl = new EventListener.ELLiveControl(context);
        eventObservable.eventListener(elLiveControl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return elLiveControl;
    }

    public EventListener.ELTalk observerTalk(Consumer<EventReceived> consumer) {
        EventListener.ELTalk elTalk = new EventListener.ELTalk(context);
        eventObservable.eventListener(elTalk)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return elTalk;
    }

    public EventListener.ELMember observerMember(Consumer<EventReceived> consumer) {
        EventListener.ELMember elMember = new EventListener.ELMember(context);
        eventObservable.eventListener(elMember)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return elMember;
    }


    public EventListener.ELContact observerContact(Consumer<EventReceived> consumer) {
        EventListener.ELContact elContact = new EventListener.ELContact(context);
        eventObservable.eventListener(elContact)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return elContact;
    }

    public EventListener.ELRoom observerRoom(Consumer<EventReceived> consumer) {
        EventListener.ELRoom elRoom = new EventListener.ELRoom(context);
        eventObservable.eventListener(elRoom)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        return elRoom;
    }

    public void addLiveTimerObserver(LiveTimerObserver.OnTimeChangedListener listener) {
        stateMachine.getLiveTimerObserver().addOnTimeChangedListener(listener);
    }

    public void removeLiveTimerObserver(LiveTimerObserver.OnTimeChangedListener listener) {
        stateMachine.getLiveTimerObserver().removeOnTimeChangedListener(listener);
    }

    public void stopLiveTimerObserver() {
        stateMachine.getLiveTimerObserver().stopObserverNow();
    }


    /**
     * 注册白板
     */
    public void registerBoard(String ticket, Board board, final APIServiceCallback<BoardItem> callback) {
        if (roomRequest != null) {
            roomRequest.registerBoard(ticket, board, callback);
        }
    }

    /**
     * 保存白板
     */
    public void saveBoard(String board, BoardSaveParams saving, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            roomRequest.saveBoard(getTicket(), board, saving, callback);
        }
    }

    /**
     * 重命名白板
     */
    public void renameBoard(String board, String title, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            BoardSaveTitleParams saving = new BoardSaveTitleParams();
            saving.title = title;
            saving.draft="null";
            roomRequest.renameBoard(getTicket(), board, saving, callback);
        }
    }

    /**
     * 关闭白板
     */
    public void closeBoard(String board, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            roomRequest.closeBoard(getTicket(), board, callback);
        }
    }

    /**
     * 删除白板
     */
    public void deleteBoard(String board, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            roomRequest.deleteBoard(getTicket(), board, callback);
        }
    }

    /**
     * 打开白板
     */
    public void openBoard(String ticket, String board, final APIServiceCallback<BoardItem> callback) {
        if (roomRequest != null) {
            roomRequest.openBoard(ticket, board, callback);
        }
    }

    /**
     * 白板管理列表数据
     */
    public void getBoards(BoardCriteria criteria, Pagination pagination, APIServiceCallback<CollectionPage<BoardItem>> callback) {
        if (roomRequest != null) {
            roomRequest.getBoards(getTicket(), criteria, pagination, callback);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ClassroomEngine() {
        //此处是Activity的context，此session的生命周期要与该activity保持一致
        //退出教室时，需要销毁

    }

    private void reset() {

        if (eventListener != null) {
            eventListener.dispose();
        }

        if (classObserver != null) {
            classObserver.dispose();
        }

        roomRequest = null;
        if (stateMachine != null) {
            stateMachine.getLiveTimerObserver().destoryObserver();
            stateMachine.destoryAndQuitNow();
            stateMachine = null;
        }
    }

    private void observerAllEvent() {
        eventListener = new EventListener(context);
        eventObservable.eventListener(eventListener)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EventReceived>() {
                    @Override
                    public void accept(EventReceived eventReceived) throws Exception {

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("ClassroomEngine received eventType:%d",
                                    eventReceived.eventType);
                        }

                        updateState(eventReceived);
                    }
                });

    }

    private Consumer<EventReceived> classesConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {
            if (XiaojsConfig.DEBUG) {
                Logger.d("receivedConsumer .....");
            }

            switch (eventReceived.eventType) {
                case Su.EventType.SYNC_CLASSES:
                    SyncClassesReceived classesReceived = (SyncClassesReceived) eventReceived.t;
                    if (classesReceived == null
                            || classesReceived.changes == null
                            || classesReceived.changes.length > 0) {
                        stateMachine.syncClasses(classesReceived);
                    }
                    break;
            }
        }
    };


    protected void updateState(EventReceived eventReceived) {
        switch (eventReceived.eventType) {
            case Su.EventType.CLOSE_MEDIA:
                stateMachine.closeMedia((CloseMediaReceive) eventReceived.t);
                break;
            case Su.EventType.STREAMING_STOPPED:
                stateMachine.streamStopped((StreamStopReceive) eventReceived.t);
                break;
            case Su.EventType.STREAMING_STARTED:
                stateMachine.streamStartted((StreamStartReceive) eventReceived.t);
                break;
            case Su.EventType.STREAM_RECLAIMED:
                stateMachine.streamReclaimed((ReclaimedReceive) eventReceived.t);
                break;
            case Su.EventType.STOP_STREAM_BY_EXPIRATION:
                stateMachine.streamExpiration((StreamExpirationReceive) eventReceived.t);
                break;
            case Su.EventType.CLASS_MODE_SWITCH:
                stateMachine.modeSwitch((ModeSwitchReceive) eventReceived.t);
                break;
            case Su.EventType.CLOSE_PREVIEW_BY_CLASS_OVER:
                stateMachine.closePreviewByClassover((ClosePreviewReceive) eventReceived.t);
                break;
            case Su.EventType.REMIND_FINALIZATION:
                stateMachine.remindFinal((EmptyReceive) eventReceived.t);
                break;
            case Su.EventType.JOIN:
                stateMachine.join((Attendee) eventReceived.t);
                break;
            case Su.EventType.LEAVE:
                stateMachine.leave((Attendee) eventReceived.t);
                break;
            case Su.EventType.KICK_OUT_BY_CONSTRAINT:
                stateMachine.constraintKickout((ConstraintKickoutReceive) eventReceived.t);
                break;
            case Su.EventType.KICK_OUT_BY_LOGOUT:
                stateMachine.logoutKickout((LogoutKickoutReceive) eventReceived.t);
                break;
            case Su.EventType.KICKOUT_BY_LEFT:
                stateMachine.kickoutByLeft((KickoutByLeftReceived) eventReceived.t);
                break;
            case Su.EventType.MEDIA_ABORTED:
                stateMachine.mediaAborted((MediaAbortedReceive) eventReceived.t);
                break;
            case Su.EventType.MEDIA_DEVICES_REFRESHED:
                stateMachine.mediaDeviceRefresh((MediaDeviceRefreshReceive) eventReceived.t);
                break;
            case Su.EventType.MEDIA_FEEDBACK:
                stateMachine.mediaFeedback((MediaFeedbackReceive) eventReceived.t);
                break;
            case Su.EventType.OPEN_MEDIA:
                stateMachine.openMeidaRecevied((OpenMediaReceive) eventReceived.t);
                break;
            case Su.EventType.REFRESH_STREAMING_QUALITY:
                stateMachine.streamQualityChangedRecevied(
                        (StreamQualityChangedReceive) eventReceived.t);
                break;
            case Su.EventType.SYNC_CLASS_STATE:
                stateMachine.syncClassStateRecevied((SyncClassStateReceive) eventReceived.t);
                break;
            case Su.EventType.SYNC_STATE:
                stateMachine.syncStateRecevied((SyncStateReceive) eventReceived.t);
                break;
            case Su.EventType.TALK:
                stateMachine.talkRecevied((Talk) eventReceived.t);
                break;
            case Su.EventType.SHARE_BOARD_ACK:
                stateMachine.shareboardAckReceived((ShareboardAckReceive) eventReceived.t);
                break;
            case Su.EventType.STOP_SHARE_BOARD:
                stateMachine.stopShareboardReceived((StopShareboardReceive) eventReceived.t);
                break;
            case Su.EventType.SHARE_BOARD:
                stateMachine.shareboardReceived((ShareboardReceive) eventReceived.t);
                break;
            case Su.EventType.SYNC_BOARD:
                stateMachine.syncBoardReceived((SyncBoardReceive) eventReceived.t);
                break;
        }
    }

}
