package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.LiveRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.live.Ticket;
import cn.xiaojs.xma.model.Pagination;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class LiveManager {

    /**
     * Generates a live ticket with the specified parameters.
     * @param context
     * @param cs
     * @param callback
     */
    public static void generateTicket(Context context,
                                      String cs,
                                      APIServiceCallback<Ticket> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.generateTicket(cs);

    }


    /**
     * Validates and boots a participant session.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void bootSession(Context context,
                                   String ticket,
                                   APIServiceCallback<CtlSession> callback) {


        LiveRequest request = new LiveRequest(context,callback);
        request.bootSession(ticket);
    }

    /**
     * Retrieves a collection of talks that match the specified criteria within a specific class.
     * @param context
     * @param ticket
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getTalks(Context context,
                         String ticket,
                         LiveCriteria criteria,
                         Pagination pagination,
                         APIServiceCallback<CollectionPage<TalkItem>> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.getTalks(ticket,criteria,pagination);
    }

    /**
     * Returns participants currently joined the specific live session.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void getAttendees(Context context,
                                    String ticket,
                                    APIServiceCallback<LiveCollection<Attendee>> callback) {
        LiveRequest request = new LiveRequest(context,callback);
        request.getAttendees(ticket);
    }
}
