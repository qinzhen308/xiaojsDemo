package cn.xiaojs.xma.data.api.service;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.AccessLesson;

import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.PersonHomeUserLesson;
import cn.xiaojs.xma.model.Registrant;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.model.account.AssociationStaus;
import cn.xiaojs.xma.model.account.CompetencySubject;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.account.OrgTeacher;
import cn.xiaojs.xma.model.account.PrivateHome;
import cn.xiaojs.xma.model.account.PublicHome;
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
import cn.xiaojs.xma.model.IgnoreNResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Privilege;
import cn.xiaojs.xma.model.VerifyCode;
import cn.xiaojs.xma.model.account.RegisterInfo;

import cn.xiaojs.xma.model.account.SocialRegisterInfo;
import cn.xiaojs.xma.model.account.VerifyParam;
import cn.xiaojs.xma.model.account.VerifyStatus;
import cn.xiaojs.xma.model.category.SubjectName;
import cn.xiaojs.xma.model.contents.Article;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.model.ctl.EnrollPage;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.model.ctl.LessonSchedule;
import cn.xiaojs.xma.model.ctl.LiveClass;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.material.LibOverview;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.TokenPair;
import cn.xiaojs.xma.model.material.UploadParam;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.model.order.EnrollOrder;
import cn.xiaojs.xma.model.order.Orderp;
import cn.xiaojs.xma.model.order.PaymentOrder;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.search.SearchResponse;
import cn.xiaojs.xma.model.security.AuthenticateStatus;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.model.account.PwdParam;
import cn.xiaojs.xma.model.security.ResetPwdParam;
import cn.xiaojs.xma.model.security.SocialLoginParams;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.model.social.DynPost;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.DynamicAcc;
import cn.xiaojs.xma.model.social.DynamicDetail;
import cn.xiaojs.xma.model.social.FollowParam;
import cn.xiaojs.xma.model.social.LikedRecord;
import cn.xiaojs.xma.model.social.Relation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by maxiaobao on 2016/10/25.
 */

public interface XiaojsService {

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
    Call<CompetencySubject> claimCompetency(@Body CompetencyParams competencyParams);

    //Get Competencies
    @GET("/v1/accounts/competencies")
    Call<ClaimCompetency> getCompetencies();


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
//    @Headers("Content-Type: application/json")
//    @POST("/v1/files/up_token")
//    Call<UpToken[]> getUpToken(@Body UpTokenParam... tokenParam);

    //Get Center Data
    @GET("/v1/accounts/center")
    Call<CenterData> getCenterData();

    //Get Private Home
    @GET("/v1/accounts/private")
    Call<PrivateHome> getPrivateHome();

    //Get Public Home
    @GET("/v1/accounts/public/{account}")
    Call<PublicHome> getPublicHome(@Path("account") String account);

    //Change Password
    @PATCH("/v1/accounts/password")
    Call<ResponseBody> changePassword(@Body PwdParam pwdParam);

    //Request Verification
    @POST("/v1/accounts/verification")
    Call<ResponseBody> requestVerification(@Body VerifyParam verifyParam);

    //Get Verification Status
    @GET("/v1/accounts/verification")
    Call<VerifyStatus> getVerificationStatus();

    //Acknowledge Invitation
    @PATCH("/v1/accounts/invitations/{invitation}/acknowledge")
    Call<ResponseBody> acknowledgeInvitation(@Path("invitation") String invitation, @Body DealAck dealAck);

    //Get Org Teachers
    @GET("/v1/accounts/public/{account}/teachers/{criteria}/{pagination}")
    Call<CollectionPage<OrgTeacher>> getOrgTeachers(@Path("account") String account,
                                                    @Path("criteria") String criteria,
                                                    @Path("pagination") String pagination);


    //Check Association
    @GET("/v1/accounts/association/{ea}/{openid}")
    Call<AssociationStaus> checkAssociation(@Path("ea") String ea, @Path("openid") String openid);

    //Social Associate
    @POST("/v1/accounts/association/{ea}/{openid}")
    Call<ResponseBody> socialAssociate(@Path("ea") String ea,
                                       @Path("openid") String openid,
                                       @Body SocialRegisterInfo registerInfo);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //CTL
    //

    //Acknowledge Lesson
    @POST("/v1/ctl/lessons/{lesson}/acknowledge")
    Call<ResponseBody> acknowledgeLesson(@Path("lesson") String lesson, @Body DealAck dealAck);

