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

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CancelReason;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.LimitInputBox;
import cn.xiaojs.xma.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class CancelLessonActivity extends BaseActivity {

    @BindView(R.id.cancel_lesson_origin_name)
    TextView mName;
    @BindView(R.id.cancel_lesson_reason)
    LimitInputBox mInput;

    private CLesson lesson;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_cancel_lesson);
        Intent intent = getIntent();
        if (intent != null) {
            lesson = (CLesson) intent.getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
            if (lesson != null) {
                mName.setText(lesson.title);
            }
        }
        setMiddleTitle(R.string.cancel_lesson);
        mInput.setHint(getString(R.string.cancel_reason_hint));
    }

    @OnClick({R.id.left_image, R.id.cancel_lesson_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.cancel_lesson_ok://确定取消
                if(Account.TypeName.STAND_ALONE_LESSON.equals(lesson.type)){
                    cancel();
                }else {
                    cancelClassLesson();
                }
                break;
            default:
                break;
        }
    }

    private void cancel(){
        String input = mInput.getInput().getText().toString();
        if (TextUtils.isEmpty(input)){
            ToastUtil.showToast(this,R.string.cancel_reason_hint);
            return;
        }
        if (lesson != null){
            CancelReason reason = new CancelReason();
            reason.setReason(input);
            LessonDataManager.requestCancelLesson(this, lesson.id, reason, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress();
                    ToastUtil.showToast(CancelLessonActivity.this,"成功取消上课!");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    ToastUtil.showToast(CancelLessonActivity.this,errorMessage);
                }
            });

            showProgress(true);
        }
    }


    private void cancelClassLesson(){
        String input = mInput.getInput().getText().toString();
        if (TextUtils.isEmpty(input)){
            ToastUtil.showToast(this,R.string.cancel_reason_hint);
            return;
        }
        if (lesson != null&&lesson.classInfo!=null){
            CancelReason reason = new CancelReason();
            reason.setReason(input);
            LessonDataManager.cancelClassesLesson(this, lesson.classInfo.id, lesson.id, reason, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress();
                    ToastUtil.showToast(CancelLessonActivity.this,"成功取消上课!");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    ToastUtil.showToast(CancelLessonActivity.this,errorMessage);
                }
            });

            showProgress(true);
        }
    }


}
