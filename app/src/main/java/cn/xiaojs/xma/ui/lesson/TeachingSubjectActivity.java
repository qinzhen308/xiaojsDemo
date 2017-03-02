package cn.xiaojs.xma.ui.lesson;

import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.account.CompetencySubject;
import cn.xiaojs.xma.ui.base.BaseActivity;

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
 * Desc: 教学能力声明科目选择
 *
 * ======================================================================================== */

public class TeachingSubjectActivity extends BaseActivity {
    public final static String KEY_TYPE = "key_type";

    private List<TeachingSubjectFragment> mFragments;

    @Override
    protected void addViewContent() {
        needHeader(false);
        initData();
        initPage();
    }

    private void initData() {
        mFragments = new ArrayList<TeachingSubjectFragment>();
    }

    private void initPage() {
        //mContent
        TeachingSubjectFragment subjectFragment = new TeachingSubjectFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.base_content, subjectFragment).commit();
        mFragments.add(subjectFragment);
    }

    private void enterNextPage(CSubject subject) {
        TeachingSubjectFragment subjectFragment = new TeachingSubjectFragment();
        subjectFragment.setParentSubject(subject);
        getSupportFragmentManager().beginTransaction().add(R.id.base_content, subjectFragment).commit();
        mFragments.add(subjectFragment);
    }

    public void onBackBtnClicked() {
        exitCurrentPage();
    }

    public void onSubjectSelected(CSubject subject) {
        enterNextPage(subject);
    }

    public void onFinishBtnClicked(CSubject subject) {
        if (subject == null) {
            Toast.makeText(this, R.string.subject_not_select, Toast.LENGTH_SHORT).show();
        } else {
            finishWithResult(subject);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!exitCurrentPage()) {
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean exitCurrentPage() {
        if (mFragments != null && !mFragments.isEmpty()) {
            if (mFragments.size() == 1) {
                //退出页面
                finishWithResult(null);
                return true;
            } else {
                //退回上一级页面
                TeachingSubjectFragment fragment = mFragments.remove(mFragments.size() - 1);
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

                return false;
            }
        }

        return true;
    }

    private void finishWithResult(CSubject subject) {

        if (subject == null){
            finish();
            return;
        }

        CompetencyParams params = new CompetencyParams();
        params.setSubject(subject.getId());

        showProgress(false);
        AccountDataManager.requestClaimCompetency(this, params, new APIServiceCallback<CompetencySubject>() {
            @Override
            public void onSuccess(CompetencySubject object) {

                cancelProgress();
                Toast.makeText(TeachingSubjectActivity.this, R.string.claim_succ, Toast.LENGTH_SHORT).show();
                SecurityManager.updatePermission(TeachingSubjectActivity.this, Su.Permission.COURSE_OPEN_CREATE, true);

                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(TeachingSubjectActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

//        Intent intent = new Intent();
//        intent.putExtra(TeachingAbilityActivity.KEY_SUBJECT, subject);
//        setResult(RESULT_OK, intent);

    }
}
