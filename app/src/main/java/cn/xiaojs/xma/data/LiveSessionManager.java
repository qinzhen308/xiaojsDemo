package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.LiveSessionRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.LiveSession.CtlSession;
import cn.xiaojs.xma.model.LiveSession.Ticket;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class LiveSessionManager extends DataManager {

    public void generateTicket(Context context, String cs, APIServiceCallback<Ticket> callback) {

        LiveSessionRequest request = new LiveSessionRequest(context,callback);
        request.generateTicket(cs);

    }


    public void bootSession(Context context,String ticket,APIServiceCallback<CtlSession> callback) {


        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session,callback)) {
            return;
        }

        LiveSessionRequest request = new LiveSessionRequest(context,callback);
        request.bootSession(session,ticket);
    }
}
