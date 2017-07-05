package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.ctl.Adviser;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.lesson.CancelLessonActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonBusiness;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.lesson.LiveLessonDetailActivity;
import cn.xiaojs.xma.ui.lesson.ModifyLessonActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.EditTimetableActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.widget.Common2Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/5/25.
 */

public class LOpModel {
    //查看报名页
    public static final int OP_APPLY =0;
    //取消开课
    public static final int OP_CANCEL_LESSON=1;
    //撤销确认（机构端对老师）
    public static final int OP_CANCEL_SUBMIT=2;
    //班级信息
    public static final int OP_CLASS_INFO=3;
    //资料库（黄icon）
    public static final int OP_DATABASE2 =4;
    //删除
    public static final int OP_DELETE=5;
    //编辑课
    public static final int OP_EDIT=6;
    //进入教室
    public static final int OP_ENTER=7;
    //进入教室（黄icon）
    public static final int OP_ENTER_2=8;
    //查看详情
    public static final int OP_LOOK=9;
    //修改上课时间
    public static final int OP_MODIFY_TIME=10;
    //取消公开
    public static final int OP_PRIVATE=11;
    //设为公开
    public static final int OP_PUBLIC=12;
    //上架
    public static final int OP_PUBLISH=13;
    //再次开课
    public static final int OP_CREATE_LESSON_AGAIN =14;
    //课表
    public static final int OP_SCHEDULE=15;
    //分享(蓝图标按钮)
    public static final int OP_SHARE=16;
    //报名注册
    public static final int OP_SIGNUP=17;
    //提交确认（机构端对老师）
    public static final int OP_SUBMIT=18;
    //撤销审核
    public static final int OP_CANCEL_CHECK=19;
    //橘黄色的分享按钮
    public static final int OP_SHARE2=20;
    //蓝色图标的资料库按钮
    public static final int OP_DATABASE1=21;
    //同意邀请 （对于老师）
    public static final int OP_AGREE_INVITE=22;
    //拒绝邀请 （对于老师）
    public static final int OP_DISAGREE_INVITE=23;
    //查看原因（审核被拒绝后）
    public static final int OP_REJECT_REASON=24;
    //重新开课
    public static final int OP_RECREATE_LESSON=25;

    private int id;

