package cn.xiaojs.xma.data.api.service;

import java.util.ArrayList;

import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.AccessLesson;
import cn.xiaojs.xma.model.LiveSession.CtlSession;
import cn.xiaojs.xma.model.LiveSession.Ticket;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CancelReason;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.CreateLesson;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.model.GENotificationsResponse;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.HomeData;
import cn.xiaojs.xma.model.IgnoreNResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.security.AuthenticateStatus;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Privilege;
import cn.xiaojs.xma.model.account.RegisterInfo;
import cn.xiaojs.xma.model.VerifyCode;

import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.FollowParam;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.model.account.UpToken;
import cn.xiaojs.xma.model.account.UpTokenParam;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


/**
 * Created by maxiaobao on 2016/10/25.
 */

public interface XiaojsService {

    //Xiaojs rest api 中接口公共URL
    String BASE_URL = "http://192.168.100.3:3000";

    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    String TIME_ZONE_ID = "GMT+8";
    String METHOD_GET = "GET";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Accounts
    //

    //Claim Competency
    @Headers("Content-Type: application/json")
    @POST("/v1/accounts/competencies")
    Call<ClaimCompetency> claimCompetency(@Body CompetencyParams competencyParams);

    //Register
    @Headers("Content-Type: application/json")
    @POST("/v1/accounts")
    Call<ResponseBody> accountRegister(@Body RegisterInfo registerInfo);

    //Get Home Data
    @GET("/v1/accounts/home")
    Call<ResponseBody> getHomeData();

    //Edit Profile
    @Headers("Content-Type: application/json")
    @PATCH("/v1/accounts/profile")
    Call<ResponseBody> editProfile(@Body Account account);

    //Get Profile
    @GET("/v1/accounts/profile")
    Call<Account> getProfile();

    //Get upToken
//    @GET("/v1/accounts/up_avatar_token")
//    Call<TokenResponse> getAvatarUpToken(@Header("SessionID") String sessionID);

    //Get upToken
//    @GET("/v1/ctl/lessons/{lesson}/up_cover_token")
//    Call<TokenResponse> getCoverUpToken(
//                                        @Path("lesson") String lesson);

    //Get upToken
    @Headers("Content-Type: application/json")
    @POST("/v1/files/up_token")
    Call<UpToken[]> getUpToken(@Body UpTokenParam... tokenParam);

    //Get Center Data
    @GET("/v1/accounts/center")
    Call<CenterData> getCenterData();


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //CTL
    //

    //Create Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons")
    Call<CLResponse> createLiveLesson(@Body CreateLesson lesson);

    //Get Lessons
    @GET("/v1/ctl/lessons/{criteria}/{pagination}")
    Call<GetLessonsResponse> getLessons(@Path("criteria") String criteria,
                                        @Path("pagination") String pagination);

    //Put Lesson On Shelves
    @POST("/v1/ctl/lessons/{lesson}/onshelves")
    Call<ResponseBody> putLessonOnShelves(@Path("lesson") String lesson);

    //Cancel Lesson-On-Shelves
    @DELETE("/v1/ctl/lessons/{lesson}/onshelves")
    Call<ResponseBody> cancelLessonOnShelves(@Path("lesson") String lesson);

    //Get Enrolled Lessons
    @GET("/v1/ctl/lessons/enrolled/{criteria}/{pagination}")
    Call<GELessonsResponse> getEnrolledLessons(
                                               @Path("criteria") String criteria,
                                               @Path("pagination") String pagination);

    //Get Lesson Data
    @GET("/v1/ctl/lessons/{lesson}")
    Call<LessonDetail> getLessonData(@Path("lesson") String lesson);

    //Get Lesson Details
    @GET("/v1/ctl/lessons/{lesson}/home")
    Call<LessonDetail> getLessonDetails(@Path("lesson") String lesson);


    //Confirm Lesson Enrollment
    @GET("/v1/ctl/lessons/{lesson}/enroll/{registrant}")
    Call<CLEResponse> confirmLessonEnrollment(@Path("lesson") String lesson,
                                              @Path("registrant") String registrant);

    //Enroll Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons/{lesson}/enroll")
    Call<ELResponse> enrollLesson(@Path("lesson") String lesson,
                                  @Body OfflineRegistrant offlineRegistrant);

    //Edit Lesson
    @Headers("Content-Type: application/json")
    @PUT("/v1/ctl/lessons/{lesson}")
    Call<ResponseBody> editLesson(@Path("lesson") String lesson, @Body LiveLesson liveLesson);

    //Cancel Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons/{lesson}/cancel")
    Call<ResponseBody> cancelLesson(@Path("lesson") String lesson, @Body CancelReason reason);

    //Toggle Access-To-Lesson
    @PATCH("/v1/ctl/lessons/{lesson}/accessible")
    Call<ResponseBody> toggleAccessLesson(@Path("lesson") String lesson,
                                          @Body AccessLesson accessLesson);






    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Categories
    //

    //Get Subject (Demo)
    //@Headers("Cache-Control: max-age=60")
    @GET("/v1/categories/subjects/demo")
    Call<CSubject> getSubject();


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Platform
    //

