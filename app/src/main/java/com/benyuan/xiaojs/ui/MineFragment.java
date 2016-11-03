package com.benyuan.xiaojs.ui;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:hy
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.mine.SettingsActivity;

import butterknife.OnClick;

public class MineFragment extends BaseFragment {

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
    }

    @OnClick({R.id.settings})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                break;
            case R.id.my_page:
                break;
            case R.id.my_course:
                break;
            case R.id.my_course_schedule:
                break;

            case R.id.my_ask_questions:
                break;
            case R.id.teach_ability_layout:
                break;
            case R.id.my_collections:
                break;
            case R.id.eval_management:
                break;
            case R.id.feedback_help:
                break;
            case R.id.settings:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            default:
                break;
        }
    }
}
