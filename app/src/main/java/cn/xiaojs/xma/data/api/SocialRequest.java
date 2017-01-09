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

    public void addContactGroup(String name) {

        ContactGroup contactGroup = new ContactGroup();
        contactGroup.name = name;

        Call<ResponseBody> call = getService().addContactGroup(contactGroup);
        enqueueRequest(APIType.ADD_CONTACT_GROUP,call);

    }

    public void postActivity(DynPost dynPost) {
        Call<Dynamic> call = getService().postActivity(dynPost);
        enqueueRequest(APIType.POST_ACTIVITY,call);
    }

    public void getActivities(Criteria criteria,
                              Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<Dynamic>> call = getService().getActivities(criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_ACTIVITIES,
                call,
                pagination.getPage(),
                CollectionPage.class,
                Dynamic.class);


    }

    public void commentActivity(String activity,String commentContent) {

        Comment comment = new Comment();
        comment.comment = commentContent;

        Call<Comment> call = getService().commentActivity(activity,comment);
        enqueueRequest(APIType.COMMENT_ACTIVITY,call);
    }

    public void getComments(Criteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<CollectionPage<Comment>> call = getService().getComments(criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_COMMENTS,call);
    }

    public void likeActivity(String activity) {
        Call<Dynamic.DynStatus> call = getService().likeActivity(activity);
        enqueueRequest(APIType.LIKE_ACTIVITY,call);
    }

    public void replyComment(String commentID, String reply) {

        Comment replyComment = new Comment();
        replyComment.reply = reply;

        Call<Comment> call = getService().replyComment(commentID,replyComment);
        enqueueRequest(APIType.REPLAY_COMMENT,call);
    }

    public void reply2Reply(String replyID, String reply) {

        Comment replyComment = new Comment();
        replyComment.reply = reply;

        Call<Comment> call = getService().reply2Reply(replyID,replyComment);
        enqueueRequest(APIType.REPLAY_2_REPLY,call);
    }

    public void getUpdates(Pagination pagination) {

        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(paginationJsonstr);
        }
        Call<CollectionPage<DynUpdate>> call = getService().getUpdates(paginationJsonstr);
        enqueueRequest(APIType.GET_UPDATES,call);
    }

    public void followContact(String contact, int group) {

        FollowParam param = new FollowParam();
        param.contact = contact;
        param.group = group;

        Call<Relation> call = getService().followContact(param);
        enqueueRequest(APIType.FOLLOW_CONTACT,call);
    }

    public void unfollowContact(String contact) {
        Call<ResponseBody> call = getService().unfollowContact(contact);
        enqueueRequest(APIType.UNFOLLOW_CONTACT,call);
    }

    public void getActivityDetails(String activity) {
        Call<DynamicDetail> call = getService().getActivityDetails(activity);
        enqueueRequest(APIType.GET_ACTIVITY_DETAILS,call);
    }

    public void getLikedRecords(Criteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }
        Call<CollectionPage<LikedRecord>> call = getService().getLikedRecords(criteriaJsonstr,
                paginationJsonstr);
        enqueueRequest(APIType.GET_LINKED_RECORDS,call);
    }

    public void getContacts(){
        Call<ArrayList<ContactGroup>> call = getService().getContacts();
        enqueueRequest(APIType.GET_CONTACTS,call);
    }

}