    //Get Notifications Overview
    @GET("/v1/platform/notifications/overview/{pagination}")
    Call<GNOResponse> getNotificationsOverview(
                                               @Path("pagination") String pagination);

    //Get Notifications
    @GET("/v1/platform/notifications/{criteria}/{pagination}")
    Call<GENotificationsResponse> getNotifications(
                                                   @Path("criteria") String criteria,
                                                   @Path("pagination") String pagination);

    //Delete Notification
    @DELETE("/v1/platform/notifications/{notification}")
    Call<ResponseBody> deleteNotification(
                                   @Path("notification") String notification);


    //Ignore Notifications
    @PATCH("/v1/platform/notifications/{criteria}")
    Call<IgnoreNResponse> ignoreNotifications(
                                              @Path("criteria") String criteria);



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Search
    // Provides access to platform search interfaces accessible to the Xiaojs client applications.
    //

    //Search Accounts
    @GET("/v1/search/accounts/{query}")
    Call<ArrayList<AccountSearch>> searchAccounts(@Path("query") String query);




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Security
    //

    //Check Session
    @GET("/v1/security/session")
    Call<AuthenticateStatus> checkSession();


    //Login
    @Headers("Content-Type: application/json")
    @POST("/v1/security/login")
    Call<LoginInfo> login(@Body LoginParams params);

    //Logout
    @Headers("Content-Type: application/json")
    @DELETE("/v1/security/logout")
    Call<ResponseBody> logout();

    //Validate Code
    @GET("/v1/security/validate/{method}/{mobile}/{code}")
    Call<APIEntity> validateCode(@Path("method") int method,
                                 @Path("mobile") long mobile,
                                 @Path("code") int code);

    //Verify Mobile
    @GET("/v1/security/verify/{method}/{mobile}")
    Call<VerifyCode> sendVerifyCode(@Path("method") int method, @Path("mobile") long mobile);

    //Does User Have Privileges
    @GET("/v1/security/privileges/{privileges}")
    Call<Privilege[]>havePrivileges(@Path("privileges") String privileges);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Social
    //

    //Add Contact Group
    @Headers("Content-Type: application/json")
    @POST("/v1/social/contactgroups")
    Call<ResponseBody> addContactGroup(@Body ContactGroup group);

    //Comment Activity
    @Headers("Content-Type: application/json")
    @POST("/v1/social/activities/{activity}/comments")
    Call<Comment> commentActivity(@Path("activity") String activity,
                                  @Body Comment comment);

    // Get Activities
    @GET("/v1/social/activities/{criteria}/{pagination}")
    Call<CollectionPage<Dynamic>> getActivities(@Path("criteria") String criteria,
                                                @Path("pagination") String pagination);

    // Get Activity Details
    @GET("/v1/social/activities/{activity}")
    Call<DynamicDetail> getActivityDetails(@Path("activity") String activity);



    //Get Comments
    @GET("/v1/social/comments/{criteria}/{pagination}")
    Call<CollectionPage<Comment>> getComments(@Path("criteria") String criteria,
                                              @Path("pagination") String pagination);




    // Get Updates
    @GET("/v1/social/updates/{pagination}")
    Call<CollectionPage<DynUpdate>> getUpdates(@Path("pagination") String pagination);

    //Like Activity
    @POST("/v1/social/activities/{activity}/liked")
    Call<Dynamic.DynStatus> likeActivity(@Path("activity") String activity);

    // Post Activity
    @Headers("Content-Type: application/json")
    @POST("/v1/social/activities")
    Call<Dynamic> postActivity(@Body DynPost post);

    //Reply To Comment
    @Headers("Content-Type: application/json")
    @POST("/v1/social/comments/{comment}/replies")
    Call<Comment> replyComment(@Path("comment") String commentID,
                               @Body Comment comment);


    //Reply To Reply
    @Headers("Content-Type: application/json")
    @POST("/v1/social/replies/{reply}/replies")
    Call<Comment> reply2Reply(@Path("reply") String replyID,
                              @Body Comment comment);


    //Follow Contact
    @Headers("Content-Type: application/json")
    @POST("/v1/social/contacts")
    Call<Relation> followContact(@Body FollowParam param);


    //Unfollow Contact
    @DELETE("/v1/social/contacts/{contact}")
    Call<ResponseBody> unfollowContact(@Path("contact") String contact);

    //Get Contacts
    @GET("/v1/social/contacts")
    Call<ArrayList<ContactGroup>> getContacts();


    // Get Liked Records
    @GET("/v1/social/liked/{criteria}/{pagination}")
    Call<CollectionPage<LikedRecord>> getLikedRecords(@Path("criteria") String criteria,
                                                      @Path("pagination") String pagination);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Live Sessions
    //Provides access to live session interfaces accessible to the Xiaojs client applications.

    //Generate Ticket
    @GET("/v1/live/ticket/{cs}")
    Call<Ticket> generateTicket(@Path("cs") String cs);


    //Boot Session
    @Headers("Content-Type: application/json")
    @POST("/v1/live/{ticket}")
    Call<CtlSession> bootSession(@Path("ticket") String ticket);




}
