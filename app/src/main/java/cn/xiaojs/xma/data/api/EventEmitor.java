package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.data.api.socket.SocketRequest;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.StreamMode;

/**
 * Created by maxiaobao on 2017/7/2.
 */

public class EventEmitor {

    /**
     * Claims to be on live before or after the scheduled class duration.
     * @param context
     * @param streamMode
     * @param callback
     */
    public static void claimStreaming(Context context,
                                      int streamMode,
                                      EventCallback<ClaimReponse> callback) {

        SocketManager socketManager = SocketManager.getSocketManager(context);
        SocketRequest<ClaimReponse> socketRequest = new SocketRequest<>(socketManager,callback);

        String event = Su.getEventSignature(Su.EventCategory.CLASSROOM,
                Su.EventType.CLAIM_STREAMING);
        StreamMode smode = new StreamMode();
        smode.mode = streamMode;

        socketRequest.emit(event, smode);

    }


}
