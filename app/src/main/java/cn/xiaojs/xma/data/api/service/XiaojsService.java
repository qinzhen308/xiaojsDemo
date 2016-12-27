package cn.xiaojs.xma.data.api.service;

import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.AccessLesson;
import cn.xiaojs.xma.model.Account;
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
import cn.xiaojs.xma.model.LoginInfo;
import cn.xiaojs.xma.model.LoginParams;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Privilege;
import cn.xiaojs.xma.model.RegisterInfo;
import cn.xiaojs.xma.model.TokenResponse;
import cn.xiaojs.xma.model.VerifyCode;

import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.Dynamic;
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Accounts
    //

    //Claim Competency
    @Headers("Content-Type: application/json")
    @POST("/v1/accounts/competencies")
    Call<ClaimCompetency> claimCompetency(@Header("SessionID") String sessionID,
                                          @Body CompetencyParams competencyParams);

    //Register
    @Headers("Content-Type: application/json")
    @POST("/v1/accounts")
    Call<ResponseBody> accountRegister(@Body RegisterInfo registerInfo);

    //Get Home Data
    @GET("/v1/accounts/home")
    Call<HomeData> getHomeData(@Header("SessionID") String sessionID);

    //Edit Profile
    @PATCH("/v1/accounts/profile")
    Call<ResponseBody> editProfile(@Header("SessionID") String sessionID,
                            @Body Account.Basic accountBasic);

    //Get Profile
    @GET("/v1/accounts/profile")
    Call<Account.Basic> getProfile(@Header("SessionID") String sessionID);

    //Get upToken
    @GET("/v1/accounts/up_avatar_token")
    Call<TokenResponse> getAvatarUpToken(@Header("SessionID") String sessionID);

    //Get upToken
    @GET("/v1/ctl/lessons/{lesson}/up_cover_token")
    Call<TokenResponse> getCoverUpToken(@Header("SessionID") String sessionID,
                                        @Path("lesson") String lesson);

    //Get Center Data
    @GET("/v1/accounts/center")
    Call<CenterData> getCenterData(@Header("SessionID") String sessionID);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //CTL
    //

    //Create Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons")
    Call<CLResponse> createLiveLesson(@Header("SessionID") String sessionID,
                                      @Body CreateLesson lesson);

    //Get Lessons
    @GET("/v1/ctl/lessons/{criteria}/{pagination}")
    Call<GetLessonsResponse> getLessons(@Header("SessionID") String sessionID,
                                        @Path("criteria") String criteria,
                                        @Path("pagination") String pagination);

    //Put Lesson On Shelves
    @POST("/v1/ctl/lessons/{lesson}/onshelves")
    Call<ResponseBody> putLessonOnShelves(@Header("SessionID") String sessionID,
                                   @Path("lesson") String lesson);

    //Cancel Lesson-On-Shelves
    @DELETE("/v1/ctl/lessons/{lesson}/onshelves")
    Call<ResponseBody> cancelLessonOnShelves(@Header("SessionID") String sessionID,
                                      @Path("lesson") String lesson);

    //Get Enrolled Lessons
    @GET("/v1/ctl/lessons/enrolled/{criteria}/{pagination}")
    Call<GELessonsResponse> getEnrolledLessons(@Header("SessionID") String sessionID,
                                               @Path("criteria") String criteria,
                                               @Path("pagination") String pagination);

    //Get Lesson Data
    @GET("/v1/ctl/lessons/{lesson}")
    Call<LessonDetail> getLessonData(@Header("SessionID") String sessionID,
                                     @Path("lesson") String lesson);

    //Get Lesson Details
    @GET("/v1/ctl/lessons/{lesson}/home")
    Call<LessonDetail> getLessonDetails(@Path("lesson") String lesson);


    //Confirm Lesson Enrollment
    @GET("/v1/ctl/lessons/{lesson}/enroll/{registrant}")
    Call<CLEResponse> confirmLessonEnrollment(@Header("SessionID") String sessionID,
                                              @Path("lesson") String lesson,
                                              @Path("registrant") String registrant);

    //Enroll Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons/{lesson}/enroll")
    Call<ELResponse> enrollLesson(@Header("SessionID") String sessionID,
                                  @Path("lesson") String lesson,
                                  @Body OfflineRegistrant offlineRegistrant);

    //Edit Lesson
    @Headers("Content-Type: application/json")
    @PUT("/v1/ctl/lessons/{lesson}")
    Call<ResponseBody> editLesson(@Header("SessionID") String sessionID,
                                @Path("lesson") String lesson,
                                @Body LiveLesson liveLesson);

    //Cancel Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons/{lesson}/cancel")
    Call<ResponseBody> cancelLesson(@Header("SessionID") String sessionID,
                                  @Path("lesson") String lesson,
                                  @Body CancelReason reason);

    //Toggle Access-To-Lesson
    @PATCH("/v1/ctl/lessons/{lesson}/accessible")
    Call<ResponseBody> toggleAccessLesson(@Header("SessionID") String sessionID,
                                          @Path("lesson") String lesson,
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
    Call<GNOResponse> getNotificationsOverview(@Header("SessionID") String sessionID,
                                               @Path("pagination") String pagination);

    //Get Notifications
    @GET("/v1/platform/notifications/{criteria}/{pagination}")
    Call<GENotificationsResponse> getNotifications(@Header("SessionID") String sessionID,
                                                   @Path("criteria") String criteria,
                                                   @Path("pagination") String pagination);

    //Delete Notification
    @DELETE("/v1/platform/notifications/{notification}")
    Call<ResponseBody> deleteNotification(@Header("SessionID") String sessionID,
                                   @Path("notification") String notification);


    //Ignore Notifications
    @PATCH("/v1/platform/notifications/{criteria}")
    Call<IgnoreNResponse> ignoreNotifications(@Header("SessionID") String sessionID,
                                              @Path("criteria") String criteria);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Security
    //

    //Login
    @Headers("Content-Type: application/json")
    @POST("/v1/security/login")
    Call<LoginInfo> login(@Body LoginParams params);

    //Logout
    @Headers("Content-Type: application/json")
    @DELETE("/v1/security/logout")
    Call<ResponseBody> logout(@Header("SessionID") String sessionID);

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
    Call<Privilege[]>havePrivileges(@Header("SessionID") String sessionID,
                                    @Path("privileges") String privileges);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Social
    //

    //Add Contact Group
    @Headers("Content-Type: application/json")
    @POST("/v1/social/contactgroups")
    Call<ResponseBody> addContactGroup(@Header("SessionID") String sessionID,
                                       @Body ContactGroup group);

    //Comment Activity
    @Headers("Content-Type: application/json")
    @POST("/v1/social/activities/{activity}/comments")
    Call<Comment> commentActivity(@Header("SessionID") String sessionID,
                                  @Path("activity") String activity,
                                  @Body Comment comment);

    //Follow Contact
    @Headers("Content-Type: application/json")
    @POST("/v1/social/contacts")
    Call<ResponseBody> followContact(@Header("SessionID") String sessionID,
                                     String contact,
                                     int group);

    // Get Activities
    @GET("/v1/social/activities/{criteria}/{pagination}")
    Call<CollectionPage<Dynamic>> getActivities(@Header("SessionID") String sessionID,
                                                @Path("criteria") String criteria,
                                                @Path("pagination") String pagination);


    //Get Comments
    @GET("/v1/social/comments/{criteria}/{pagination}")
    Call<CollectionPage<Comment>> getComments(@Header("SessionID") String sessionID,
                                              @Path("criteria") String criteria,
                                              @Path("pagination") String pagination);




    // Get Updates
    //@GET("/v1/social/updates/{pagination}")
    //Call<CollectionPage<>>

    //Like Activity
    @POST("/v1/social/activities/{activity}/liked")
    Call<Dynamic.DynStatus> likeActivity(@Header("SessionID") String sessionID,
                                         @Path("activity") String activity);

    // Post Activity
    @Headers("Content-Type: application/json")
    @POST("/v1/social/activities")
    Call<Dynamic> postActivity(@Header("SessionID") String sessionID, @Body DynPost post);

    //Reply To Comment
    @Headers("Content-Type: application/json")
    @POST("/v1/social/comments/{comment}/replies")
    Call<Comment> replyComment(@Header("SessionID") String sessionID,
                              @Path("comment") String commentID,
                              @Body Comment comment);


    //Reply To Reply
    @Headers("Content-Type: application/json")
    @POST("/v1/social/replies/{reply}/replies")
    Call<Comment> reply2Reply(@Header("SessionID") String sessionID,@Path("reply") String replyID, @Body Comment comment);



}
