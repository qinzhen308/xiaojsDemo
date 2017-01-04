package cn.xiaojs.xma.ui.classroom;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.XjsUtils;

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
 * Date:2017/1/4
 * Desc:
 *
 * ======================================================================================== */

public class SettingDialog extends Dialog implements CompoundButton.OnCheckedChangeListener, Constants{
    private ToggleButton mCameraBtn;
    private ToggleButton mMicroPhoneBtn;

    public SettingDialog(Context context) {
        super(context, R.style.CommonDialog);
        init();
    }

    public SettingDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected SettingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }


    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.layout_classroom_setting_dialog);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);

        mCameraBtn = (ToggleButton) findViewById(R.id.camera_switcher);
        mMicroPhoneBtn = (ToggleButton) findViewById(R.id.microphone_switcher);

        mCameraBtn.setOnCheckedChangeListener(this);
        mCameraBtn.setOnCheckedChangeListener(this);

        //default
        mCameraBtn.setChecked(true);
        mMicroPhoneBtn.setChecked(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = XjsUtils.getSharedPreferences().edit();
        switch (buttonView.getId()) {
            case R.id.camera_switcher:
                editor.putBoolean(KEY_CAMERA_OPEN, isChecked);
                break;
            case R.id.microphone_switcher:
                editor.putBoolean(KEY_MICROPHONE_OPEN, isChecked);
                break;
        }
        editor.commit();
    }
}
