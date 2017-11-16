package cn.xiaojs.xma.ui.lesson;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/14
 * Desc:
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CancelReason;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.schedule.AddLessonScheduleFragment;
import cn.xiaojs.xma.ui.widget.LimitInputBox;
import cn.xiaojs.xma.util.ToastUtil;

public class CancelLessonFragment extends BottomSheetFragment
        implements DialogInterface.OnKeyListener {

    @BindView(R.id.cancel_lesson_origin_name)
    TextView mName;
    @BindView(R.id.cancel_lesson_reason)
    LimitInputBox mInput;

    private ScheduleLesson lesson;

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_cancel_lesson_schedule, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    protected void initView() {
        Bundle data = getArguments();
        if (data != null) {
            lesson = (ScheduleLesson) data.getSerializable(CourseConstant.KEY_LESSON_BEAN);
            if (lesson != null) {
                mName.setText(lesson.title);
            }
        }
        mInput.setHint(getString(R.string.cancel_reason_hint));
    }

    @OnClick({R.id.iv_back, R.id.cancel_lesson_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                dismiss();
                break;
            case R.id.cancel_lesson_ok://确定取消
                cancelClassLesson();
                break;
            default:
                break;
        }
    }


    private void cancelClassLesson(){
        String input = mInput.getInput().getText().toString();
        if (TextUtils.isEmpty(input)){
            ToastUtil.showToast(getActivity(),R.string.cancel_reason_hint);
            return;
        }
        if (lesson != null){
            CancelReason reason = new CancelReason();
            reason.setReason(input);
            LessonDataManager.cancelClassesLesson(getActivity(), ClassroomEngine.getEngine().getCtlSession().cls.id, lesson.id, reason, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress();
                    ToastUtil.showToast(getActivity(),"成功取消上课!");
                    if(getTargetFragment()!=null){
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,new Intent());
                    }
                    dismiss();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    ToastUtil.showToast(getActivity(),errorMessage);
                }
            });

            showProgress(true);
        }
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    public static CancelLessonFragment createInstance(ScheduleLesson lesson){
        CancelLessonFragment fragment=new CancelLessonFragment();
        Bundle data=new Bundle();
        if(lesson!=null){
            data.putSerializable(CourseConstant.KEY_LESSON_BEAN,lesson);
        }
        fragment.setArguments(data);
        return fragment;
    }

}