    //Create Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons")
    Call<CLResponse> createLiveLesson(@Body CreateLesson lesson);

    //Get Lessons
//    @GET("/v1/ctl/lessons/{criteria}/{pagination}")
//    Call<GetLessonsResponse> getLessons(@Path("criteria") String criteria,
//                                        @Path("pagination") String pagination);

    //Get lessons (Taught by user)
    @GET("/v1/ctl/taught/lessons/{userId}")
    Call<CollectionPageData<PersonHomeUserLesson>> getLessons(@Path("userId") String userId,
                                                              @Query("page") int page,
                                                              @Query("limit") int limit);

    //Put Lesson On Shelves
    @POST("/v1/ctl/lessons/{lesson}/onshelves")
    Call<ResponseBody> putLessonOnShelves(@Path("lesson") String lesson);

    //Cancel Lesson-On-Shelves
    @DELETE("/v1/ctl/lessons/{lesson}/onshelves")
    Call<ResponseBody> cancelLessonOnShelves(@Path("lesson") String lesson);

    //Get Enrolled Lessons
//    @GET("/v1/ctl/lessons/enrolled/{criteria}/{pagination}")
//    Call<GELessonsResponse> getEnrolledLessons(
//                                               @Path("criteria") String criteria,
//                                               @Path("pagination") String pagination);

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

    //跟Edit Lesson是一个接口，在修改上课时间时，为了避免上传多余字段。
    @Headers("Content-Type: application/json")
    @PUT("/v1/ctl/lessons/{lesson}")
    Call<ResponseBody> editLessonSchedule(@Path("lesson") String lesson,
                                          @Body LessonSchedule lessonSchedule);

    //Cancel Lesson
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons/{lesson}/cancel")
    Call<ResponseBody> cancelLesson(@Path("lesson") String lesson, @Body CancelReason reason);

    //Toggle Access-To-Lesson
    @PATCH("/v1/ctl/lessons/{lesson}/accessible")
    Call<ResponseBody> toggleAccessLesson(@Path("lesson") String lesson,
                                          @Body AccessLesson accessLesson);


    //Get Classes
    @GET("/v1/ctl/classes/{criteria}/{pagination}")
    Call<GetLessonsResponse> getClasses(@Path("criteria") String criteria,
                                        @Path("pagination") String pagination);

    //Get Enrolled Classes
    @GET("/v1/ctl/classes/enrolled/{criteria}/{pagination}")
    Call<GELessonsResponse> getEnrolledClasses(@Path("criteria") String criteria,
                                               @Path("pagination") String pagination);

    //Get Live Classes
    @GET("/v1/ctl/live")
    Call<LiveClass> getLiveClasses();

    //Get Enrolled Students
    @GET("/v1/ctl/lesson/{lessonId}/enrolled")
    Call<EnrollPage> getEnrolledStudents(@Path("lessonId") String lessonId,
                                         @Query("page") int page,
                                         @Query("limit") int limit,
                                         @Query("state") String state);

    //Hide Lesson
    @PATCH("/v1/ctl/lessons/{lesson}/hidden")
    Call<ResponseBody> hideLesson(@Path("lesson") String lesson);

    //Join Lesson
    @POST("/v1/ctl/lessons/{lesson}/join")
    Call<JoinResponse> joinLesson(@Path("lesson") String lesson, @Body Registrant registrant);

    @POST("/v1/ctl/lessons/{lesson}/join")
    Call<JoinResponse> joinLesson(@Path("lesson") String lesson);


    //Create Class
    @POST("/v1/ctl/classes")
    Call<CLResponse> createClass(@Body ClassParams params);

    //Get Classes Schedule
    @GET("/v1/ctl/schedule")
    Call<ScheduleData> getClassesSchedule(@Query("cycle") String cycle,
                                          @Query("next") int next,
                                          @Query("pre") int pre);

    @GET("/v1/ctl/schedule/{classId}")
    Call<ScheduleData> getClassesSchedule(@Path("classId") String classId,
                                          @Query("cycle") String cycle,
                                          @Query("next") int next,
                                          @Query("pre") int pre);


    //Get Classes Schedule
    @GET("/v1/ctl/schedule")
    Call<ScheduleData> getClassesSchedule(@Query("cycle") String cycle,
                                          @Query("next") int next,
                                          @Query("pre") int pre,
                                          @Query("unformat") String unformat,
                                          @Query("type") String type,
                                          @Query("state") String state);

