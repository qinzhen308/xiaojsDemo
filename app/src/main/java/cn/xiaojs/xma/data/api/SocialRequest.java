package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.Dynamic;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/12/27.
 */

public class SocialRequest extends ServiceRequest {

    public SocialRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void addContactGroup(String sessionID,String name) {

        ContactGroup contactGroup = new ContactGroup();
        contactGroup.name = name;

        Call<ResponseBody> call = getService().addContactGroup(sessionID,contactGroup);
        enqueueRequest(APIType.ADD_CONTACT_GROUP,call);

    }

    public void postActivity(String session, DynPost dynPost) {
        Call<Dynamic> call = getService().postActivity(session,dynPost);
        enqueueRequest(APIType.POST_ACTIVITY,call);
    }

    public void getActivities(@NonNull String session,
                              Criteria criteria,
                              Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<Dynamic>> call = getService().getActivities(session,
                criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_ACTIVITIES,call);
    }

    public void commentActivity(@NonNull String session,String activity,String commentContent) {

        Comment comment = new Comment();
        comment.comment = commentContent;

        Call<Comment> call = getService().commentActivity(session,activity,comment);
        enqueueRequest(APIType.COMMENT_ACTIVITY,call);
    }

}
