package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.LiveSession.CtlSession;
import cn.xiaojs.xma.model.LiveSession.Ticket;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class LiveRequest extends ServiceRequest{

    public LiveRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void generateTicket(String cs) {
        Call<Ticket> call = getService().generateTicket(cs);
        enqueueRequest(APIType.GENERATE_TICKET,call);
    }
    public void bootSession(String ticket) {

        Call<CtlSession> call = getService().bootSession(ticket);
        enqueueRequest(APIType.BOOT_SESSION,call);
    }
}