    @GET("/v1/ctl/schedule/{classId}")
    Call<ScheduleData> getClassesSchedule(@Path("classId") String classId,
                                          @Query("cycle") String cycle,
                                          @Query("next") int next,
                                          @Query("pre") int pre,
                                          @Query("unformat") String unformat,
                                          @Query("type") String type,
                                          @Query("state") String state);


    @GET("/v1/ctl/schedule")
    Call<ScheduleData> getClassesSchedule(@Query("start") String start,
                                          @Query("end") String end);

    @GET("/v1/ctl/schedule/{classId}")
    Call<ScheduleData> getClassesSchedule(@Path("classId") String classId,
                                          @Query("start") String start,
                                          @Query("end") String end);

    @GET("/v1/ctl/schedule")
    Call<ScheduleData> getClassesSchedule(@Query("start") String start,
                                          @Query("end") String end,
                                          @Query("unformat") String unformat,
                                          @Query("type") String type,
                                          @Query("state") String state);


    @GET("/v1/ctl/schedule/{classId}")
    Call<ScheduleData> getClassesSchedule(@Path("classId") String classId,
                                          @Query("start") String start,
                                          @Query("end") String end,
                                          @Query("unformat") String unformat,
                                          @Query("type") String type,
                                          @Query("state") String state);


    @GET("/v1/ctl/schedule")
    Call<CollectionResult<PrivateClass>> getClassesSchedule4Class(@Query("start") String start,
                                                                  @Query("end") String end,
                                                                  @Query("type") String type,
                                                                  @Query("state") String state,
                                                                  @Query("limit") int limit,
                                                                  @Query("page") int page);

    @GET("/v1/ctl/schedule")
    Call<CollectionResult<PrivateClass>> getClassesSchedule4Class(@Query("start") String start,
                                                                  @Query("end") String end,
                                                                  @Query("type") String type,
                                                                  @Query("state") String state,
                                                                  @Query("limit") int limit,
                                                                  @Query("page") int page,
                                                                  @Query("q") String q);

    @GET("/v1/ctl/schedule")
    Call<CollectionCalendar<ClassSchedule>> getClassesSchedule4Lesson(@Query("start") String start,
                                                                      @Query("end") String end,
                                                                      @Query("type") String type,
                                                                      @Query("state") String state,
                                                                      @Query("limit") int limit,
                                                                      @Query("page") int page);

    @GET("/v1/ctl/schedule")
    Call<CollectionCalendar<ClassSchedule>> getClassesSchedule4Lesson(@Query("start") String start,
                                                                      @Query("end") String end,
                                                                      @Query("type") String type,
                                                                      @Query("state") String state,
                                                                      @Query("limit") int limit,
                                                                      @Query("page") int page,
                                                                      @Query("q") String q);

    //Schedule Class Lesson
    @POST("/v1/ctl/classes/{classes}/schedule/lessons")
    Call<ResponseBody> scheduleClassLesson(@Path("classes") String classes,
                                           @Body ClassLesson classLesson);

    //Check Overlap
    @POST("/v1/ctl/classes/{classes}/schedule/check")
    Call<ResponseBody> checkOverlap(@Path("classes") String classes, @Body ClassLesson classLesson);

    //Get Class
    @GET("/v1/ctl/classes/{class}")
    Call<ClassInfoData> getClass(@Path("class") String classid);

    //Modify Class
    @PATCH("/v1/ctl/classes/{class}/modify")
    Call<CLResponse> modifyClass(@Path("class") String classid, @Body ModifyClassParams params);

    //Get Class Students
    @GET("/v1/ctl/classes/{classes}/students/{pagination}")
    Call<CollectionPage<StudentEnroll>> getClassStudents(@Path("classes") String classes,
                                                         @Path("pagination") String pagination);

    //Add Class Student
    @PATCH("/v1/ctl/classes/{classes}/students")
    Call<ResponseBody> addClassStudent(@Path("classes") String classes,
                                       @Body ClassEnrollParams enrollParams);

    //Remove Class Student
    @DELETE("/v1/ctl/classes/{classes}/students")
    Call<ResponseBody> removeClassStudent(@Path("classes") String classes, @Body String[] students);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Categories
    //

    //Get Subject (Demo)
    //@Headers("Cache-Control: max-age=60")
    @GET("/v1/categories/subjects/demo")
    Call<CSubject> getSubject();

    //Get Subjects
    @GET("/v1/categories/subjects/{parent}")
    Call<List<CSubject>> getSubjects(@Path("parent") String parent,
                                     @Query("page") int page,
                                     @Query("limit") int limit);

