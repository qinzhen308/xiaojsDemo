package cn.xiaojs.xma.ui.recordlesson.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Date;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.ui.CommonWebActivity;
import cn.xiaojs.xma.ui.base.AbsOpModel;
import cn.xiaojs.xma.ui.common.ShareBeautifulQrcodeActivity;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.lesson.CancelLessonActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LiveLessonDetailActivity;
import cn.xiaojs.xma.ui.lesson.ModifyLessonActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.EditTimetableActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.recordlesson.EnrolledStudentsActivity;
import cn.xiaojs.xma.ui.recordlesson.ManualRegistrationActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonDetailActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonEnrollActivity;
import cn.xiaojs.xma.ui.widget.Common2Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.ResponseBody;

/**
 * Created by Paul Z on 2017/7/25.
 * 录播课操作
 */

public class RLOpModel extends AbsOpModel<RLesson> {



    public RLOpModel(int id){
        super(id);
    }


    @Override
    public void onClick(Activity context,RLesson data,int position){
        switch (getId()){
            case OP_APPLY:
                //报名页
                enterApplyPage(context,data);
                break;
            case OP_DELETE:
//                if(data==null){
//                    deleteNativeLesson(context,position);
//                }else {
//                    delete(context,position,data);
//                }
                break;
            case OP_EDIT:
                edit(context, data);
                break;
            case OP_LOOK:
                detail(context, data.id);
                break;
            case OP_PRIVATE:
                cancelPublish(context,data);
                break;
            case OP_PUBLIC:
                publish(context,data);
                break;
            case OP_PUBLISH:
                //上架
                shelves(context,data,position);
                break;
            case OP_SHARE:
                share(context,data);
                break;
            case OP_SIGNUP:
                registration(context,data);
                break;
            case OP_CANCEL_CHECK:
//                offShelves(context, data);
                break;
            case OP_REJECT_REASON:
                rejectReason(context,data);
                break;
            case OP_RECREATE_LESSON:
                lessonAgain(context,data);
                break;
            case OP_APPLY_STUDENTS_LIST:
                enterApplyStudents(context,data);
                break;

        }
    }

    private void showProgress(Activity activity){
        if(activity instanceof IDialogMethod){
            ((IDialogMethod) activity).showProgress(false);
        }
    }

    private void cancelProgress(Activity activity){
        if(activity instanceof IDialogMethod){
            ((IDialogMethod) activity).cancelProgress();
        }
    }

    //统一刷新列表，因为精准刷新在这里不太好实现
    //这个类的存在是为了封装所有操作，以适用很多场景，但精准刷新会加重耦合，使得这个类毫无意义
    //后续优化
    private void updateData(Activity context,boolean justNative){
        ((IUpdateMethod)context).updateData(justNative,2);
    }

    private void updateJustItem(Activity context,int position,RLesson cLesson){
        ((IUpdateMethod)context).updateItem(position,cLesson);
    }

    private void removeJustItem(Activity context,int position,RLesson cLesson){
        ((IUpdateMethod)context).updateItem(position,cLesson,"remove");
    }


    public static void editLesson(Activity context){
        Intent intent=new Intent(context,LessonCreationActivity.class);
        context.startActivity(intent);
    }


    //进入报名页
    public void enterApplyPage(Activity context,RLesson bean){
//        String url=ApiManager.getShareLessonUrl(bean.id, Social.SearchType.COURSE);
//        if(url.contains("?")){
//            url+="&app=android";
//        }else {
//            url+="?app=android";
//        }
//        CommonWebActivity.invoke(context,"",url);
        RecordedLessonEnrollActivity.invoke(context,bean.id);
    }


