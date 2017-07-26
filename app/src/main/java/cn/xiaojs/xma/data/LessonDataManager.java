package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.LessonRequest;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.CancelReason;
import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.CreateLesson;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Pagination;

import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.PersonHomeUserLesson;
import cn.xiaojs.xma.model.ctl.CRecordLesson;
import cn.xiaojs.xma.model.ctl.CheckOverlapParams;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.CriteriaStudents;
import cn.xiaojs.xma.model.ctl.EnrollPage;
import cn.xiaojs.xma.model.ctl.JoinClassParams;
import cn.xiaojs.xma.model.ctl.JoinCriteria;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.model.ctl.LessonSchedule;
import cn.xiaojs.xma.model.ctl.LiveClass;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.model.ctl.ModifyModeParam;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.ctl.DecisionReason;
import cn.xiaojs.xma.model.ctl.RemoveStudentParams;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.model.ctl.ScheduleOptions;
import cn.xiaojs.xma.model.ctl.ScheduleParams;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.ctl.Students;
import cn.xiaojs.xma.model.recordedlesson.RLChapter;
import cn.xiaojs.xma.model.recordedlesson.RLStudentsCriteria;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.recordedlesson.RLessonDetail;
import cn.xiaojs.xma.model.recordedlesson.RecordedLessonCriteria;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import okhttp3.ResponseBody;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
     */
    public static void joinLesson(Context context,
                                  String lesson,
                                  APIServiceCallback<JoinResponse> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.joinLesson(lesson);
    }

    /**
     * Creates a class by a teachers or an organization, and entering into the following lifecycle.
     */
    public static void createClass(Context context,
                                   ClassParams params,
                                   APIServiceCallback<CLResponse> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.createClass(params);
    }


    /**
     * Get Classes Schedule
     */
    public static void getClassesSchedule(Context context,
                                          ScheduleOptions options,
                                          APIServiceCallback<ScheduleData> callback) {

        getClassesSchedule(context, null, options, callback);
    }

    public static void getClassesSchedule(Context context,
                                          String classId,
                                          ScheduleOptions options,
                                          APIServiceCallback<ScheduleData> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule(classId, options.getOptions());
    }


    public static void getClassesSchedule4Class(Context context,
                                                ScheduleOptions options,
                                                Pagination pagination,
                                                APIServiceCallback<CollectionResult<PrivateClass>> callback) {

        int limit = pagination.getMaxNumOfObjectsPerPage();
        int page = pagination.getPage();

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule4Class(options.getOptions(), limit, page);
    }

    public static void getClassesSchedule4Lesson(Context context,
                                                 ScheduleOptions options,
                                                 Pagination pagination,
                                                 APIServiceCallback<CollectionCalendar<ClassSchedule>> callback) {

        int limit = pagination.getMaxNumOfObjectsPerPage();
        int page = pagination.getPage();

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule4Lesson(options.getOptions(), limit, page);
    }

    public static void getClassesSchedule4Lesson(Context context,
                                                 String classid,
                                                 ScheduleOptions options,
                                                 Pagination pagination,
                                                 APIServiceCallback<CollectionCalendar<ClassSchedule>> callback) {

        int limit = pagination.getMaxNumOfObjectsPerPage();
        int page = pagination.getPage();

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule4Lesson(classid, options.getOptions(), limit, page);
    }


    /**
     * 获取热门班
     *
     * @param context
     * @param limit
     * @param callback
     */
    public static void getHotClasses(Context context,
                                     int limit,
                                     APIServiceCallback<CollectionResult<PrivateClass>> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassesSchedule(Account.TypeName.CLASS, limit, "NotHumanRemoved");
    }

    /**
     * Schedule Class Lesson .
     */
    public static void scheduleClassLesson(Context context,
                                           String classes,
                                           ScheduleParams params,
                                           APIServiceCallback callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.scheduleClassLesson(classes, params);
    }

    /**
     * 可用于排课前的检查，包括检查老师的状态、上课日程是否冲突 .
     */
    public static boolean checkOverlap(Context context,
                                       CheckOverlapParams params) throws Exception {

        LessonRequest lessonRequest = new LessonRequest(context, null);
        return lessonRequest.checkOverlapSync(params);
    }

    /**
     * get the info of privateclass.
     * 获取班信息
     */
    public static void getClassInfo(Context context,
                                    String classid,
                                    APIServiceCallback<ClassInfo> callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClass(classid);
    }

    /**
     * modify the specific private class.
     * 编辑班课内容（班名称、班主任）;
     */
    public static void modifyClass(Context context,
                                   String classid,
                                   ModifyClassParams params,
                                   APIServiceCallback<CLResponse> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.modifyClass(classid, params);
    }

    public static void modifyClass(Context context,
                                   String classid,
                                   ModifyModeParam params,
                                   APIServiceCallback<CLResponse> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.modifyClass(classid, params);
    }

    public static void modifyClass(Context context,
                                   String classid,
                                   Publish params,
                                   APIServiceCallback<CLResponse> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.modifyClass(classid, params);
    }


    /**
     * Returns the students taught by an classes .
     */
    public static void getClassStudents(Context context,
                                        String classes,
                                        boolean joined,
                                        Pagination pagination,
                                        APIServiceCallback<CollectionPage<StudentEnroll>> callback) {

        JoinCriteria joinCriteria = new JoinCriteria();
        joinCriteria.joined = joined;

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClassStudents(classes, joinCriteria, pagination);

    }

    /**
     * Import students from a standaloneLesson or class, or enrolled students to the class.
     */
    public static void addClassStudent(Context context,
                                       String classId,
                                       ClassEnrollParams enrollParams,
                                       APIServiceCallback callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.addClassStudent(classId, enrollParams);

    }

    public static void getClasses(Context context, CriteriaStudents criteria, APIServiceCallback<Students> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getClasses(criteria);
    }


    /**
     * Remove enrolled students from the Class.
     */
    public static void removeClassStudent(Context context,
                                          String classid,
                                          String[] students,
                                          APIServiceCallback callback) {


        RemoveStudentParams params = new RemoveStudentParams();
        params.students = students;


        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.removeClassStudent(classid, params);

    }

    /**
     * A person apply for join as student for the class
     *
     * @param context
     * @param classid
     * @param callback
     */
    public static void joinClass(Context context, String classid, JoinClassParams joinClassParams, APIServiceCallback<ResponseBody> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.joinClass(classid, joinClassParams);

    }

    /**
     * Approved or rejected the person apply for join class as student.
     *
     * @param context
     * @param joinId
     * @param reason
     * @param callback
     */
    public static void reviewJoinClass(Context context,
                                       String joinId,
                                       DecisionReason reason,
                                       APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.reviewJoinClass(joinId, reason);
    }


    /**
     * Remove a class.
     * Can only be removed if the state as Idle. if there are unfinished lessons in the class,
     * please cancelled or removed the lessons if you want to do this .
     *
     * @param context
     * @param classid
     * @param callback
     */
    public static void removeClass(Context context, String classid, APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.removeClass(classid);
    }


    /**
     * 编辑班课
     *
     * @param context
     * @param classId
     * @param lessonId
     * @param classLesson
     * @param callback
     */
    public static void modifyClassesLesson(Context context,
                                           String classId,
                                           String lessonId,
                                           ClassLesson classLesson,
                                           APIServiceCallback callback) {

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.modifyClassesLesson(classId, lessonId, classLesson);

    }

    /**
     * 删除班课
     *
     * @param context
     * @param classId
     * @param lessonId
     * @param callback
     */
    public static void deleteClassesLesson(Context context,
                                           String classId,
                                           String lessonId,
                                           APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.deleteClassesLesson(classId, lessonId);
    }


    /**
     * 取消班课.
     *
     * @param context
     * @param classId
     * @param lessonId
     * @param reason
     * @param callback
     */
    public static void cancelClassesLesson(Context context,
                                           String classId,
                                           String lessonId,
                                           CancelReason reason,
                                           APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.cancelClassesLesson(classId, lessonId, reason);
    }

    /**
     * Create Recorded Course
     *
     * @param context
     * @param recordLesson
     * @param callback
     */
    public static void createRecordedCourse(Context context,
                                            CRecordLesson recordLesson,
                                            APIServiceCallback<CLResponse> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.createRecordedCourse(recordLesson);
    }

    /**
     * Requests to put the specific recorded course on shelves.
     *
     * @param context
     * @param course
     * @param callback
     */
    public static void putRecordedCourseOnShelves(Context context,
                                                  String course,
                                                  APIServiceCallback callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.putRecordedCourseOnShelves(course);
    }

    /**
     * Returns a collection of standalone lessons that matched the specified criteria.
     *
     * @param context
     * @param pagination
     * @param start
     * @param end
     * @param state
     * @param key
     * @param subtype
     * @param callback
     */
    public void getCourses(Context context,
                           Pagination pagination,
                           String start,
                           String end,
                           String state,
                           String key,
                           String subtype,
                           APIServiceCallback<ResponseBody> callback) {


        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getCourses(pagination.getMaxNumOfObjectsPerPage(),
                pagination.getPage(),
                start,
                end,
                state,
                key,
                subtype);
    }

    /**
     * Returns recordedCourses owned or enrolled by an user or taught by an user
     * (as the lead teachers or a teaching assistant) or owned by an organization,
     * optionally filtered by criteria.
     *
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getRecordedCourses(Context context,
                                          RecordedLessonCriteria criteria,
                                          Pagination pagination,
                                          APIServiceCallback<CollectionPage<RLesson>> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getRecordedCourses(criteria, pagination);
    }


    /**
     * Returns detailed data for the recordedCourse.
     *
     * @param context
     * @param course
     * @param callback
     */
    public static void getRecordedCourse(Context context,
                                         String course,
                                         APIServiceCallback<RLessonDetail> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getRecordedCourse(course);
    }

    /**
     * Returns detailed data for the recordedCourse in public state.
     *
     * @param context
     * @param course
     * @param callback
     */
    public static void getRecordedCoursePublic(Context context,
                                               String course,
                                               APIServiceCallback<RLessonDetail> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getRecordedCoursePublic(course);
    }

    public static void getRecordedCourseChapters(Context context,
                                                 String course,
                                                 String chapter,
                                                 APIServiceCallback<ArrayList<RLChapter>> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getRecordedCourseChapters(course, chapter);
    }


    public static void getRecordedCourseStudents(Context context,
                                                 String course,
                                                 RLStudentsCriteria criteria,
                                                 Pagination pagination,
                                                 APIServiceCallback<CollectionPage<StudentEnroll>> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getRecordedCourseStudents(course, criteria, pagination);
    }

    public static void addRecordedCourseStudent(Context context,
                                                String course,
                                                ClassEnrollParams enrollParams,
                                                APIServiceCallback<ResponseBody> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.addRecordedCourseStudent(course, enrollParams);
    }

    public static void reviewRecordedCourseEnroll(Context context,
                                                String enroll,
                                                DecisionReason reason,
                                                APIServiceCallback<ResponseBody> callback) {
        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.reviewRecordedCourseEnroll(enroll, reason);
    }

}
