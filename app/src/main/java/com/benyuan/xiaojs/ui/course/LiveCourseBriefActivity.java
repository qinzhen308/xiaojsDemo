package com.benyuan.xiaojs.ui.course;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;

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

public class LiveCourseBriefActivity extends BaseActivity {
    private final static int MAX_CHAR = 200;
    @BindView(R.id.input_content)
    EditText mInputContentEdt;
    @BindView(R.id.input_tips)
    TextView mInputTipsTv;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.live_course_brief);
        addView(R.layout.layout_edit_info);

        init();
    }

    private void init() {
        mInputContentEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHAR)});
        mInputContentEdt.setHint(R.string.live_course_brief_hint);

        mInputTipsTv.setText(String.format(getString(R.string.input_tips), 0, MAX_CHAR));

        mInputContentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mInputTipsTv.setText(String.format(getString(R.string.input_tips), s.length(), MAX_CHAR));
            }
        });
    }

    @OnClick({R.id.left_image, R.id.sub_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.sub_btn:
                break;
            default:
                break;
        }
    }
}
