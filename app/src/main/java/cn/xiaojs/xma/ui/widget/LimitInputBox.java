package cn.xiaojs.xma.ui.widget;
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

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LimitInputBox extends FrameLayout {

    @BindView(R.id.limit_input)
    EditText mInput;
    @BindView(R.id.limit_counter)
    TextView mCounter;

    private int total = 100;

    public LimitInputBox(Context context) {
        super(context);
        init();
    }

    public LimitInputBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LimitInputBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LimitInputBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_limit_input_box, this, true);
        ButterKnife.bind(this);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCounter(s.length());
            }
        });
        setFilter();
        updateCounter(0);
    }

    private void updateCounter(int len) {
        if (len <= total) {
            StringBuilder sb = new StringBuilder();
            sb.append(len);
            sb.append('/');
            sb.append(total);
            sb.append("字");
            mCounter.setText(sb);
        }
    }

    private void setFilter(){
        mInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(total)});
    }

    public void setLimit(int limit) {
        total = limit;
        setFilter();
    }

    public EditText getInput(){
        return mInput;
    }

    public void setHint(String hint){
        mInput.setHint(hint);
    }
}
