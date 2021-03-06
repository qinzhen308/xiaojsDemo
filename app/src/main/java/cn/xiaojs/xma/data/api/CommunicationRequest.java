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
import cn.xiaojs.xma.model.Pagination;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class CommunicationRequest extends ServiceRequest{

    public CommunicationRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void getTalks2(LiveCriteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<TalkItem>> call
                = getService().getTalks(criteriaJsonstr, paginationJsonstr);

        enqueueRequest(APIType.GET_TALKS,call);

    }
}
