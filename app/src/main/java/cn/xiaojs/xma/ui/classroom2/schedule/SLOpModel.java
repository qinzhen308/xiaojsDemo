package cn.xiaojs.xma.ui.classroom2.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Date;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.ui.base.AbsOpModel;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.common.ShareBeautifulQrcodeActivity;
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
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.widget.Common2Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/11/2.
 */

public class SLOpModel extends AbsOpModel<ScheduleLesson> {



    public SLOpModel(int id){
        super(id);
    }


    @Override
    public void onClick(Activity context,ScheduleLesson data,int position){
        switch (getId()){

            case OP_CANCEL_LESSON:
                cancelLesson(context,data);
                break;
            case OP_CANCEL_SUBMIT:

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

            case OP_MODIFY_TIME:
                modifyLesson(context,data);
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
        ((IUpdateMethod)context).updateData(justNative,1);
    }

    private void updateJustItem(Activity context,int position,ScheduleLesson cLesson){
        ((IUpdateMethod)context).updateItem(position,cLesson);
    }

    private void removeJustItem(Activity context,int position,ScheduleLesson cLesson){
        ((IUpdateMethod)context).updateItem(position,cLesson,"remove");
    }




    //编辑（区分公开课和班课）
    private void edit(Activity context,ScheduleLesson bean) {

        Intent intent = new Intent(context, EditTimetableActivity.class);
        intent.putExtra(ClassInfoActivity.EXTRA_CLASSID,ClassroomEngine.getEngine().getCtlSession().cls.id);
        intent.putExtra(EditTimetableActivity.EXTRA_C_LESSON,bean);
        context.startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }



    private void modifyLesson(Activity context,ScheduleLesson bean) {
        Intent intent = new Intent(context, ModifyLessonActivity.class);
        TeachLesson tl=new TeachLesson();
        tl.setId(bean.id);
        tl.setTitle(bean.title);
        tl.setSchedule(bean.schedule);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, tl);
        context.startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //取消上课
    private void cancelLesson(Activity context,ScheduleLesson bean) {
        Intent intent = new Intent(context, CancelLessonActivity.class);
        CLesson cLesson=new CLesson();
        cLesson.id=bean.id;
        cLesson.title=bean.title;
        cLesson.type=bean.typeName;
        cLesson.classInfo=new ClassInfo();
        cLesson.classInfo.id= ClassroomEngine.getEngine().getCtlSession().cls.id;
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, cLesson);
        context.startActivityForResult(intent, CourseConstant.CODE_CANCEL_LESSON);
    }

    //删除
    private void delete(final Activity context,final int pos,final ScheduleLesson bean) {
        final CommonDialog dialog = new CommonDialog(context);
        dialog.setTitle(R.string.delete);
        dialog.setDesc(R.string.delete_lesson_tip);
        dialog.setRightBtnText(R.string.delete);
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

    private void hideLesson(final Activity context, final int pos, final ScheduleLesson bean) {
        showProgress(context);
        LessonDataManager.deleteClassesLesson(context, ClassroomEngine.getEngine().getCtlSession().cls.id, bean.id, new APIServiceCallback() {
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

    //删除
    private void deleteNativeLesson(final Activity context,final int pos) {
        ((IUpdateMethod)context).updateItem(pos,null,"remove");
    }

}
