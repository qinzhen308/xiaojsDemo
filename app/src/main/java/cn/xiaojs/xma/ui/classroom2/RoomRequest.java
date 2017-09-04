package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.statemachine.IState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.ctl.FinishClassResponse;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.RequestShareboard;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/7/5.
 */

public class RoomRequest {

    private Context context;
    private ClassroomStateMachine stateMachine;

    public RoomRequest(Context context, ClassroomStateMachine stateMachine) {
        this.context = context;
        this.stateMachine = stateMachine;
    }

    protected void claimStream(int mode, final EventCallback<ClaimReponse> callback) {
        EventManager.claimStreaming(context, mode, new EventCallback<ClaimReponse>() {
            @Override
            public void onSuccess(ClaimReponse claimReponse) {
                stateMachine.getSession().individualStreamDuration = claimReponse.finishOn;
                stateMachine.startLiveShow(claimReponse);
                callback.onSuccess(claimReponse);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }

    protected void startStreaming(final EventCallback<EventResponse> callback) {
        EventManager.streamingStarted(context, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }

    protected void stopStreaming(final int streamType,
                              String csOfCurrent,
                              final EventCallback<StreamStoppedResponse> callback) {

        if (streamType == CTLConstant.StreamingType.PUBLISH_INDIVIDUAL) {
            stateMachine.getSession().individualStreamDuration = 0;
            stateMachine.stopLiveShow();
        }

        EventManager.streamingStopped(context, csOfCurrent, new EventCallback<StreamStoppedResponse>() {
            @Override
            public void onSuccess(StreamStoppedResponse streamStoppedResponse) {
                callback.onSuccess(streamStoppedResponse);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }

    protected void mediaFeedback(int mediaStatus,
                              final EventCallback<EventResponse> callback) {

        if (mediaStatus == Live.MediaStatus.READY) {
            stateMachine.getSession().one2one = true;
        } else {
            stateMachine.getSession().ctlSession.publishUrl = null;
            stateMachine.getSession().one2one = false;
        }

        EventManager.mediaFeedback(context, mediaStatus, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });

    }

    protected void openMedia(String to, final EventCallback<EventResponse> callback) {
        EventManager.openMedia(context, to, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                stateMachine.getSession().one2one = true;
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                stateMachine.getSession().one2one = false;
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }

    protected void closeMedia(String to, final EventCallback<CloseMediaResponse> callback) {
        EventManager.closeMedia(context, to, new EventCallback<CloseMediaResponse>() {
            @Override
            public void onSuccess(CloseMediaResponse closeMediaResponse) {
                stateMachine.getSession().one2one = false;
                callback.onSuccess(closeMediaResponse);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }

    protected void sendTalk(Talk talk,
                              final EventCallback<TalkResponse> callback) {

        EventManager.sendTalk(context, talk, new EventCallback<TalkResponse>() {
            @Override
            public void onSuccess(TalkResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });

    }

    protected void beginClass(String ticket, final APIServiceCallback<ClassResponse> callback) {
        LiveManager.beginClass(context, ticket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                if (XiaojsConfig.DEBUG) {
                    IState state = stateMachine.getCurrentState();
                    Logger.d("the current state is:%s", state);
                }
                stateMachine.startLesson(object);
                callback.onSuccess(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                callback.onFailure(errorCode, errorMessage);
            }
        });
    }

    protected void finishClass(String ticket, final APIServiceCallback<ResponseBody> callback){
        LiveManager.finishClass(context, ticket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                FinishClassResponse response = null;
                try {
                    response = ClassroomEngine.parseSocketBean(object.string(),FinishClassResponse.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
                stateMachine.finishLesson(response);
                callback.onSuccess(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                callback.onFailure(errorCode, errorMessage);
            }
        });
    }

    protected void resumeClass(String ticket, final APIServiceCallback<ClassResponse> callback){
        LiveManager.resumeClass(context, ticket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                stateMachine.resumeLesson(object);
                callback.onSuccess(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                callback.onFailure(errorCode, errorMessage);
            }
        });
    }


    protected void pauseClass(String ticket, final APIServiceCallback<ResponseBody> callback){
        LiveManager.pauseClass(context, ticket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                stateMachine.pauseLesson();
                callback.onSuccess(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                callback.onFailure(errorCode, errorMessage);
            }
        });
    }


    protected void shareboradFeedback(boolean accepted,String board,
                                 final EventCallback<EventResponse> callback) {

        if (!accepted) {
            stateMachine.getSession().shareboardData = null;
        }

        EventManager.shareboardFeedback(context, accepted, board, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });

    }

    public static void requestShareboard(Context context, RequestShareboard shareboard,
                                         final EventCallback<EventResponse> callback) {
        EventManager.requestShareboard(context, shareboard, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }


    protected void stopShareboard(String board, String[] targetIds,
                                      final EventCallback<EventResponse> callback) {


        EventManager.stopShareboard(context, board, targetIds, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

                callback.onFailed(errorCode, errorMessage);
            }
        });


    }


}
