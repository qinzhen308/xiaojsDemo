package cn.xiaojs.xma.ui.classroom;
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
 * Date:2016/11/28
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cn.xiaojs.xma.R;

public class SettingPanel extends Panel implements View.OnClickListener{
    private TextView mFluentTv;
    private TextView mStandardTv;
    private TextView mHighTv;

    private ToggleButton mMicroPhoneSwitcher;
    private ToggleButton mCameraSwitcher;
    private ToggleButton mChatSwitcher;

    public SettingPanel(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_setting, null);
    }

    @Override
    public void initChildView(View root) {
        mFluentTv = (TextView) root.findViewById(R.id.fluent);
        mStandardTv = (TextView) root.findViewById(R.id.standard_definition);
        mHighTv = (TextView) root.findViewById(R.id.high_definition);

        mMicroPhoneSwitcher = (ToggleButton) root.findViewById(R.id.microphone_switcher);
        mCameraSwitcher = (ToggleButton) root.findViewById(R.id.camera_switcher);
        mChatSwitcher = (ToggleButton) root.findViewById(R.id.class_room_chat);

        mFluentTv.setOnClickListener(this);
        mStandardTv.setOnClickListener(this);
        mHighTv.setOnClickListener(this);

    }

    @Override
    public void initData() {
        //default fluent
        mFluentTv.setSelected(true);

        mMicroPhoneSwitcher.setChecked(true);
        mCameraSwitcher.setChecked(true);
        mChatSwitcher.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        reset();
        if (!v.isSelected()) {
            v.setSelected(true);
        }

        switch (v.getId()) {
            case R.id.fluent:
                break;
            case R.id.standard_definition:
                break;
            case R.id.high_definition:
                break;
        }
    }

    private void reset() {
        mFluentTv.setSelected(false);
        mStandardTv.setSelected(false);
        mHighTv.setSelected(false);
    }
}
