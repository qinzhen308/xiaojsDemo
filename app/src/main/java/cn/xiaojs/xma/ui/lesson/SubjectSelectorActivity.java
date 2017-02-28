package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.ClaimCompetency;
import cn.xiaojs.xma.model.Competency;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.mine.TeachingAbilityActivity;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/2/28
 * Desc:
 *
 * ======================================================================================== */

public class SubjectSelectorActivity extends BaseActivity {
    @BindView(R.id.subject_list)
    ListView mSubjectListView;

    private SubjectSelectorAdapter mSubjectAdapter;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.subject_category_select);
        setRightText(R.string.finish);
        mRightText.setTextColor(getResources().getColor(R.color.font_orange));

        addView(R.layout.activity_select_subject);
        loadData();
    }

    private void initView () {

    }

    @OnClick({R.id.left_image, R.id.right_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_view:
                finishWithResult();
                break;
            default:
                break;
        }
    }

    private void loadData() {
        showProgress(true);
        AccountDataManager.getCompetencies(this, new APIServiceCallback<ClaimCompetency>() {
            @Override
            public void onSuccess(ClaimCompetency object) {
                cancelProgress();
                if (object != null) {
                    mSubjectAdapter = new SubjectSelectorAdapter(SubjectSelectorActivity.this, object.competencies);
                    mSubjectListView.setAdapter(mSubjectAdapter);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
    }

    private void finishWithResult() {
        Competency competency = null;
        if (mSubjectAdapter != null) {
            competency = mSubjectAdapter.getSelectedCompetency();
        }
        if (competency == null) {
            Toast.makeText(this, R.string.subject_not_select, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(LessonCreationActivity.KEY_COMPETENCY, competency);
        setResult(RESULT_OK, intent);
        finish();
    }
}
