package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.AccessLesson;
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

import com.orhanobut.logger.Logger;

import java.util.Map;

import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.Registrant;
import cn.xiaojs.xma.model.account.DealAck;

import cn.xiaojs.xma.model.PersonHomeUserLesson;

import cn.xiaojs.xma.model.ctl.CheckOverlapParams;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.CriteriaStudents;
import cn.xiaojs.xma.model.ctl.JoinClassParams;
import cn.xiaojs.xma.model.ctl.JoinCriteria;
import cn.xiaojs.xma.model.ctl.ModifyModeParam;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.ctl.ClassInfoData;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.model.ctl.ClassParams;
import cn.xiaojs.xma.model.ctl.EnrollPage;
import cn.xiaojs.xma.model.ctl.JoinResponse;
import cn.xiaojs.xma.model.ctl.LessonSchedule;
import cn.xiaojs.xma.model.ctl.LiveClass;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.model.ctl.DecisionReason;
import cn.xiaojs.xma.model.ctl.RemoveStudentParams;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.model.ctl.ScheduleParams;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.ctl.Students;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonRequest extends ServiceRequest {

    public LessonRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void createLiveLesson(CreateLesson lesson) {

        Call<CLResponse> call = getService().createLiveLesson(lesson);
        enqueueRequest(APIType.CREATE_LESSON, call);

    }

    public void getLessons(String account, int page, int limit) {
        Call<CollectionPageData<PersonHomeUserLesson>> call = getService().getLessons(account, page, limit);
        enqueueRequest(APIType.GET_LESSONS_BY_USER, call);
    }

//    public void getLessons(@NonNull Criteria criteria,
//                           @NonNull Pagination pagination) {
//
//
//        String criteriaJsonstr = objectToJsonString(criteria);
//        String paginationJsonstr = objectToJsonString(pagination);
//
//        if (XiaojsConfig.DEBUG) {
//            Logger.json(criteriaJsonstr);
//            Logger.json(paginationJsonstr);
//        }
//
//
//        Call<GetLessonsResponse> call = getService().getLessons(criteriaJsonstr,
//                paginationJsonstr);
//
//        enqueueRequest(APIType.GET_LESSONS, call);
//
//    }


    public void putLessonOnShelves(@NonNull String lesson) {

        Call<ResponseBody> call = getService().putLessonOnShelves(lesson);
        enqueueRequest(APIType.PUT_LESSON_ON_SHELVES, call);

    }

    public void cancelLessonOnShelves(@NonNull String lesson) {

        Call<ResponseBody> call = getService().cancelLessonOnShelves(lesson);
        enqueueRequest(APIType.CANCEL_LESSON_ON_SHELVES, call);
    }

//    public void getEnrolledLessons(@NonNull Criteria criteria,
//                                   @NonNull Pagination pagination) {
//
//
//        String criteriaJsonstr = objectToJsonString(criteria);
//        String paginationJsonstr = objectToJsonString(pagination);
//
//        if (XiaojsConfig.DEBUG) {
//            Logger.json(criteriaJsonstr);
//            Logger.json(paginationJsonstr);
//        }
//
//        Call<GELessonsResponse> call = getService().getEnrolledLessons(criteriaJsonstr,
//                paginationJsonstr);
//        enqueueRequest(APIType.GET_ENROLLED_LESSONS, call);
//
//    }

    public void getLessonData(@NonNull String lesson) {

        Call<LessonDetail> call = getService().getLessonData(lesson);
        enqueueRequest(APIType.GET_LESSON_DATA, call);

    }


    public void getLessonDetails(@NonNull String lesson) {

        Call<LessonDetail> call = getService().getLessonDetails(lesson);
        enqueueRequest(APIType.GET_LESSON_DETAILS, call);

    }

    public void editLesson(@NonNull String lesson,
                           @NonNull LiveLesson liveLesson) {

        Call<ResponseBody> call = getService().editLesson(lesson, liveLesson);
        enqueueRequest(APIType.EDIT_LESSON, call);

    }

    public void editLessonSchedule(@NonNull String lesson,
                                   @NonNull LessonSchedule lessonSchedule) {

        Call<ResponseBody> call = getService().editLessonSchedule(lesson, lessonSchedule);
        enqueueRequest(APIType.EDIT_LESSON_SCHEDULE, call);

    }


    public void enrollLesson(@NonNull String lesson,
                             @Nullable OfflineRegistrant offlineRegistrant) {

        Call<ELResponse> call = getService().enrollLesson(lesson, offlineRegistrant);
        enqueueRequest(APIType.ENROLL_LESSON, call);

    }

    public void confirmLessonEnrollment(@NonNull String lesson,
                                        @NonNull String registrant) {

        Call<CLEResponse> call = getService().confirmLessonEnrollment(lesson, registrant);
        enqueueRequest(APIType.CONFIRM_LESSON_ENROLLMENT, call);
    }

    public void cancelLesson(@NonNull String lesson,
                             @NonNull CancelReason reason) {

        Call<ResponseBody> call = getService().cancelLesson(lesson, reason);
        enqueueRequest(APIType.CANCEL_LESSON, call);

    }


    public void toggleAccessLesson(@NonNull String lesson,
                                   final boolean accessible) {

        AccessLesson accessLesson = new AccessLesson();
        accessLesson.setAccessible(accessible);

        Call<ResponseBody> call = getService().toggleAccessLesson(lesson, accessLesson);
        enqueueRequest(APIType.TOGGLE_ACCESS_TO_LESSON, call);

    }


    public void getClasses(@NonNull Criteria criteria,
                           @NonNull Pagination pagination) {


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }


        Call<GetLessonsResponse> call = getService().getClasses(criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_CLASSES, call);

    }

    public void getEnrolledClasses(@NonNull Criteria criteria, @NonNull Pagination pagination) {


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }


        Call<GELessonsResponse> call = getService().getEnrolledClasses(criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_ENROLLED_CLASSESS, call);

    }

    public void getLiveClasses() {
        Call<LiveClass> call = getService().getLiveClasses();
        enqueueRequest(APIType.GET_LIVE_CLASSES, call);
    }


    public void acknowledgeLesson(String lesson, DealAck ack) {
        Call<ResponseBody> call = getService().acknowledgeLesson(lesson, ack);
        enqueueRequest(APIType.ACKNOWLEDGE_LESSON, call);

    }

    public void getEnrolledStudents(String lesson, int page, int limit, String state) {
        Call<EnrollPage> call = getService().getEnrolledStudents(lesson, page, limit, state);
        enqueueRequest(APIType.GET_ENROLLED_STUDENTS, call);

    }

    public void hideLesson(String lesson) {
        Call<ResponseBody> call = getService().hideLesson(lesson);
        enqueueRequest(APIType.HIDE_LESSON, call);
    }

    public void joinLesson(String lesson, Registrant registrant) {
        Call<JoinResponse> call = getService().joinLesson(lesson, registrant);
        enqueueRequest(APIType.JOIN_LESSON, call);
    }

    public void createClass(ClassParams params) {
        Call<CLResponse> call = getService().createClass(params);
        enqueueRequest(APIType.CREATE_CLASS, call);
    }

    public void getClassesSchedule(String classId, Map<String, String> options) {
        Call<ScheduleData> call;
        if (TextUtils.isEmpty(classId)) {
            call = getService().getClassesSchedule(options);
        } else {
            call = getService().getClassesSchedule(classId, options);
        }

        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }

    public void getClassesSchedule4Class(Map<String, String> options,
                                         int limit,
                                         int page) {
        Call<CollectionResult<PrivateClass>> call = getService().getClassesSchedule4Class(options,
                limit, page);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }

    public void getClassesSchedule4Lesson(Map<String, String> options,
                                          int limit,
                                          int page) {

        Call<CollectionCalendar<ClassSchedule>> call = getService().getClassesSchedule4Lesson(options,
                limit, page);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }

    public void getClassesSchedule4Lesson(String classid,
                                          Map<String, String> options,
                                          int limit,
                                          int page) {

        Call<CollectionCalendar<ClassSchedule>> call = getService().getClassesSchedule4Lesson(classid,
                options,
                limit,
                page);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }


    public void getClassesSchedule(String type, int limit,String state) {
        Call<CollectionResult<PrivateClass>> call = getService().getClassesSchedule(type, limit,state);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }


    public void scheduleClassLesson(String classes, ScheduleParams params) {
        Call<ResponseBody> call = getService().scheduleClassLesson(classes, params);
        enqueueRequest(APIType.SCHEDULE_CLASS_LESSON, call);
    }

    public boolean checkOverlapSync(CheckOverlapParams params) throws Exception {

        Response<ResponseBody> response = getService().checkOverlap(params).execute();
        if (response != null && response.code() == SUCCESS_CODE) {
            return true;
        }
        return false;
    }

    public void getClass(String classid) {
        Call<ClassInfo> call = getService().getClass(classid);
        enqueueRequest(APIType.GET_CLASS, call);
    }

    public void modifyClass(String classid, ModifyClassParams params) {
        Call<CLResponse> call = getService().modifyClass(classid, params);
        enqueueRequest(APIType.MODIFY_CLASS, call);
    }
    public void modifyClass(String classid, Publish params) {
        Call<CLResponse> call = getService().modifyClass(classid, params);
        enqueueRequest(APIType.MODIFY_CLASS, call);
    }
    public void modifyClass(String classid, ModifyModeParam params) {
        Call<CLResponse> call = getService().modifyClass(classid, params);
        enqueueRequest(APIType.MODIFY_CLASS, call);
    }

    public void getClassStudents(String classes, JoinCriteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }


        Call<CollectionPage<StudentEnroll>> call = getService().getClassStudents(classes,
                criteriaJsonstr, paginationJsonstr);
        enqueueRequest(APIType.GET_CLASS_STUDENTS, call);
    }

    public void getClasses(CriteriaStudents criteria) {

        String criteriaJsonstr = objectToJsonString(criteria);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
        }
        Call<Students> call = getService().getClasses(criteriaJsonstr);
        enqueueRequest(APIType.GET_CLASS_STUDENTS_JOIN, call);
    }

    public void addClassStudent(String classId, ClassEnrollParams enrollParams) {
        Call<ResponseBody> call = getService().addClassStudent(classId, enrollParams);
        enqueueRequest(APIType.ADD_CLASS_STUDENTS, call);

    }

    public void removeClassStudent(String classid, RemoveStudentParams students) {
        Call<ResponseBody> call = getService().removeClassStudent(classid, students);
        enqueueRequest(APIType.REMOVE_CLASS_STUDENT, call);
    }

    public void joinLesson(String lesson) {
        Call<JoinResponse> call = getService().joinLesson(lesson);
        enqueueRequest(APIType.JOIN_LESSON, call);
    }

    public void joinClass(String classid, JoinClassParams joinClassParams) {
        Call<ResponseBody> call = getService().joinClass(classid,joinClassParams);
        enqueueRequest(APIType.JOIN_CLASS, call);
    }

    public void reviewJoinClass(String joinId, DecisionReason reason) {
        Call<ResponseBody> call = getService().reviewJoinClass(joinId, reason);
        enqueueRequest(APIType.REVIEW_JOIN_CLASS, call);
    }


    public void removeClass(String classid) {
        Call<ResponseBody> call = getService().removeClass(classid);
        enqueueRequest(APIType.REMOVE_CLASS, call);
    }

    public void modifyClassesLesson(String classId, String lessonId, ClassLesson classLesson) {
        Call<ResponseBody> call = getService().modifyClassesLesson(classId, lessonId, classLesson);
        enqueueRequest(APIType.MODIFY_CLASSES_LESSON, call);
    }

    public void deleteClassesLesson(String classId, String lessonId) {
        Call<ResponseBody> call = getService().deleteClassesLesson(classId, lessonId);
        enqueueRequest(APIType.DELETE_CLASS_LESSON, call);
    }

    public void cancelClassesLesson(String classId, String lessonId, CancelReason reason) {
        Call<ResponseBody> call = getService().cancelClassesLesson(classId, lessonId, reason);
        enqueueRequest(APIType.CANCEL_CLASS_LESSON, call);
    }


    @Override
    public void doTask(int apiType, Object responseBody) {
        if (apiType == APIType.JOIN_CLASS
                || apiType == APIType.JOIN_LESSON
                || apiType == APIType.ENROLL_LESSON
                || apiType == APIType.CREATE_CLASS
                || apiType == APIType.CREATE_LESSON) {

            //FIXME 此处有个BUG，
            // 1.当等于APIType.JOIN_CLASS时，如果加入需要验证的班，验证通过后才能改为true。先忽略此问题。
            // 2.解散班、删除公开课后，也会有此问题，也先忽略
            AccountDataManager.setHasClass(getContext(), true);
        }
    }
}
