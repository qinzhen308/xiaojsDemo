package cn.xiaojs.xma.data;

import android.content.Context;

import java.util.List;

import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.CommunicationRequest;
import cn.xiaojs.xma.data.api.LiveRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
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

/**
 * Created by maxiaobao on 2017/1/9.
 */

public class CommunicationManager {


    /**
     * Retrieves a collection of talks that match the specified criteria within a specific class.
     */
    public static void getTalks(Context context,
                                LiveCriteria criteria,
                                Pagination pagination,
                                APIServiceCallback<CollectionPage<TalkItem>> callback) {

        CommunicationRequest request = new CommunicationRequest(context, callback);
        request.getTalks2(criteria, pagination);
    }
}
