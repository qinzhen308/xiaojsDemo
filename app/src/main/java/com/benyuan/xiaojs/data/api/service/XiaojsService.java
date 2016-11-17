package com.benyuan.xiaojs.data.api.service;

import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.CLResponse;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.ELResponse;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.GELessonsResponse;
import com.benyuan.xiaojs.model.GNOResponse;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.GetSubjectResponse;
import com.benyuan.xiaojs.model.HomeData;
import com.benyuan.xiaojs.model.IgnoreNResponse;
import com.benyuan.xiaojs.model.LessonDetail;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.model.Notification;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


/**
 * Created by maxiaobao on 2016/10/25.
 */

public interface XiaojsService {

    //Xiaojs rest api 中接口公共URL
    String BASE_URL = "http://192.168.100.115:3000/";

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
    Call<Empty> accountRegister(@Body RegisterInfo registerInfo);

    //Get Home Data
    @GET("/v1/accounts/home")
    Call<HomeData> getHomeData(@Header("SessionID") String sessionID);

    //Edit Profile
    @PATCH("/v1/accounts/profile")
    Call<Empty> editProfile(@Header("SessionID") String sessionID,
                            @Body Account.Basic accountBasic);

    //Get Profile
    @GET("/v1/accounts/profile")
    Call<Account> getProfile(@Header("SessionID") String sessionID);

    //Get upToken
    @GET("/v1/accounts/up_avatar_token")
    Call<ResponseBody> getAvatarUpToken(@Header("SessionID") String sessionID);

    //Get upToken
    @GET("/v1/lessons/up_resource_token")
    Call<ResponseBody> getCoverUpToken(@Header("SessionID") String sessionID);



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
    Call<Empty> putLessonOnShelves(@Header("SessionID") String sessionID,
                                   @Path("lesson") String lesson);

    //Cancel Lesson-On-Shelves
    @DELETE("/v1/ctl/lessons/{lesson}/onshelves")
    Call<Empty> cancelLessonOnShelves(@Header("SessionID") String sessionID,
                                      @Path("lesson") String lesson);

    //Get Enrolled Lessons
    @GET("/v1/ctl/lessons/enrolled/{criteria}/{pagination}")
    Call<GELessonsResponse> getEnrolledLessons(@Header("SessionID") String sessionID,
                                               @Path("criteria") String criteria,
                                               @Path("pagination") String pagination);

    //Get Lesson Details
    @GET("/v1/ctl/lessons/{lesson}")
    Call<LessonDetail> getLessonDetails(@Path("lesson") String lesson);

    //Confirm Lesson Enrollment
    @GET("/v1/ctl/lessons/{lesson}/enroll/{registrant}")
    Call<Empty> confirmLessonEnrollment(@Header("SessionID") String sessionID,
                                        @Path("lesson") String lesson,
                                        @Path("registrant") String registrant);

    //Enroll Lesson
    @POST("/v1/ctl/lessons/{lesson}/enroll")
    Call<ELResponse> enrollLesson(@Header("SessionID") String sessionID,
                                  @Path("lesson") String lesson);


    //Get Lesson Homepage
    @GET("/v1/ctl/lesson-home/{lesson}")
    Call<LessonDetail> getLessonHomepage(@Path("lesson") String lesson);


    //Edit Lesson
    @Headers("Content-Type: application/json")
    @PUT("/v1/ctl/lessons/{lesson}")
    Call<ResponseBody> editLesson(@Header("SessionID") String sessionID,
                                @Path("lesson") String lesson,
                                @Body LiveLesson liveLesson);




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Categories
    //

    //Get Subject (Demo)
    @GET("/v1/categories/subjects/demo")
    Call<GetSubjectResponse> getSubject();


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
    Call<ArrayList<Notification>> getNotifications(@Header("SessionID") String sessionID,
                                                   @Path("criteria") String criteria,
                                                   @Path("pagination") String pagination);

    //Delete Notification
    @DELETE("/v1/platform/notifications/{notification}")
    Call<Empty> deleteNotification(@Header("SessionID") String sessionID,
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
    Call<Empty> logout(@Header("SessionID") String sessionID);

    //Validate Code
    @GET("/v1/security/validate/{method}/{mobile}/{code}")
    Call<APIEntity> validateCode(@Path("method") int method,
                                 @Path("mobile") long mobile,
                                 @Path("code") int code);

    //Verify Mobile
    @GET("/v1/security/verify/{method}/{mobile}")
    Call<VerifyCode> sendVerifyCode(@Path("method") int method, @Path("mobile") long mobile);


}
