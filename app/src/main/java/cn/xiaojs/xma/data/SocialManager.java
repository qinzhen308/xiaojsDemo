package cn.xiaojs.xma.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Xu;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.SocialRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.data.db.CursorTaskLoader;
import cn.xiaojs.xma.data.db.LoadDataCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.account.UpTokenParam;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicAcc;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.model.social.Relation;
import okhttp3.ResponseBody;

import static cn.xiaojs.xma.data.AccountDataManager.getSessionID;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class SocialManager {

//    public static void getContacts(final Context context, final LoadDataCallback<Cursor> callback) {
//
//        if (!(context instanceof FragmentActivity)) {
//
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the context is not FragmentActivity,so do nothing and return");
//            }
//            return;
//        }
//
//        CursorTaskLoader taskLoader = new CursorTaskLoader(context, new ContactDao());
//        DataLoader<Cursor> dataLoader = new DataLoader<>(taskLoader, callback);
//        ((FragmentActivity) context).getSupportLoaderManager().initLoader(0, null, dataLoader);
//
//    }

    public static void getAccountActivities(Context context,
                                            String account,
                                            Pagination pagination,
                                            APIServiceCallback<List<DynamicAcc>> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getAccountActivities(account,
                pagination.getPage(), pagination.getMaxNumOfObjectsPerPage());
    }


    /**
     * Add Contact Group
     * Adds a custom contact group.
     * @param context
     * @param groupName
     * @param callback
     */
    public static void addContactGroup(@NonNull Context context,
                                       String groupName,
                                       APIServiceCallback<ResponseBody> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.addContactGroup(groupName);


    }

    /**
     * Post Activity
     * Posts an activity into one's Moments or specific Class Circles.
     * @param context
     * @param post
     * @param callback
     */
    public static void postActivity(@NonNull Context context,
                                    DynPost post,
                                    @NonNull APIServiceCallback<Dynamic> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.postActivity(post);
    }

    /**
     * Get Activities
     * Returns recent activities and new activity updates for a specific account,
     * optionally filtered by criteria.
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getActivities(Context context,
                                     Criteria criteria,
                                     Pagination pagination,
                                     APIServiceCallback<CollectionPage<Dynamic>> callback) {


        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getActivities(criteria, pagination);
    }

    /**
     * Comment Activity
     * Leaves a comment on the specific activity.
     * @param context
     * @param activity
     * @param commentContent
     * @param callback
     */
    public static void commentActivity(Context context,
                                       String activity,
                                       String commentContent,
                                       APIServiceCallback<Comment> callback) {


        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.commentActivity(activity,commentContent);

    }

    /**
     * Get Comments
     * Returns a hierarchy of comments and replies underneath them for the specific document.
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getComments(Context context,
                                   Criteria criteria,
                                   Pagination pagination,
                                   APIServiceCallback<CollectionPage<Comment>> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getComments(criteria, pagination);
    }

    /**
     * Like Activity
     * Enables to express love to an activity.
     * @param context
     * @param activity
     * @param callback
     */
    public static void likeActivity(Context context,
                                    String activity,
                                    APIServiceCallback<Dynamic.DynStatus> callback) {


        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.likeActivity(activity);
    }

    /**
     * Reply To Comment
     * Replies to a comment.
     * @param context
     * @param commentID
     * @param reply
     * @param callback
     */
    public static void replyComment(Context context,
                                    String commentID,
                                    String reply,
                                    APIServiceCallback<Comment> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.replyComment(commentID,reply);
    }


    /**
     * Reply To Reply
     * Replies to a reply.
     * @param context
     * @param replyID
     * @param reply
     * @param callback
     */
    public static void reply2Reply(Context context,
                                   String replyID,
                                   String reply,
                                   APIServiceCallback<Comment> callback) {


        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.reply2Reply(replyID,reply);
    }


    /**
     * Get Updates
     * Returns a page of new activity updates only.
     * @param context
     * @param pagination
     * @param callback
     */
    public static void getUpdates(Context context,
                                  Pagination pagination,
                                  APIServiceCallback<CollectionPage<DynUpdate>> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getUpdates(pagination);

    }

    /**
     * Follow Contact
     * Follows the specific account.
     * @param context
     * @param contact
     * @param group
     * @param callback
     */
    public static void followContact(Context context,
                                     String contact,
                                     String name,
                                     long group,
                                     APIServiceCallback<Relation> callback) {


        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.followContact(contact,name, group);
    }


    /**
     * Unfollow Contact
     * Cancels followship with the specific contact.
     * @param context
     * @param contact
     * @param callback
     */
    public static void unfollowContact(Context context, String contact,APIServiceCallback callback) {


        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.unfollowContact(contact);

    }

    /**
     * Get Activity Details
     * Returns details for a specific activity.
     * @param context
     * @param activity
     * @param callback
     */
    public static void getActivityDetails(Context context,
                                          String activity,
                                          APIServiceCallback<DynamicDetail> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getActivityDetails(activity);

    }

    /**
     * Get Liked Records
     * Returns a specific page of liked records for the specific document.
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getLikedRecords(Context context,
                                       Criteria criteria,
                                       Pagination pagination,
                                       APIServiceCallback<CollectionPage<LikedRecord>> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getLikedRecords(criteria,pagination);

    }

    /**
     * Get Contacts
     * Retrieves all contacts, classes got involved in and recent chats for the session account.
     * @param context
     * @param callback
     */
    public static void getContacts(Context context,
                                    APIServiceCallback<ArrayList<ContactGroup>> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getContacts();

    }

    public static void getContacts2(Context context,
                                   APIServiceCallback<ArrayList<ContactGroup>> callback) {

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getContacts2();

    }

    public static ArrayList<ContactGroup> getContacts2(Context context) throws IOException{

        SocialRequest socialRequest = new SocialRequest(context,null);
        return socialRequest.getContacts2Sync();

    }

    public static ArrayList<ContactGroup> getContacts(Context context) throws IOException{

        SocialRequest socialRequest = new SocialRequest(context,null);
        return socialRequest.getContactsSync();

    }


    public static  ContactGroup parseAddGroup(String body) {

        ContactGroup contactGroup = null;

        try {
            JSONObject jobject = new JSONObject(body);

            String key = jobject.keys().next();
            String name = jobject.getString(key);

            contactGroup = new ContactGroup();
            contactGroup.name = name;
            contactGroup.group = Long.valueOf(key);
            contactGroup.collection = new ArrayList<>(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactGroup;
    }


    /**
     * 上传动态图片
     * @param context
     * @param filePath
     * @param qiniuService
     */
    public static void uploadSocialPhoto(Context context,
                                           @NonNull final String filePath,
                                           @NonNull QiniuService qiniuService) {


        if (qiniuService == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            qiniuService.uploadFailure(false);
            return;
        }

//        String session = getSessionID(context);
//
//        if (TextUtils.isEmpty(session)) {
//
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the sessionID is empty,so the request return failure");
//            }
//
//            qiniuService.uploadFailure();
//            return;
//        }

        QiniuRequest qiniuRequest = new QiniuRequest(context,filePath,qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.DRAWING_IN_ACTIVITY,1);


    }

    public static void deleteActivity(Context context, String activity,APIServiceCallback callback) {
        SocialRequest socialRequest = new SocialRequest(context,callback);
        socialRequest.deleteActivity(activity);
    }

    public static void deleteCommentOrReply(Context context, String commentOrReply,APIServiceCallback callback) {
        SocialRequest socialRequest = new SocialRequest(context,callback);
        socialRequest.deleteCommentOrReply(commentOrReply);
    }





}
