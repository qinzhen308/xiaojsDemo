package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Xu;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.LessonRequest;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.CancelReason;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.CreateLesson;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Pagination;

import cn.xiaojs.xma.model.Registrant;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.PersonHomeUserLesson;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.model.ctl.EnrollPage;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.model.ctl.LessonSchedule;
import cn.xiaojs.xma.model.ctl.LiveClass;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.model.ctl.StudentInfo;
import okhttp3.ResponseBody;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonDataManager {


    /**
     * 创建直播课
     */
    public static void requestCreateLiveLesson(Context context,
                                               CreateLesson lesson,
                                               APIServiceCallback<CLResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the create live lession request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.createLiveLesson(lesson);

    }

//
//    /**
//     * 获取开的课程
//     */
//    public static void requestGetLessons(Context context,
//                                         @NonNull Criteria criteria,
//                                         @NonNull Pagination pagination,
//                                         @NonNull APIServiceCallback<GetLessonsResponse> callback) {
//
//        if (callback == null) {
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the api service callback is null,so cancel the GetLessons request");
//            }
//            return;
//        }
//
//        LessonRequest lessonRequest = new LessonRequest(context, callback);
//        lessonRequest.getLessons(criteria, pagination);
//
//    }

    /**
     * 通过用户ID获取用户授的课
     */
    public static void getLessonsByUser(Context context,
                                        String account,
                                        Pagination pagination,
                                        APIServiceCallback<CollectionPageData<PersonHomeUserLesson>> callback) {

        int page = 1;
        int limit = 10;

        if (pagination != null) {
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        if (account == null) {
            account = "";
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLessons(account, page, limit);

    }

    /**
     * 上架直播课
     */
    public static void requestPutLessonOnShelves(Context context,
                                                 @NonNull String lesson,
                                                 @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the putLessonOnShelves request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.putLessonOnShelves(lesson);

    }

    /**
     * 取消上架课程
     */
    public static void requestCancelLessonOnShelves(Context context,
                                                    @NonNull String lesson,
                                                    @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the requestCancelLessonOnShelves request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.cancelLessonOnShelves(lesson);
    }

//    /**
//     * 获取已报名的课程
//     */
//    public static void requestGetEnrolledLessons(Context context,
//                                                 @NonNull Criteria criteria,
//                                                 @NonNull Pagination pagination,
//                                                 @NonNull APIServiceCallback<GELessonsResponse> callback) {
//
//        if (callback == null) {
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("the api service callback is null,so cancel the GetEnrolledLessons request");
//            }
//            return;
//        }
//
//
//        LessonRequest lessonRequest = new LessonRequest(context, callback);
//        lessonRequest.getEnrolledLessons(criteria, pagination);
//
//    }

    /**
     * 获取直播课详情
     */
    public static void requestLessonData(Context context,
                                         @NonNull String lesson,
                                         @NonNull APIServiceCallback<LessonDetail> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the requestGetLessonDetails request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLessonData(lesson);
    }


    /**
     * 上传课程封面图片
     */
    public static void requestUploadCover(Context context,
                                          @NonNull final String filePath,
                                          @NonNull QiniuService qiniuService) {

        if (qiniuService == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            qiniuService.uploadFailure(false);
            return;
        }

//        String session = AccountDataManager.getSessionID(context);
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

        QiniuRequest qiniuRequest = new QiniuRequest(context, filePath, qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.COVER_OF_CTL, 1);

    }


    public static void requestLessonDetails(Context context,
                                            @NonNull String lesson,
                                            @NonNull APIServiceCallback<LessonDetail> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLessonDetails(lesson);
    }


    public static void requestEditLesson(Context context,
                                         @NonNull String lesson,
                                         @NonNull LiveLesson liveLesson,
                                         @NonNull APIServiceCallback callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.editLesson(lesson, liveLesson);
    }

    /**
     * 修改上课时间
     */
    public static void editLessonSchedule(Context context,
                                          @NonNull String lesson,
                                          @NonNull LessonSchedule lessonSchedule,
                                          @NonNull APIServiceCallback callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.editLessonSchedule(lesson, lessonSchedule);
    }


    public static void requestEnrollLesson(Context context,
                                           @NonNull String lesson,
                                           @Nullable OfflineRegistrant offlineRegistrant,
                                           @NonNull APIServiceCallback<ELResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.enrollLesson(lesson, offlineRegistrant);

    }

    public static void requestLessonEnrollment(Context context,
                                               @NonNull String lesson,
                                               @NonNull String registrant,
                                               @NonNull APIServiceCallback<CLEResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.confirmLessonEnrollment(lesson, registrant);
    }


    public static void requestCancelLesson(Context context,
                                           @NonNull String lesson,
                                           @NonNull CancelReason reason,
                                           @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.cancelLesson(lesson, reason);

    }

    public static void requestToggleAccessLesson(Context context,
                                                 @NonNull String lesson,
                                                 boolean accessible,
                                                 @NonNull APIServiceCallback callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.toggleAccessLesson(lesson, accessible);
    }


    /**
     * 获取开的课程
     */
    public static void getClasses(Context context,
                                  @NonNull Criteria criteria,
                                  @NonNull Pagination pagination,
                                  @NonNull APIServiceCallback<GetLessonsResponse> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClasses(criteria, pagination);

    }

    /**
     * 获取我报名的课
     */
    public static void getEnrolledClasses(Context context,
                                          @NonNull Criteria criteria,
                                          @NonNull Pagination pagination,
                                          @NonNull APIServiceCallback<GELessonsResponse> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getEnrolledClasses(criteria, pagination);

    }


    /**
     * 直播课接口
     */
    public static void getLiveClasses(Context context, @NonNull APIServiceCallback<LiveClass> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLiveClasses();
    }

    public static void acknowledgeLesson(Context context, String lesson, DealAck ack, APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.acknowledgeLesson(lesson, ack);
    }

    /**
     * 查询指定课程的报名的学生
     */
    public static void getEnrolledStudents(Context context,
                                           String lesson,
                                           int page,
                                           int limit,
                                           APIServiceCallback<EnrollPage> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getEnrolledStudents(lesson, page, limit, "Enrolled");

    }

    /**
     * Hidden the specific standalone lesson.
     */
    public static void hideLesson(Context context, String lesson, APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.hideLesson(lesson);
    }

    /**
     * 加入无需报名的课
     *
     * @param registrant 可选项，此处可传null
     */
    public static void joinLesson(Context context,
                                  String lesson,
                                  Registrant registrant,
                                  APIServiceCallback<JoinResponse> callback) {
        if (registrant == null) {
            registrant = new Registrant();
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.joinLesson(lesson, registrant);
    }

    /**
     * Creates a class by a teacher or an organization, and entering into the following lifecycle.
     */
    public static void createClass(Context context,
                                   ClassParams params,
                                   APIServiceCallback<CLResponse> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.createClass(params);
    }


    /**
     *
     * @param context
     * @param cycle
     * @param next
     * @param pre
     * @param callback
     */
    public static void getClassesSchedule(Context context,
                                          String cycle,
                                          int next,
                                          int pre,
                                          APIServiceCallback<ScheduleData> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule(cycle, next, pre);
    }

    /**
     *
     * @param context
     * @param start
     * @param end
     * @param callback
     */
    public static void getClassesSchedule(Context context,
                                          long start,
                                          long end,
                                          APIServiceCallback<ScheduleData> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule(start, end);
    }


    /**
     * Schedule Class Lesson .
     * @param context
     * @param classes
     * @param lesson
     * @param callback
     */
    public static void scheduleClassLesson(Context context,
                                           String classes,
                                           ClassLesson lesson,
                                           APIServiceCallback<ScheduleData> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.scheduleClassLesson(classes, lesson);
    }

    /**
     * 可用于排课前的检查，包括检查老师的状态、上课日程是否冲突 .
     * @param context
     * @param classes
     * @param lesson
     * @param callback
     */
    public static void checkOverlap(Context context,
                                    String classes,
                                    ClassLesson lesson,
                                    APIServiceCallback callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.checkOverlap(classes, lesson);
    }

    /**
     * get the info of privateclass.
     * 获取班信息
     * @param context
     * @param classid
     * @param callback
     */
    public static void getClassInfo(Context context,
                                    String classid,
                                    APIServiceCallback<ClassInfoData> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClass(classid);
    }

    /**
     * modify the specific private class.
     * 编辑班课内容（班名称、班主任）;
     * @param context
     * @param classid
     * @param params
     * @param callback
     */
    public static void modifyClass(Context context,
                                   String classid,
                                   ModifyClassParams params,
                                   APIServiceCallback<CLResponse> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.modifyClass(classid, params);
    }
}
