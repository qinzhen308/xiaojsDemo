package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.LiveRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.live.ClassMode;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
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

    /**
     * Actually starts the scheduled live session by the teaching lead.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void beginClass(Context context,
                                  String ticket,
                                  APIServiceCallback<ClassResponse> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.beginClass(ticket,null);
    }

    /**
     * Actually starts the scheduled live session by the teaching lead.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void beginClass(Context context,
                                  String ticket,
                                  int mode,
                                  APIServiceCallback<ClassResponse> callback) {

        ClassMode classMode = new ClassMode();
        classMode.mode = mode;

        LiveRequest request = new LiveRequest(context,callback);
        request.beginClass(ticket,classMode);
    }

    /**
     * Closes an open board.
     * @param context
     * @param ticket
     * @param board
     * @param callback
     */
    public static void closeBoard(Context context,
                                  String ticket,
                                  String board,
                                  APIServiceCallback<ResponseBody> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.closeBoard(ticket, board);

    }

    /**
     * Retrieves a collection of boards accessible to the current attendee within a specific class.
     * @param context
     * @param ticket
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getBoards(Context context,
                                 String ticket,
                                 BoardCriteria criteria,
                                 Pagination pagination,
                                 APIServiceCallback<CollectionPage<BoardItem>> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.getBoards(ticket, criteria, pagination);

    }

    /**
     * Retrieves a board owned by the current attendee.
     * @param context
     * @param ticket
     * @param board
     * @param callback
     */
    public static void openBoard(Context context,
                                 String ticket,
                                 String board,
                                 APIServiceCallback<BoardItem> callback) {

        LiveRequest request = new LiveRequest(context,callback);
        request.openBoard(ticket, board);
    }

    /**
     * Pauses the live session to have a rest.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void pauseClass(Context context,
                                  String ticket,
                                  APIServiceCallback<ResponseBody> callback) {
        LiveRequest request = new LiveRequest(context,callback);
        request.pauseClass(ticket);
    }

    /**
     * Registers a board created during a class.
     * @param context
     * @param ticket
     * @param board
     * @param callback
     */
    public static void registerBoard(Context context,
                                     String ticket,
                                     Board board,
                                     APIServiceCallback<BoardItem> callback) {
        LiveRequest request = new LiveRequest(context,callback);
        request.registerBoard(ticket, board);
    }

    /**
     * Requests to finish the live session by the teaching lead.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void finishClass(Context context,
                                     String ticket,
                                     APIServiceCallback<ClassResponse> callback) {
        LiveRequest request = new LiveRequest(context,callback);
        request.finishClass(ticket);
    }

    /**
     * Manually resumes a paused live session.
     * @param context
     * @param ticket
     * @param callback
     */
    public static void resumeClass(Context context,
                                   String ticket,
                                   APIServiceCallback<ClassResponse> callback) {
        LiveRequest request = new LiveRequest(context,callback);
        request.resumeClass(ticket, null);
    }

    /**
     * Manually resumes a paused live session.
     * @param context
     * @param ticket
     * @param mode
     * @param callback
     */
    public static void resumeClass(Context context,
                                   String ticket,
                                   int mode,
                                   APIServiceCallback<ClassResponse> callback) {
        ClassMode classMode = new ClassMode();
        classMode.mode = mode;

        LiveRequest request = new LiveRequest(context,callback);
        request.resumeClass(ticket, classMode);
    }

}
