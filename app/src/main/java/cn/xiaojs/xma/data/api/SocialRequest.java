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
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.FollowParam;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.model.social.Relation;
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

        enqueueRequest(APIType.GET_ACTIVITIES,
                call,
                pagination.getPage(),
                CollectionPage.class,
                Dynamic.class);


    }

    public void commentActivity(@NonNull String session,String activity,String commentContent) {

        Comment comment = new Comment();
        comment.comment = commentContent;

        Call<Comment> call = getService().commentActivity(session,activity,comment);
        enqueueRequest(APIType.COMMENT_ACTIVITY,call);
    }

    public void getComments(@NonNull String session, Criteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<Comment>> call = getService().getComments(session,
                criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_COMMENTS,call);
    }

    public void likeActivity(@NonNull String session,String activity) {
        Call<Dynamic.DynStatus> call = getService().likeActivity(session,activity);
        enqueueRequest(APIType.LIKE_ACTIVITY,call);
    }

    public void replyComment(@NonNull String session, String commentID, String reply) {

        Comment replyComment = new Comment();
        replyComment.reply = reply;

        Call<Comment> call = getService().replyComment(session,commentID,replyComment);
        enqueueRequest(APIType.REPLAY_COMMENT,call);
    }

    public void reply2Reply(@NonNull String session, String replyID, String reply) {

        Comment replyComment = new Comment();
        replyComment.reply = reply;

        Call<Comment> call = getService().reply2Reply(session,replyID,replyComment);
        enqueueRequest(APIType.REPLAY_2_REPLY,call);
    }

    public void getUpdates(@NonNull String session, Pagination pagination) {

        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(paginationJsonstr);
        }
        Call<CollectionPage<DynUpdate>> call = getService().getUpdates(session,paginationJsonstr);
        enqueueRequest(APIType.GET_UPDATES,call);
    }

    public void followContact(@NonNull String session, String contact, int group) {

        FollowParam param = new FollowParam();
        param.contact = contact;
        param.group = group;

        Call<Relation> call = getService().followContact(session,param);
        enqueueRequest(APIType.FOLLOW_CONTACT,call);
    }

    public void unfollowContact(@NonNull String session, String contact) {
        Call<ResponseBody> call = getService().unfollowContact(session,contact);
        enqueueRequest(APIType.UNFOLLOW_CONTACT,call);
    }

    public void getActivityDetails(@NonNull String session, String activity) {
        Call<DynamicDetail> call = getService().getActivityDetails(session,activity);
        enqueueRequest(APIType.GET_ACTIVITY_DETAILS,call);
    }

    public void getLikedRecords(@NonNull String session, Criteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }
        Call<CollectionPage<LikedRecord>> call = getService().getLikedRecords(session,
                criteriaJsonstr,
                paginationJsonstr);
        enqueueRequest(APIType.GET_LINKED_RECORDS,call);
    }

    public void getContacts(@NonNull String session){
        Call<ArrayList<ContactGroup>> call = getService().getContacts(session);
        enqueueRequest(APIType.GET_CONTACTS,call);
    }

}
