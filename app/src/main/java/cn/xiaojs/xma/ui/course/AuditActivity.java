package cn.xiaojs.xma.ui.course;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/8
 * Desc:
 *
 * ======================================================================================== */

public class AuditActivity extends BaseActivity {
    @BindView(R.id.visible_to_stu)
    ToggleButton mVisibleToStu;
    @BindView(R.id.audit_select)
    TextView mSitInOnPerson;

    private LiveLesson mLesson;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.audit_title);

        addView(R.layout.activity_audit);

        init();
    }

    private void init() {
        Object object = getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO);
        if (object instanceof LiveLesson) {
            mLesson = (LiveLesson) object;
        }
    }

    @OnClick({R.id.left_image, R.id.sub_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.audit_portrait:
            case R.id.audit_select:
                //select sit in person
                break;
            case R.id.sub_btn:
                String selectTip = getString(R.string.please_select);
                String persons = mSitInOnPerson.getText().toString();
                if (!mVisibleToStu.isChecked() && (TextUtils.isEmpty(persons) || selectTip.equals(persons) || persons.split(",") == null)) {
                    finish();
                } else {
                    LiveLesson.Audit audit = new LiveLesson.Audit();
                    audit.setEnabled(true);
                    audit.setVisibleToStudents(mVisibleToStu.isSelected());
                    //audit.setGrantedTo(persons.split(","));
                    mLesson.setAudit(audit);
                    Intent i = new Intent();
                    i.putExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO, mLesson);
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
