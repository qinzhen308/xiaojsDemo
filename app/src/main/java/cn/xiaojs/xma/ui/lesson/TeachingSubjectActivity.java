package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cn.xiaojs.xma.R;
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
 * Desc:选择学科类目，教学能力
 *
 * ======================================================================================== */

public class TeachingSubjectActivity extends BaseActivity {
    private List<TeachingSubjectFragment> mFragments;

    @Override
    protected void addViewContent() {
        needHeader(false);

        mFragments = new ArrayList<TeachingSubjectFragment>();
        initPage();
    }

    private void initPage() {
        //mContent
        TeachingSubjectFragment subjectFragment = new TeachingSubjectFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.base_content, subjectFragment).commit();
        mFragments.add(subjectFragment);
    }

    private void enterNextPage() {
        TeachingSubjectFragment subjectFragment = new TeachingSubjectFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.base_content, subjectFragment).commit();
    }

    public void onBackClicked() {
        exitCurrentPage();
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
                finishWithResult();
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

    private void finishWithResult() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }
}
