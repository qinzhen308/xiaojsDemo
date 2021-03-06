package cn.xiaojs.xma.data.api.service;

import java.util.List;

import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.live.ClassMode;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveSchedule;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.live.Ticket;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by maxiaobao on 2017/1/18.
 */

public interface Live2Service {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Live Sessions
    //Provides access to live session interfaces accessible to the Xiaojs client applications.

    //Generate Ticket
    @GET("/v1/live/ticket/{cs}")
    Observable<Ticket> generateTicket(@Path("cs") String cs);


    //Boot Session
    @Headers("Content-Type: application/json")
    @POST("/v1/live/{ticket}")
    Observable<CtlSession> bootSession(@Path("ticket") String ticket);

    //Get Talks
    @GET("/v1/live/{ticket}/talks/{criteria}/{pagination}")
    Observable<CollectionPage<TalkItem>> getTalks(@Path("ticket") String ticket,
                                            @Path("criteria") String criteria,
                                            @Path("pagination") String pagination);


    //Get Attendees
    @GET("/v1/live/{ticket}/attendees")
    Observable<LiveCollection<Attendee>> getAttendees(@Path("ticket") String ticket);

    //Begin Class
    @PATCH("/v1/live/{ticket}/begin")
    Observable<ClassResponse> beginClass(@Path("ticket") String ticket, @Body ClassMode mode);

    //Close Board
    @PATCH("/v1/live/{ticket}/boards/{board}")
    Observable<ResponseBody> closeBoard(@Path("ticket") String ticket, @Path("board") String board);

    //Get Boards
    @GET("/v1/live/{ticket}/boards/{criteria}/{pagination}")
    Observable<CollectionPage<BoardItem>> getBoards(@Path("ticket") String ticket,
                                              @Path("criteria") String criteria,
                                              @Path("pagination") String pagination);

    //Open Board
    @GET("/v1/live/{ticket}/boards/{board}")
    Observable<BoardItem> openBoard(@Path("ticket") String ticket, @Path("board") String board);

    //Pause Class
    @PATCH("/v1/live/{ticket}/pause")
    Observable<ResponseBody> pauseClass(@Path("ticket") String ticket);

    //Register Board
    @POST("/v1/live/{ticket}/boards")
    Observable<BoardItem> registerBoard(@Path("ticket") String ticket, @Body Board board);

    //Finish Class
    @PATCH("/v1/live/{ticket}/end")
    Observable<ResponseBody> finishClass(@Path("ticket") String ticket);

    //Resume Class
    @PATCH("/v1/live/{ticket}/resume")
    Observable<ClassResponse> resumeClass(@Path("ticket") String ticket, @Body ClassMode mode);


    //Get Live Schedule
    @GET("/v1/live/{ticket}/schedule/{criteria}/{pagination}")
    Observable<List<LiveSchedule>> getLiveSchedule(@Path("ticket") String ticket,
                                             @Path("criteria") String criteria,
                                             @Path("pagination") String pagination);


}
