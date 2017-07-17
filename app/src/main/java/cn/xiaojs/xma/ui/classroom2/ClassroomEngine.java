package cn.xiaojs.xma.ui.classroom2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.ctl.FinishClassResponse;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;


/**
 * Created by maxiaobao on 2017/7/3.
 */

public class ClassroomEngine {

    private volatile static ClassroomEngine classroomEngine;

    private Context context;
    private RoomRequest roomRequest;
    private ClassroomStateMachine stateMachine;

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
            stateMachine = new ClassStateMachine(context,session);
        } else {
            stateMachine = new StandloneStateMachine(context, session);
        }
        stateMachine.start();

        roomRequest = new RoomRequest(context, stateMachine);

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
     * 添加事件监听
     */
    public void addEvenListener(EventListener listener) {
        if (stateMachine != null) {
            stateMachine.addEventListener(listener);
        }
    }

    /**
     * 注销事件监听
     */
    public void removeEvenListener(EventListener listener) {
        if (stateMachine != null) {
            stateMachine.removeEventListener(listener);
        }
    }

    /**
     * 获取教室类型
     */
    public ClassroomType getClassroomType() {
        RoomSession roomSession= stateMachine.getSession();
        return roomSession == null ? ClassroomType.Unknown : roomSession.classroomType;
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

    public String getRoomTitle() {
        return stateMachine.getTitle();
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
        return  stateMachine.getSession().individualStreamDuration;
    }


    /**
     * 请求开始直播秀
     * @param mode
     * @param callback
     */
    public void claimStream(int mode, final EventCallback<ClaimReponse> callback) {
        if (roomRequest != null) {
            roomRequest.claimStream(mode, callback);
        }
    }

    /**
     * 开始推流
     * @param callback
     */
    public void startStreaming(final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.startStreaming(callback);
        }
    }

    /**
     * 停止推流
     * @param csOfCurrent
     * @param callback
     */
    public void stopStreaming(int streamType, String csOfCurrent,
                              final EventCallback<StreamStoppedResponse> callback) {
        if (roomRequest != null) {
            roomRequest.stopStreaming(streamType, csOfCurrent, callback);
        }
    }


    /**
     * 发送申请一对一
     * @param to
     * @param callback
     */
    public void openMedia(String to, final EventCallback<EventResponse> callback) {
        if (roomRequest != null) {
            roomRequest.openMedia(to, callback);
        }
    }

    /**
     * 关闭1对1
     * @param to
     * @param callback
     */
    public void closeMedia(String to, final EventCallback<CloseMediaResponse> callback) {
        if (roomRequest != null) {
            roomRequest.closeMedia(to, callback);
        }
    }

    /**
     * 一对一推流时候，推流成功后发送的socket事件
     * @param mediaStatus
     * @param callback
     */
    public void mediaFeedback(int mediaStatus,
                              final EventCallback<EventResponse> callback) {


        if (roomRequest != null) {
            roomRequest.mediaFeedback(mediaStatus, callback);
        }
    }

    /**
     * 发送交流
     * @param talk
     * @param callback
     */
    public void sendTalk(Talk talk,
                              final EventCallback<TalkResponse> callback) {
        if (roomRequest != null) {
            roomRequest.sendTalk(talk,callback);
        }
    }

    /**
     * 开始上课
     * @param ticket
     * @param callback
     */
    public void beginClass(String ticket, final APIServiceCallback<ClassResponse> callback) {
        if (roomRequest != null) {
            roomRequest.beginClass(ticket, callback);
        }
    }

    /**
     * 下课
     * @param ticket
     * @param callback
     */
    public void finishClass(String ticket, final APIServiceCallback<ResponseBody> callback) {
        if (roomRequest != null) {
            roomRequest.finishClass(ticket, callback);
        }
    }

    /**
     * 恢复上课
     * @param ticket
     * @param callback
     */
    public void resumeClass(String ticket, final APIServiceCallback<ClassResponse> callback) {
        if (roomRequest != null) {
            roomRequest.resumeClass(ticket, callback);
        }
    }

    /**
     * 暂停上课
     * @param ticket
     * @param callback
     */
    public void pauseClass(String ticket, final APIServiceCallback<ResponseBody> callback){
        if (roomRequest != null) {
            roomRequest.pauseClass(ticket, callback);
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

    private ClassroomEngine() {
        //此处是Activity的context，此session的生命周期要与该activity保持一致
        //退出教室时，需要销毁

    }

    private void reset() {
        roomRequest = null;
        if (stateMachine != null) {
            stateMachine.destoryAndQuitNow();
            stateMachine = null;
        }
    }

}
