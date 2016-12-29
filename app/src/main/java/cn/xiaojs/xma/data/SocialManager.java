package cn.xiaojs.xma.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.SocialRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.data.db.CursorTaskLoader;
import cn.xiaojs.xma.data.db.LoadDataCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.model.social.Relation;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class SocialManager extends DataManager {

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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.addContactGroup(session, groupName);


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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.postActivity(session, post);
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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getActivities(session, criteria, pagination);
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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.commentActivity(session,activity,commentContent);

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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getComments(session, criteria, pagination);
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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.likeActivity(session,activity);
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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.replyComment(session,commentID,reply);
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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.reply2Reply(session,replyID,reply);
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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }
        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getUpdates(session,pagination);

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
                                     int group,
                                     APIServiceCallback<Relation> callback) {

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.followContact(session,contact,group);
    }


    /**
     * Unfollow Contact
     * Cancels followship with the specific contact.
     * @param context
     * @param contact
     * @param callback
     */
    public static void unfollowContact(Context context, String contact,APIServiceCallback callback) {
        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.unfollowContact(session,contact);

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

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getActivityDetails(session,activity);

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


        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }
        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getLikedRecords(session,criteria,pagination);

    }

    /**
     * Get Contacts
     * Retrieves all contacts, classes got involved in and recent chats for the session account.
     * @param context
     * @param callback
     */
    public static void getContacts(Context context,
                                   APIServiceCallback<ArrayList<ContactGroup>> callback) {

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        SocialRequest socialRequest = new SocialRequest(context, callback);
        socialRequest.getContacts(session);

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





}
