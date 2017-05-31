package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.grade.ClassMaterialActivity;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.CancelLessonActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonBusiness;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LiveLessonDetailActivity;
import cn.xiaojs.xma.ui.lesson.ModifyLessonActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassInfoActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.LessonScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.widget.CommonDialog;
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
    //撤销确认
    public static final int OP_CANCEL_SUBMIT=2;
    public static final int OP_CLASS_INFO=3;
    public static final int OP_DATABASE=4;
    public static final int OP_DELETE=5;
    public static final int OP_EDIT=6;
    public static final int OP_ENTER=7;
    public static final int OP_ENTER_2=8;
    public static final int OP_LOOK=9;
    public static final int OP_MODIFY_TIME=10;
    public static final int OP_PRIVATE=11;
    public static final int OP_PUBLIC=12;
    public static final int OP_PUBLISH=13;
    public static final int OP_RECREATE_LESSON=14;
    public static final int OP_SCHEDULE=15;
    public static final int OP_SHARE=16;
    public static final int OP_SIGNUP=17;
    public static final int OP_SUBMIT=18;
    //撤销审核
    public static final int OP_CANCEL_CHECK=19;
    //橘黄色的分享按钮
    public static final int OP_SHARE2=20;

    
    
    private int id;

    public LOpModel(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public void onClick(Activity context,Object data){
        switch (id){
            case OP_APPLY:

                break;
            case OP_CANCEL_LESSON:

                break;
            case OP_CANCEL_SUBMIT:

                break;
            case OP_CLASS_INFO:
                if(data instanceof CLesson) {
                    classInfo(context, ((CLesson) data).classInfo.id);
                }
                break;
            case OP_DATABASE:
//                enterDatabase(context);
                if(data instanceof CLesson) {
                    databank(context,((CLesson) data).title ,((CLesson) data).id);
                }
                break;
            case OP_DELETE:

                break;
            case OP_EDIT:
//                editLesson(context);
                if(data instanceof CLesson){
                    edit(context,((CLesson) data).id);
                }
                break;
            case OP_ENTER:
                enterSchedule(context);
                break;
            case OP_ENTER_2:
                enterSchedule(context);
                break;
            case OP_LOOK:
                if(data instanceof CLesson){
                    detail(context,((CLesson) data).id);
                }
                break;
            case OP_MODIFY_TIME:
                break;
            case OP_PRIVATE:
                break;
            case OP_PUBLIC:
                break;
            case OP_PUBLISH:
                break;
            case OP_RECREATE_LESSON:
                break;
            case OP_SCHEDULE:
                break;
            case OP_SHARE:
                share(context);
                break;
            case OP_SIGNUP:

                break;
            case OP_SUBMIT:
                break;
            case OP_CANCEL_CHECK:
                if(data instanceof CLesson){
                    offShelves(context,((CLesson) data).id);
                }
                break;
            case OP_SHARE2:
                share(context);
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


    public static void editLesson(Activity context){
        Intent intent=new Intent(context,LessonCreationActivity.class);
        context.startActivity(intent);
    }

    public static void share(Activity context){
        ShareUtil.show(context,"标题test","test内容","https//baidu.com");
    }

    public static void enterSchedule(Activity context){
        Intent intent=new Intent(context,ClassScheduleActivity.class);
        context.startActivity(intent);
    }

    public void enterDatabase(Activity context){
        Intent intent=new Intent(context,MaterialActivity.class);
        intent.putExtra(MaterialActivity.KEY_IS_MINE,false);
        context.startActivity(intent);
    }

    public void classInfo(Activity context,String classId){
        Intent intent=new Intent(context,ClassInfoActivity.class);
        intent.putExtra(ClassInfoActivity.EXTRA_CLASSID,classId);
        context.startActivity(intent);
    }


    //上架
    private void shelves(final Activity context,final TeachLesson bean) {
        showProgress(context);
        LessonDataManager.requestPutLessonOnShelves(context, bean.getId(), new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);

                //如果是已经实名认证的用户开的课，上架成功后，自动通过，不需要审核
                if (AccountDataManager.isVerified(context)) {

                    if (TextUtils.isEmpty(bean.getTicket())) {
                        //如果没有ticket，需要重新调用接口，刷新课的信息。
//                        request(mCriteria);
                        updateData(context,true);

                    }else {
//                        bean.setState(LessonState.PENDING_FOR_LIVE);
                        ToastUtil.showToast(context, R.string.shelves_ok);

//                        notifyData(bean);
                        updateData(context,true);
                    }

                }else {
                    bean.setState(LessonState.PENDING_FOR_APPROVAL);
                    ToastUtil.showToast(context, R.string.shelves_need_examine);
                    updateData(context,true);
//                    notifyData(bean);
                }



            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress(context);
                ToastUtil.showToast(context, errorMessage);
            }
        });
    }

    //编辑
    private void edit(Activity context,String id) {
        Intent intent = new Intent(context, LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, id);
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_EDIT);
        ((BaseActivity) context).startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //撤销审核，取消上架
    private void offShelves(final Activity context, final String id) {
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
                LessonDataManager.requestCancelLessonOnShelves(context, id, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress(context);
//                        bean.setState(LessonState.DRAFT);
//                        notifyData(bean);

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
    private void dealAck(final Activity context, final int position, final TeachLesson bean, final int descion) {

        DealAck ack = new DealAck();
        ack.decision = descion;

        showProgress(context);
        LessonDataManager.acknowledgeLesson(context, bean.getId(), ack, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);
                if (descion == Ctl.ACKDecision.ACKNOWLEDGE) {
                    bean.setState(LessonState.ACKNOWLEDGED);
                    Toast.makeText(context, "您已同意", Toast.LENGTH_SHORT).show();
//                    notifyData(bean);
                    updateData(context,true);

                } else {
                    Toast.makeText(context, "您已拒绝", Toast.LENGTH_SHORT).show();
//                    getList().remove(position);
//                    notifyDataSetChanged();
                    updateData(context,true);

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
    private void databank(Activity context,String title,String id) {
        Intent intent = new Intent(context, ClassMaterialActivity.class);
        intent.putExtra(ClassMaterialActivity.EXTRA_DELETEABLE,true);
        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_ID, id);
        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_NAME, title);
        context.startActivity(intent);
    }

    //进入教室
    private void enterClass(Activity context,TeachLesson bean) {
        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, bean.getTicket());
        i.setClass(context, ClassroomActivity.class);
        context.startActivity(i);
    }

    private void modifyLesson(Activity context,TeachLesson bean) {
        Intent intent = new Intent(context, ModifyLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        ((BaseActivity) context).startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //取消上课
    private void cancelLesson(Activity context,TeachLesson bean) {
        Intent intent = new Intent(context, CancelLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        ((BaseActivity) context).startActivityForResult(intent, CourseConstant.CODE_CANCEL_LESSON);
    }

    //删除
    private void delete(final Activity context,final int pos,final TeachLesson bean) {
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

    private void hideLesson(final Activity context, final int pos, final TeachLesson bean) {
        showProgress(context);
        LessonDataManager.hideLesson(context, bean.getId(), new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);
//                removeItem(pos);
                ToastUtil.showToast(context, R.string.delete_success);
                updateData(context,true);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress(context);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
    private void share(Context context,TeachLesson bean) {

        if (bean == null) return;

        String startTime = TimeUtil.format(bean.getSchedule().getStart().getTime(),
                TimeUtil.TIME_YYYY_MM_DD_HH_MM);

        String name = "";
        if (bean.getTeacher() != null && bean.getTeacher().getBasic() != null) {
            name = bean.getTeacher().getBasic().getName();
        }

        String shareUrl = ApiManager.getShareLessonUrl(bean.getId());

        ShareUtil.show((Activity) context, bean.getTitle(), new StringBuilder(startTime).append("\r\n").append(name).toString(), shareUrl);
    }

    //报名注册
    private void registration(Context context,TeachLesson bean) {
        Schedule schedule = bean.getSchedule();
        long start = (schedule != null && schedule.getStart() != null) ? schedule.getStart().getTime() : 0;
        LessonBusiness.enterEnrollRegisterPage(context,
                bean.getId(),
                bean.getCover(),
                bean.getTitle(),
                start,
                schedule != null ? schedule.getDuration() : 0);
    }

    //发布到主页
    private void publish(final Activity context, final TeachLesson bean) {
        if (bean.getPublish().accessible) {
            cancelPublish(context,bean);
            return;
        }
        showProgress(context);
        LessonDataManager.requestToggleAccessLesson(context, bean.getId(), true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(context);
                bean.getPublish().accessible = true;
                ToastUtil.showToast(context, R.string.lesson_publish_tip);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress(context);
                ToastUtil.showToast(context, errorMessage);
            }
        });
    }

    //取消发布
    private void cancelPublish(final Activity context, final TeachLesson bean) {
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
                LessonDataManager.requestToggleAccessLesson(context, bean.getId(), false, new APIServiceCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        cancelProgress(context);
                        bean.getPublish().accessible = false;
                        ToastUtil.showToast(context, R.string.course_state_cancel);
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
    private void lessonAgain(Activity context,TeachLesson bean) {
        Intent intent = new Intent(context, LessonCreationActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_ID, bean.getId());
        intent.putExtra(CourseConstant.KEY_TEACH_ACTION_TYPE, CourseConstant.TYPE_LESSON_AGAIN);
        ((BaseActivity) context).startActivityForResult(intent, CourseConstant.CODE_LESSON_AGAIN);
    }
}
