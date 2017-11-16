package cn.xiaojs.xma.ui.classroom2.schedule;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;


import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.ui.base.AbsOpModel;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.lesson.CancelLessonFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.ModifyLessonActivity;
import cn.xiaojs.xma.ui.lesson.xclass.EditTimetableFragment;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.widget.CommonDialog;
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

    }

    @Override
    public void onClick(Fragment fragment, ScheduleLesson data, int position) {
        switch (getId()){

            case OP_CANCEL_LESSON:
                cancelLesson(fragment,data);
                break;
            case OP_CANCEL_SUBMIT:

                break;
            case OP_DELETE:
                if(data==null){
                    deleteNativeLesson(fragment,position);
                }else {
                    delete(fragment,position,data);
                }
                break;
            case OP_EDIT:
                edit(fragment, data);
                break;

            case OP_MODIFY_TIME:
                modifyLesson(fragment,data);
                break;

        }
    }

    private void showProgress(Fragment fragment){
        if(fragment instanceof IDialogMethod){
            ((IDialogMethod) fragment).showProgress(false);
        }
    }

    private void cancelProgress(Fragment fragment){
        if(fragment instanceof IDialogMethod){
            ((IDialogMethod) fragment).cancelProgress();
        }
    }

    //统一刷新列表，因为精准刷新在这里不太好实现
    //这个类的存在是为了封装所有操作，以适用很多场景，但精准刷新会加重耦合，使得这个类毫无意义
    //后续优化
    private void updateData(Fragment fragment,boolean justNative){
        ((IUpdateMethod)fragment).updateData(justNative,1);
    }

    private void updateJustItem(Fragment fragment,int position,ScheduleLesson cLesson){
        ((IUpdateMethod)fragment).updateItem(position,cLesson);
    }

    private void removeJustItem(Fragment fragment,int position,ScheduleLesson cLesson){
        ((IUpdateMethod)fragment).updateItem(position,cLesson,"remove");
    }




    //编辑（区分公开课和班课）
    private void edit(Fragment fragment,ScheduleLesson bean) {
        EditTimetableFragment target=EditTimetableFragment.createInstance(bean);
        target.setTargetFragment(fragment,CourseConstant.CODE_EDIT_LESSON);
        target.show(fragment.getFragmentManager(),"edit_class");
    }



    private void modifyLesson(Fragment fragment,ScheduleLesson bean) {
        Intent intent = new Intent(fragment.getActivity(), ModifyLessonActivity.class);
        TeachLesson tl=new TeachLesson();
        tl.setId(bean.id);
        tl.setTitle(bean.title);
        tl.setSchedule(bean.schedule);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, tl);
        fragment.startActivityForResult(intent, CourseConstant.CODE_EDIT_LESSON);
    }

    //取消上课
    private void cancelLesson(Fragment fragment,ScheduleLesson bean) {
        CancelLessonFragment target=CancelLessonFragment.createInstance(bean);
        target.setTargetFragment(fragment,CourseConstant.CODE_CANCEL_LESSON);
        target.show(fragment.getFragmentManager(),"cancel_lesson");
    }

    //删除
    private void delete(final Fragment fragment,final int pos,final ScheduleLesson bean) {
        final CommonDialog dialog = new CommonDialog(fragment.getActivity());
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
                hideLesson(fragment,pos, bean);
            }
        });
        dialog.show();
    }

    private void hideLesson(final Fragment fragment, final int pos, final ScheduleLesson bean) {
        showProgress(fragment);
        LessonDataManager.deleteClassesLesson(fragment.getActivity(), ClassroomEngine.getEngine().getCtlSession().cls.id, bean.id, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress(fragment);
                ToastUtil.showToast(fragment.getContext(), R.string.delete_success);
                removeJustItem(fragment, pos, bean);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress(fragment);
                Toast.makeText(fragment.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //删除
    private void deleteNativeLesson(final Fragment fragment,final int pos) {
        ((IUpdateMethod)fragment).updateItem(pos,null,"remove");
    }

}
