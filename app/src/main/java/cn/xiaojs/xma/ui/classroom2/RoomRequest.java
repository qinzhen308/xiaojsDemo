package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;

import cn.xiaojs.xma.data.EventManager;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;

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

    public void claimStream(int mode, final EventCallback<ClaimReponse> callback) {
        EventManager.claimStreaming(context, mode, new EventCallback<ClaimReponse>() {
            @Override
            public void onSuccess(ClaimReponse claimReponse) {
                stateMachine.startLiveShow(claimReponse);
                callback.onSuccess(claimReponse);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                callback.onFailed(errorCode, errorMessage);
            }
        });
    }

    public void startStreaming(final EventCallback<EventResponse> callback) {
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

    public void stopStreaming(String csOfCurrent,
                              final EventCallback<StreamStoppedResponse> callback) {

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
}
