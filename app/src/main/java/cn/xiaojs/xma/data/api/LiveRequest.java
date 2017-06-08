package cn.xiaojs.xma.data.api;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
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

    public void beginClass(String ticket, ClassMode mode) {

        Call<ResponseBody> call = getLiveService().beginClass(ticket, mode);
        enqueueRequest(APIType.BEGIN_CLASS, call);
    }

    public void closeBoard(String ticket, String board) {
        Call<ResponseBody> call = getLiveService().closeBoard(ticket, board);
        enqueueRequest(APIType.CLOSE_BOARD,call);
    }

    public void getBoards(String ticket, BoardCriteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<BoardItem>> call = getLiveService().getBoards(ticket,
                criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_BOARDS, call);
    }

    public void openBoard(String ticket, String board) {
        Call<BoardItem> call = getLiveService().openBoard(ticket, board);
        enqueueRequest(APIType.OPEN_BOARD, call);
    }

    public void pauseClass(String ticket) {
        Call<ResponseBody> call = getLiveService().pauseClass(ticket);
        enqueueRequest(APIType.PAUSE_CLASS, call);
    }

    public void registerBoard(String ticket, Board board) {
        Call<BoardItem> call = getLiveService().registerBoard(ticket, board);
        enqueueRequest(APIType.REGISTER_BOARD, call);
    }

    public void finishClass(String ticket) {
        Call<ResponseBody> call = getLiveService().finishClass(ticket);
        enqueueRequest(APIType.FINISH_CLASS,call);
    }

    public void resumeClass(String ticket, ClassMode mode) {

        Call<ClassResponse> call = getLiveService().resumeClass(ticket,mode);
        enqueueRequest(APIType.RESUME_CLASS,call);
    }

    public void getLiveSchedule(String ticket, Criteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }
        Call<List<LiveSchedule>> call = getLiveService().getLiveSchedule(ticket,
                criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_LIVE_SCHEDULE,call);
    }
}