    //上架
    private void shelves(final Activity context,final RLesson bean,final int position) {

        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.record_lesson_shelve_tip);
        dialog.setDesc(AccountDataManager.isVerified(context)?R.string.record_lesson_shelve_tip_for_verified:R.string.record_lesson_shelve_tip_for_not_verify);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
                showProgress(context);
                LessonDataManager.putRecordedCourseOnShelves(context, bean.id, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress(context);

                        //如果是已经实名认证的用户开的课，上架成功后，自动通过，不需要审核
                        if (AccountDataManager.isVerified(context)) {
                            bean.state= Ctl.RecordedCourseState.ONSHELVES;
                            ToastUtil.showToast(context, R.string.shelves_ok);
                            updateData(context,true);
                        }else {
                            bean.state= Ctl.RecordedCourseState.PENDING_FOR_APPROVAL;
                            ToastUtil.showToast(context, R.string.shelves_need_examine);
                            updateJustItem(context,position,bean);
                        }


                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress(context);
                        ToastUtil.showToast(context, errorMessage);
                    }
                });
            }
        });

        dialog.show();

    }

    //编辑（区分公开课和班课）
    private void edit(Activity context,RLesson bean) {
        RecordedLessonActivity.invoke(context,bean.id,true);

    }

    //撤销审核，取消上架
    private void offShelves(final Activity context, final CLesson bean) {
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.cancel_examine);
        dialog.setDesc(R.string.cancel_examine_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
                showProgress(context);
                LessonDataManager.requestCancelLessonOnShelves(context, bean.id, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress(context);
                        bean.state=LessonState.DRAFT;
                        updateData(context,true);
                        ToastUtil.showToast(context, R.string.off_shelves_success);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress(context);
                        ToastUtil.showToast(context, errorMessage);
                    }
                });
            }
        });

        dialog.show();

    }


    //查看详情
    private void detail(Activity context,String id) {
        Intent intent = new Intent(context, LiveLessonDetailActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, id);
        context.startActivity(intent);
    }

    //班级圈
    private void circle(TeachLesson bean) {
        //modifyLesson(bean);
    }


    private void modifyLesson(Activity context,CLesson bean) {
        Intent intent = new Intent(context, ModifyLessonActivity.class);
        TeachLesson tl=new TeachLesson();
        tl.setId(bean.id);
        tl.setTitle(bean.title);
        tl.setSchedule(bean.schedule);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, tl);
        context.startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //取消上课
    private void cancelLesson(Activity context,CLesson bean) {
        Intent intent = new Intent(context, CancelLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        context.startActivityForResult(intent, CourseConstant.CODE_CANCEL_LESSON);
    }

    //删除
    private void delete(final Activity context,final int pos,final CLesson bean) {
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.delete);
        dialog.setDesc(R.string.delete_lesson_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
                hideLesson(context,pos, bean);
            }
        });
        dialog.show();
    }

    private void hideLesson(final Activity context, final int pos, final CLesson bean) {
        showProgress(context);
        if(Account.TypeName.STAND_ALONE_LESSON.equals(bean.type)){
            LessonDataManager.hideLesson(context, bean.id, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress(context);
                    ToastUtil.showToast(context, R.string.delete_success);
//                    removeJustItem(context, pos, bean);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress(context);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            LessonDataManager.deleteClassesLesson(context, bean.classInfo.id, bean.id, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress(context);
                    ToastUtil.showToast(context, R.string.delete_success);
//                    removeJustItem(context, pos, bean);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress(context);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    //备课
    private void prepare(TeachLesson bean) {

    }

    //班级主页
    private void home(Activity context,TeachLesson bean) {
        Intent intent = new Intent(context, GradeHomeActivity.class);
        //mContext.startActivity(intent);
    }

    //分享
    private void share(Context context,RLesson bean) {

        ShareBeautifulQrcodeActivity.invoke(context,ShareBeautifulQrcodeActivity.TYPE_RECORDED_LESSON,bean.id,bean.title,bean.teachers[0],bean.expire!=null?bean.expire.effective+"天":"永久");

//        if (bean == null) return;
//        String date = null;
//        if(bean.expire!=null){
//            date=ScheduleUtil.getDateYMD(new Date(bean.createdOn.getTime()+ ScheduleUtil.DAY*bean.expire.effective));
//        }else {
//            date="永久";
//        }
//        String name = "";
//        if (!ArrayUtil.isEmpty(bean.teachers)) {
//            name = bean.teachers[0].name;
//        }
//        String shareUrl = ApiManager.getShareLessonUrl(bean.id,Social.SearchType.COURSE);
//        ShareUtil.shareUrlByUmeng((Activity) context, bean.title, new StringBuilder(date).append("\r\n").append("主讲：").append(name).toString(), shareUrl);
    }

    //报名注册
    private void registration(Context context,RLesson bean) {
        if(bean.createdBy!=null&&AccountDataManager.getAccountID(context).equals(bean.createdBy.getId())){
            //线下报名--老师拉学生
            context.startActivity(new Intent(context,ManualRegistrationActivity.class).putExtra(CourseConstant.KEY_LESSON_ID,bean.id));
        }else {
            //线上报名---学生主动报
            /*String url=ApiManager.getShareLessonUrl(bean.id, Social.SearchType.COURSE);
            if(url.contains("?")){
                url+="&app=android";
            }else {
                url+="?app=android";
            }
            CommonWebActivity.invoke(context,"",url);*/
            RecordedLessonEnrollActivity.invoke(context,bean.id);
        }

    }

    //发布到主页
    private void publish(final Activity context, final RLesson bean) {
        if (bean.isPublic()) {
            cancelPublish(context,bean);
            return;
        }
        showProgress(context);
        LessonDataManager.publishRecordedCourse(context, bean.id, true, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress(context);
                bean.setPublic(true);
                ToastUtil.showToast(context, R.string.lesson_publish_tip);
                updateData(context,true);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress(context);
                ToastUtil.showToast(context, errorMessage);
            }
        });
    }

    //取消发布
    private void cancelPublish(final Activity context, final RLesson bean) {
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.cancel_publish);
        dialog.setDesc(R.string.cancel_publish_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {

                dialog.dismiss();

                showProgress(context);
                LessonDataManager.publishRecordedCourse(context, bean.id, false, new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {
                        cancelProgress(context);
                        bean.setPublic(false);
                        ToastUtil.showToast(context, R.string.course_state_cancel);
                        updateData(context,true);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress(context);
                        ToastUtil.showToast(context, errorMessage);
                    }
                });
            }
        });
        dialog.show();
    }

    //再次开课
    private void lessonAgain(Activity context,RLesson bean) {
        RecordedLessonActivity.invoke(context,bean.id);
    }

    private void rejectReason(Activity context, RLesson bean){
        Common2Dialog dialog=new Common2Dialog(context);
        dialog.setTitle("取消原因");
        dialog.setDesc(bean.reason);
        dialog.show();
    }

    public void enterApplyStudents(Context context,RLesson bean){
        EnrolledStudentsActivity.invoke(context,bean.id);
    }

    //判断权限是否大于等于班主任
    private boolean isTeaching(Context context,CLesson cl){
        String id= AccountPref.getAccountID(context);
        if(cl.owner!=null&&id.equals(cl.owner.getId())){//我是拥有者
            return true;
        }else if(cl.classInfo!=null&&!ArrayUtil.isEmpty(cl.classInfo.advisers)){//我是班主任
            for(cn.xiaojs.xma.model.account.Account ad:cl.classInfo.advisers){
                if(id.equals(ad.getId()))return true;
            }
        }
        return false;
    }

    //删除
    private void deleteNativeLesson(final Activity context,final int pos) {
        ((IUpdateMethod)context).updateItem(pos,null,"remove");
    }

}