    public LOpModel(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public void onClick(Activity context,CLesson data,int position){
        switch (id){
            case OP_APPLY:
                //报名页
                enterApplyPage(context,data.id);
                break;
            case OP_CANCEL_LESSON:
                cancelLesson(context,data);
                break;
            case OP_CANCEL_SUBMIT:

                break;
            case OP_CLASS_INFO:
                classInfo(context, data.classInfo.id,false);
                break;
            case OP_DATABASE2:
//                enterDatabase(context);
                databank(context,data);
                break;
            case OP_DELETE:
                if(data==null){
                    deleteNativeLesson(context,position);
                }else {
                    delete(context,position,data);
                }
                break;
            case OP_EDIT:
                edit(context, data);
                break;
            case OP_ENTER:
                enterClass(context, data);
                break;
            case OP_ENTER_2:
                enterClass(context, data);
                break;
            case OP_LOOK:
                detail(context, data.id);
                break;
            case OP_MODIFY_TIME:
                modifyLesson(context,data);
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
            case OP_CREATE_LESSON_AGAIN:
                lessonAgain(context,data);
                break;
            case OP_SCHEDULE:
                enterSchedule(context, data.classInfo.id, data.classInfo.title,isTeaching(context,data));
                break;
            case OP_SHARE:
                share(context,data);
                break;
            case OP_SIGNUP:
                registration(context,data);
                break;
            case OP_SUBMIT:
                break;
            case OP_CANCEL_CHECK:
                offShelves(context, data);
                break;
            case OP_SHARE2:
                share(context,data);
                break;
            case OP_DATABASE1:
                databank(context,data);
                break;
            case OP_AGREE_INVITE:
                dealAck(context,position,data,Ctl.ACKDecision.ACKNOWLEDGE);
                break;
            case OP_DISAGREE_INVITE:
                dealAck(context,position,data,Ctl.ACKDecision.REFUSED);
                break;
            case OP_REJECT_REASON:
                rejectReason(context,data);
                break;
            case OP_RECREATE_LESSON:
                lessonAgain(context,data);
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
        ((IUpdateMethod)context).updateData(justNative);
    }

    private void updateJustItem(Activity context,int position,CLesson cLesson){
        ((IUpdateMethod)context).updateItem(position,cLesson);
    }

    private void removeJustItem(Activity context,int position,CLesson cLesson){
        ((IUpdateMethod)context).updateItem(position,cLesson,"remove");
    }


    public static void editLesson(Activity context){
        Intent intent=new Intent(context,LessonCreationActivity.class);
        context.startActivity(intent);
    }


    public static void enterSchedule(Activity context,String classId,String title,boolean teaching){
        ClassScheduleActivity.invoke(context,classId,title,teaching);
    }


    public void classInfo(Activity context,String classId,boolean teaching){
        Intent intent=new Intent(context,ClassInfoActivity.class);
        intent.putExtra(ClassInfoActivity.EXTRA_CLASSID,classId);
        context.startActivity(intent);
    }

    //进入报名页
    public void enterApplyPage(Activity context,String lessonId){
        context.startActivity(new Intent(context,LessonHomeActivity.class).putExtra(CourseConstant.KEY_LESSON_ID,lessonId));
    }


    //上架
    private void shelves(final Activity context,final CLesson bean,final int position) {
        showProgress(context);
        LessonDataManager.requestPutLessonOnShelves(context, bean.id, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);

                //如果是已经实名认证的用户开的课，上架成功后，自动通过，不需要审核
                if (AccountDataManager.isVerified(context)) {

                    if (TextUtils.isEmpty(bean.ticket)) {
                        //如果没有ticket，需要重新调用接口，刷新课的信息。
                        updateData(context,false);

                    }else {
                        bean.state=LessonState.PENDING_FOR_LIVE;
                        ToastUtil.showToast(context, R.string.shelves_ok);
                        updateData(context,true);
                    }

                }else {
                    bean.state=LessonState.PENDING_FOR_APPROVAL;
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

    //编辑（区分公开课和班课）
    private void edit(Activity context,CLesson bean) {
        if(Account.TypeName.STAND_ALONE_LESSON.equals(bean.type)){
            Intent intent = new Intent(context, LessonCreationActivity.class);
            intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.id);
            intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_EDIT);
            context.startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
        }else {
            Intent intent = new Intent(context, EditTimetableActivity.class);
            intent.putExtra(ClassInfoActivity.EXTRA_CLASSID,bean.classInfo.id);
            intent.putExtra(EditTimetableActivity.EXTRA_C_LESSON,bean);
            context.startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
        }

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

    //同意或者拒绝
    private void dealAck(final Activity context, final int position, final CLesson bean, final int descion) {

        DealAck ack = new DealAck();
        ack.decision = descion;

        showProgress(context);
        LessonDataManager.acknowledgeLesson(context, bean.id, ack, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);
                if (descion == Ctl.ACKDecision.ACKNOWLEDGE) {
                    bean.state=LessonState.ACKNOWLEDGED;
                    Toast.makeText(context, "您已同意", Toast.LENGTH_SHORT).show();
                    updateJustItem(context,position,bean);

                } else {
                    Toast.makeText(context, "您已拒绝", Toast.LENGTH_SHORT).show();
                    removeJustItem(context,position,bean);
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress(context);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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

    //资料库
    private void databank(Activity context,CLesson cl) {
        if(Account.TypeName.STAND_ALONE_LESSON.equals(cl.type)){
            Intent intent = new Intent(context, ClassMaterialActivity.class);
//        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
            intent.putExtra(ClassMaterialActivity.EXTRA_ID, cl.id);
            intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, cl.title);
            intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Account.TypeName.STAND_ALONE_LESSON.equals(cl.type)?Collaboration.SubType.STANDA_LONE_LESSON:Collaboration.SubType.PRIVATE_CLASS);
            context.startActivity(intent);
        }else if(cl.classInfo!=null){
            Intent intent = new Intent(context, ClassMaterialActivity.class);
//        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
            intent.putExtra(ClassMaterialActivity.EXTRA_ID, cl.classInfo.id);
            intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, cl.classInfo.title);
            intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, Account.TypeName.STAND_ALONE_LESSON.equals(cl.type)?Collaboration.SubType.STANDA_LONE_LESSON:Collaboration.SubType.PRIVATE_CLASS);
            context.startActivity(intent);
        }

    }

    //进入教室
    private void enterClass(Activity context,CLesson data) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, data.classInfo!=null&&!TextUtils.isEmpty(data.classInfo.ticket)?data.classInfo.ticket:data.ticket);
        i.setClass(context, ClassroomActivity.class);
        context.startActivity(i);
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
                    removeJustItem(context, pos, bean);
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
                    removeJustItem(context, pos, bean);
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
    private void share(Context context,CLesson bean) {

        if (bean == null) return;

        String startTime = TimeUtil.format(bean.schedule.getStart().getTime(),
                TimeUtil.TIME_YYYY_MM_DD_HH_MM);

        String name = "";
        if (bean.teacher != null ) {
            name = bean.teacher.name;
        }

        String shareUrl = ApiManager.getShareLessonUrl(bean.id,bean.type);

        ShareUtil.show((Activity) context, bean.title, new StringBuilder(startTime).append("\r\n").append("主讲：").append(name).toString(), shareUrl);
    }

    //报名注册
    private void registration(Context context,CLesson bean) {
        Schedule schedule = bean.schedule;
        long start = (schedule != null && schedule.getStart() != null) ? schedule.getStart().getTime() : 0;
        LessonBusiness.enterEnrollRegisterPage(context,
                bean.id,
                "",
                bean.title,
                start,
                schedule != null ? schedule.getDuration() : 0);
    }

    //发布到主页
    private void publish(final Activity context, final CLesson bean) {
        if (bean.accessible) {
            cancelPublish(context,bean);
            return;
        }
        showProgress(context);
        LessonDataManager.requestToggleAccessLesson(context, bean.id, true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);
                bean.accessible = true;
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
    private void cancelPublish(final Activity context, final CLesson bean) {
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
                LessonDataManager.requestToggleAccessLesson(context, bean.id, false, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress(context);
                        bean.accessible = false;
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
    private void lessonAgain(Activity context,CLesson bean) {
        Intent intent = new Intent(context, LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.id);
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_AGAIN);
        context.startActivityForResult(intent, CourseConstant.CODE_LESSON_AGAIN);
    }

    private void rejectReason(Activity context, CLesson bean){
        Common2Dialog dialog=new Common2Dialog(context);
        dialog.setTitle("取消原因");
        dialog.setDesc("原因：不知道");
        dialog.show();
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
