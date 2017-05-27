package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.AccessLesson;
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
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

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
import retrofit2.Call;

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

    public void getLessons(String account,int page, int limit){
        Call<CollectionPageData<PersonHomeUserLesson>> call = getService().getLessons(account,page,limit);
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

    public void getEnrolledStudents(String lesson,int page, int limit, String state){
        Call<EnrollPage> call = getService().getEnrolledStudents(lesson, page, limit, state);
        enqueueRequest(APIType.GET_ENROLLED_STUDENTS,call);

    }

    public void hideLesson(String lesson) {
        Call<ResponseBody> call = getService().hideLesson(lesson);
        enqueueRequest(APIType.HIDE_LESSON, call);
    }

    public void joinLesson(String lesson, Registrant registrant) {
        Call<JoinResponse> call = getService().joinLesson(lesson,registrant);
        enqueueRequest(APIType.JOIN_LESSON, call);
    }

    public void createClass(ClassParams params) {
        Call<CLResponse> call = getService().createClass(params);
        enqueueRequest(APIType.CREATE_CLASS, call);
    }

    public void getClassesSchedule(String cycle, int next, int pre) {
        Call<ScheduleData> call = getService().getClassesSchedule(cycle, next, pre);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }

    public void getClassesSchedule(String cycle, int next, int pre, String unformat, String type, String state) {
        Call<ScheduleData> call = getService().getClassesSchedule(cycle, next, pre, unformat, type, state);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }

    public void getClassesSchedule(Date start, Date end) {

        String startJsonstr = objectToJsonString(start);
        String endJsonstr = objectToJsonString(end);

        if (XiaojsConfig.DEBUG) {
            Logger.json(startJsonstr);
            Logger.json(endJsonstr);
        }

        Call<ScheduleData> call = getService().getClassesSchedule(startJsonstr, endJsonstr);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }

    public void getClassesSchedule(Date start, Date end, String unformat, String type, String state) {

        String startJsonstr = objectToJsonString(start);
        String endJsonstr = objectToJsonString(end);

        if (XiaojsConfig.DEBUG) {
            Logger.json(startJsonstr);
            Logger.json(endJsonstr);
        }

        Call<ScheduleData> call = getService().getClassesSchedule(startJsonstr, endJsonstr, unformat, type, state);
        enqueueRequest(APIType.GET_CLASSES_SCHEDULE, call);
    }


    public void scheduleClassLesson(String classes, ClassLesson lesson) {
        Call<ResponseBody> call = getService().scheduleClassLesson(classes, lesson);
        enqueueRequest(APIType.SCHEDULE_CLASS_LESSON, call);
    }

    public void checkOverlap(String classes, ClassLesson lesson) {
        Call<ResponseBody> call = getService().checkOverlap(classes, lesson);
        enqueueRequest(APIType.CHECK_OVERLAP, call);
    }

    public void getClass(String classid) {
        Call<ClassInfoData> call = getService().getClass(classid);
        enqueueRequest(APIType.GET_CLASS, call);
    }

    public void modifyClass(String classid, ModifyClassParams params) {
        Call<CLResponse> call = getService().modifyClass(classid,params);
        enqueueRequest(APIType.MODIFY_CLASS, call);
    }

}
