package cn.xiaojs.xma.data.api;

import android.content.Context;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.LiveSession.Attendee;
import cn.xiaojs.xma.model.LiveSession.CtlSession;
import cn.xiaojs.xma.model.LiveSession.LiveCollection;
import cn.xiaojs.xma.model.LiveSession.LiveCriteria;
import cn.xiaojs.xma.model.LiveSession.TalkItem;
import cn.xiaojs.xma.model.LiveSession.Ticket;
import cn.xiaojs.xma.model.Pagination;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class LiveRequest extends ServiceRequest{

    public LiveRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void generateTicket(String cs) {
        Call<Ticket> call = getLiveService().generateTicket(cs);
        enqueueRequest(APIType.GENERATE_TICKET,call);
    }
    public void bootSession(String ticket) {

        Call<CtlSession> call = getLiveService().bootSession(ticket);
        enqueueRequest(APIType.BOOT_SESSION,call);
    }

    public void getTalks(String ticket, LiveCriteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<TalkItem>> call = getLiveService().getTalks(ticket,
                criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_TALKS,call);

    }

    public void getAttendees(String ticket) {
        Call<LiveCollection<Attendee>> call = getLiveService().getAttendees(ticket);
        enqueueRequest(APIType.GET_ATTENDEES,call);
    }
}