    //Add Open Subject
    @POST("/v1/categories/subjects")
    Call<CSubject> addOpenSubject(@Body SubjectName name);

    //Search Subjects
    @GET("/v1/categories/subjects/{name}/search/{pagination}")
    Call<CollectionPage<CSubject>> searchSubjects(@Path("name") String name,
                                                  @Path("pagination") String pagination);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Order
    //

    //Create Order
    @POST("/v1/order")
    Call<PaymentOrder> createOrder(@Body Orderp orderp);

    //CreatePaymentCharge
    @GET("/v1/order/{order}/charge/{channel}")
    Call<ResponseBody> createPaymentCharge(@Path("order") String order,
                                           @Path("channel") String channel);

    //Get Orders
    @GET("/v1/orders")
    Call<List<EnrollOrder>> getOrders(@Query("page") int page, @Query("limit") int limit);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Platform
    //

    //Check Upgrade
    @GET("/v1/platform/version")
    Call<Upgrade> checkUpgrade();

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

    //Search Accounts 2.0
    @GET("/v1/search/accounts_v2.0")
    Call<CollectionPageData<AccountInfo>> searchAccounts(@Query("key") String key,
                                                         @Query("page") int page,
                                                         @Query("limit") int limit,
                                                         @Query("type") String type);

    //Search Lessons
    @GET("/v1/search/lessons")
    Call<CollectionPageData<LessonInfo>> searchLessons(@Query("key") String key,
                                                       @Query("page") int page,
                                                       @Query("limit") int limit,
                                                       @Query("type") String type);


    //Search Accounts & Lessons
    @GET("/v1/search/integrate")
    Call<SearchResponse> searchAccountsOrLessons(@Query("key") String key, @Query("size") String size);


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

    //Social Login
    @POST("/v1/security/sociallogin/{ea}/{openid}")
    Call<LoginInfo> socialLogin(@Path("ea") String ea,
                                @Path("openid") String openid,
                                @Body SocialLoginParams params);

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
    Call<Privilege[]> havePrivileges(@Path("privileges") String privileges);


    //Reset Password
    @PATCH("/v1/security/forgot")
    Call<ResponseBody> resetPassword(@Body ResetPwdParam resetPwdParam);


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

    //Get Account Activities
    @GET("/v1/social/activities/{account}?page={page}&limit={limit}")
    Call<List<DynamicAcc>> getAccountActivities(@Path("account") String account, int page, int limit);

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


    //Delete Activity
    @DELETE("/v1/social/activities/{activity}")
    Call<ResponseBody> deleteActivity(@Path("activity") String activity);

    //Delete Comment Or Reply
    @DELETE("/v1/social/comments/{commentOrReply}")
    Call<ResponseBody> deleteCommentOrReply(@Path("commentOrReply") String commentOrReply);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Collaborations
    //

    //Get Upload Tokens
    @GET("/v1/collaboration/uploadtokens/{type}/{quantity}")
    Call<TokenPair[]> getUploadTokens(@Path("type") int type, @Path("quantity") int quantity);

    //Add To Library
    @POST("/v1/collaboration/documents")
    Call<UploadReponse> addToLibrary(@Body UploadParam uploadParam);

    //Get Library Overview
    @GET("/v1/collaboration/overview/{criteria}/{pagination}")
    Call<LibOverview> getLibraryOverview(@Path("criteria") String criteria,
                                         @Path("pagination") String pagination);


    //Get Documents
    @GET("/v1/collaboration/documents")
    Call<UserDoc> getDocuments(@Query("id") String id,
                               @Query("subtype") String subtype
                               /*@Query("page") int page,
                               @Query("limit") int limit*/);

    //Delete Document
    @DELETE("/v1/collaboration/documents/{document}")
    Call<ResponseBody> deleteDocument(@Path("document") String document, @Query("shared") boolean shared);

    //Share Document
    @PATCH("/v1/collaboration/documents/{document}/share")
    Call<ShareDoc> shareDocument(@Path("document") String document, @Body ShareResource resource);

    //Share Documents
    @POST("/v1/collaboration/library/{targetId}/share")
    Call<ShareDoc> shareDocuments(@Path("targetId") String targetId, @Body ShareResource resource);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Contents
    //

    //Get Article
    @GET("/v1/contents/articles/{article}")
    Call<Article> getArticle(@Path("article") String article);


}
