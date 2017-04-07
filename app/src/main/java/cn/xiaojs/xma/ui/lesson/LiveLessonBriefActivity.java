package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class LiveLessonBriefActivity extends BaseActivity {
    private final static int MAX_CHAR = 500;
    @BindView(R.id.input_content)
    EditText mInputContentEdt;
    @BindView(R.id.input_tips)
    TextView mInputTipsTv;

    private LiveLesson mLesson;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.live_lesson_brief);
        addView(R.layout.layout_edit_info);

        init();
    }

    private void init() {
        mInputContentEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHAR)});
        mInputContentEdt.setHint(R.string.live_lesson_brief_hint);

        mInputTipsTv.setText(Html.fromHtml(String.format(getString(R.string.input_tips), 0, MAX_CHAR)));
        mInputContentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Spanned tips = Html.fromHtml(String.format(getString(R.string.input_tips), s.length(), MAX_CHAR));
                mInputTipsTv.setText(tips);
            }
        });

        Object object = getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO);
        if (object instanceof LiveLesson) {
            mLesson = (LiveLesson) object;
            if (mLesson.getOverview() != null) {
                String text = mLesson.getOverview().getText();
                mInputContentEdt.setText(text);
                if (!TextUtils.isEmpty(text)) {
                    mInputContentEdt.setSelection(text.length());
                }
            }
        }
    }

    @OnClick({R.id.left_image, R.id.sub_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.sub_btn:
                String content = mInputContentEdt.getText().toString().trim();
                if (!TextUtils.isEmpty(content) && mLesson != null) {
                    LiveLesson.Overview overview = new LiveLesson.Overview();
                    overview.setText(content);
                    mLesson.setOverview(overview);
                    Intent i = new Intent();
                    i.putExtra(CourseConstant.KEY_LESSON_OPTIONAL_INFO, mLesson);
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
