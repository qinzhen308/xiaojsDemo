package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.LiveRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.LiveSession.CtlSession;
import cn.xiaojs.xma.model.LiveSession.Ticket;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class LiveManager {

    public void generateTicket(Context context, String cs, APIServiceCallback<Ticket> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.generateTicket(cs);

    }


    public void bootSession(Context context,String ticket,APIServiceCallback<CtlSession> callback) {


        LiveRequest request = new LiveRequest(context,callback);
        request.bootSession(ticket);
    }
}
