package cn.xiaojs.xma.data;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.LiveRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ctl.FinishClassResponse;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.live.BoardSaveParams;
import cn.xiaojs.xma.model.live.ClassMode;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.LiveSchedule;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.live.Ticket;
import cn.xiaojs.xma.model.Pagination;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class LiveManager {

    /**
     * Generates a live ticket with the specified parameters.
     */
    public static void generateTicket(Context context,
                                      String cs,
                                      APIServiceCallback<Ticket> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.generateTicket(cs);

    }


    /**
     * Validates and boots a participant session.
     */
    public static LiveRequest bootSession(Context context,
                                          String ticket,
                                          APIServiceCallback<CtlSession> callback) {


        LiveRequest request = new LiveRequest(context, callback);
        request.bootSession(ticket);
        return request;
    }

    public static CtlSession bootSession2(Context context,
                                          String ticket) throws Exception {


        LiveRequest request = new LiveRequest(context, null);
        return request.bootSession2(ticket);
    }

    /**
     * Retrieves a collection of talks that match the specified criteria within a specific class.
     */
    public static void getTalks(Context context,
                                String ticket,
                                LiveCriteria criteria,
                                Pagination pagination,
                                APIServiceCallback<CollectionPage<TalkItem>> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.getTalks(ticket, criteria, pagination);
    }

    /**
     * Returns participants currently joined the specific live session.
     */
    public static void getAttendees(Context context,
                                    String ticket,
                                    boolean peekOnly,
                                    APIServiceCallback<LiveCollection<Attendee>> callback) {
        LiveRequest request = new LiveRequest(context, callback);
        request.getAttendees(ticket,peekOnly);
    }

    public static LiveCollection<Attendee> getAttendeesSync(Context context,
                                    String ticket,
                                    boolean peekOnly) throws IOException {
        LiveRequest request = new LiveRequest(context, null);
        return request.getAttendeesSync(ticket,peekOnly);
    }

    /**
     * Actually starts the scheduled live session by the teaching lead.
     */
    public static void beginClass(Context context,
                                  String ticket,
                                  APIServiceCallback<ClassResponse> callback) {
        ClassMode classMode = new ClassMode();
        classMode.mode = Live.StreamMode.NO;

        LiveRequest request = new LiveRequest(context, callback);
        request.beginClass(ticket, classMode);
    }

    /**
     * Actually starts the scheduled live session by the teaching lead.
     */
    public static void beginClass(Context context,
                                  String ticket,
                                  int mode,
                                  APIServiceCallback<ClassResponse> callback) {

        ClassMode classMode = new ClassMode();
        classMode.mode = mode;

        LiveRequest request = new LiveRequest(context, callback);
        request.beginClass(ticket, classMode);
    }

    /**
     * Closes an open board.
     */
    public static void closeBoard(Context context,
                                  String ticket,
                                  String board,
                                  APIServiceCallback<ResponseBody> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.closeBoard(ticket, board);

    }
    /**
     * delete an board.
     */
    public static void deleteBoard(Context context,
                                  String ticket,
                                  String board,
                                  APIServiceCallback<ResponseBody> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.deleteBoard(ticket, board);

    }

    /**
     * Retrieves a collection of boards accessible to the current attendee within a specific class.
     */
    public static void getBoards(Context context,
                                 String ticket,
                                 BoardCriteria criteria,
                                 Pagination pagination,
                                 APIServiceCallback<CollectionPage<BoardItem>> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.getBoards(ticket, criteria, pagination);

    }

    /**
     * Retrieves a board owned by the current attendee.
     */
    public static void openBoard(Context context,
                                 String ticket,
                                 String board,
                                 APIServiceCallback<BoardItem> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.openBoard(ticket, board);
    }

    /**
     * Pauses the live session to have a rest.
     */
    public static void pauseClass(Context context,
                                  String ticket,
                                  APIServiceCallback<ResponseBody> callback) {
        LiveRequest request = new LiveRequest(context, callback);
        request.pauseClass(ticket);
    }

    /**
     * Registers a board created during a class.
     */
    public static void registerBoard(Context context,
                                     String ticket,
                                     Board board,
                                     APIServiceCallback<BoardItem> callback) {
        LiveRequest request = new LiveRequest(context, callback);
        request.registerBoard(ticket, board);
    }
    /**
     * save the board.
     */
    public static void saveBoard(Context context,
                                     String ticket,
                                     String board,
                                     BoardSaveParams saving,
                                     APIServiceCallback<ResponseBody> callback) {
        LiveRequest request = new LiveRequest(context, callback);
        request.saveBoard(ticket, board,saving);
    }

    /**
     * Requests to finish the live session by the teaching lead.
     */
    public static void finishClass(Context context,
                                   String ticket,
                                   APIServiceCallback<ResponseBody> callback) {
        LiveRequest request = new LiveRequest(context, callback);
        request.finishClass(ticket);
    }

    /**
     * Manually resumes a paused live session.
     */
    public static void resumeClass(Context context,
                                   String ticket,
                                   APIServiceCallback<ClassResponse> callback) {
        ClassMode classMode = new ClassMode();
        classMode.mode = Live.StreamMode.NO;

        LiveRequest request = new LiveRequest(context, callback);
        request.resumeClass(ticket, classMode);
    }

    /**
     * Manually resumes a paused live session.
     */
    public static void resumeClass(Context context,
                                   String ticket,
                                   int mode,
                                   APIServiceCallback<ClassResponse> callback) {
        ClassMode classMode = new ClassMode();
        classMode.mode = mode;

        LiveRequest request = new LiveRequest(context, callback);
        request.resumeClass(ticket, classMode);
    }

    /**
     * Returns lessons scheduled for the current class, optionally filtered by criteria.
     */
    public static void getLiveSchedule(Context context,
                                       String ticket,
                                       Criteria criteria,
                                       Pagination pagination,
                                       APIServiceCallback<List<LiveSchedule>> callback) {

        LiveRequest request = new LiveRequest(context, callback);
        request.getLiveSchedule(ticket, criteria, pagination);
    }

}
