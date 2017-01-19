package cn.xiaojs.xma.data.api.service;

import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.live.Ticket;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by maxiaobao on 2017/1/18.
 */

public interface LiveService {

    String SERVICE_PORT = "3004";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Live Sessions
    //Provides access to live session interfaces accessible to the Xiaojs client applications.

    //Generate Ticket
    @GET("/v1/live/ticket/{cs}")
    Call<Ticket> generateTicket(@Path("cs") String cs);


    //Boot Session
    @Headers("Content-Type: application/json")
    @POST("/v1/live/{ticket}")
    Call<CtlSession> bootSession(@Path("ticket") String ticket);

    //Get Talks
    @GET("/v1/live/{ticket}/talks/{criteria}/{pagination}")
    Call<CollectionPage<TalkItem>> getTalks(@Path("ticket") String ticket,
                                            @Path("criteria") String criteria,
                                            @Path("pagination") String pagination);


    //Get Attendees
    @GET("/v1/live/{ticket}/attendees")
    Call<LiveCollection<Attendee>> getAttendees(@Path("ticket") String ticket);
}