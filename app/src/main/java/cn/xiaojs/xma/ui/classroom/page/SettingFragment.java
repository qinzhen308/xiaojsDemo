package cn.xiaojs.xma.ui.classroom.page;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.widget.SheetFragment;
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
 * Date:2017/5/5
 * Desc:
 *
 * ======================================================================================== */

public class SettingFragment extends SheetFragment implements Constants, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.fluent)
    TextView mFluentTv;
    @BindView(R.id.standard_definition)
    TextView mStandardTv;
    @BindView(R.id.high_definition)
    TextView mHighTv;

    @BindView(R.id.microphone_switcher)
    ToggleButton mMicroPhoneSwitcher;
    @BindView(R.id.camera_switcher)
    ToggleButton mCameraSwitcher;
    @BindView(R.id.mobile_network_watch_live)
    ToggleButton mMobileNetworkLiveSwitcher;

    private OnSettingChangedListener mOnSettingChangedListener;

    public void setOnSettingChangedListener(OnSettingChangedListener listener) {
        mOnSettingChangedListener = listener;
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_sliding_setting, null);
    }

    @Override
    protected void onFragmentShow(DialogInterface dialogInterface) {
        setQuality();

        SharedPreferences sf = XjsUtils.getSharedPreferences();
        mMobileNetworkLiveSwitcher.setChecked(sf.getBoolean(KEY_MOBILE_NETWORK_LIVE, false));
        mMicroPhoneSwitcher.setChecked(sf.getBoolean(KEY_MICROPHONE_OPEN, true));
        mCameraSwitcher.setChecked(sf.getBoolean(KEY_CAMERA_OPEN, true));

        mMobileNetworkLiveSwitcher.setOnCheckedChangeListener(this);
        mMicroPhoneSwitcher.setOnCheckedChangeListener(this);
        mCameraSwitcher.setOnCheckedChangeListener(this);
    }

    @Override
    protected View getTargetView(View root) {
        return root.findViewById(R.id.setting_title);
    }

    @OnClick({R.id.fluent, R.id.standard_definition, R.id.high_definition})
    public void onClick(View v) {
        reset();
        if (!v.isSelected()) {
            v.setSelected(true);
        }
        SharedPreferences.Editor editor = XjsUtils.getSharedPreferences().edit();
        int quality = QUALITY_STANDARD;

        switch (v.getId()) {
            case R.id.fluent:
                quality = QUALITY_FLUENT;
                editor.putInt(KEY_QUALITY, QUALITY_FLUENT);
                break;
            case R.id.standard_definition:
                quality = QUALITY_STANDARD;
                editor.putInt(KEY_QUALITY, QUALITY_STANDARD);
                break;
            case R.id.high_definition:
                quality = QUALITY_HIGH;
                editor.putInt(KEY_QUALITY, QUALITY_HIGH);
                break;
            default:
                break;
        }

        editor.apply();

        if (mOnSettingChangedListener != null) {
            mOnSettingChangedListener.onResolutionChanged(quality);
        }
    }

    private void setQuality() {
        SharedPreferences spf = XjsUtils.getSharedPreferences();
        int quality = spf.getInt(KEY_QUALITY, QUALITY_STANDARD);
        switch (quality) {
            case QUALITY_FLUENT:
                mFluentTv.setSelected(true);
                break;
            case QUALITY_STANDARD:
                mStandardTv.setSelected(true);
                break;
            case QUALITY_HIGH:
                mHighTv.setSelected(true);
                break;
        }
    }

    private void reset() {
        mFluentTv.setSelected(false);
        mStandardTv.setSelected(false);
        mHighTv.setSelected(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = XjsUtils.getSharedPreferences().edit();
        switch (buttonView.getId()) {
            case R.id.mobile_network_watch_live:
                editor.putBoolean(KEY_MOBILE_NETWORK_LIVE, isChecked);
                if (mOnSettingChangedListener != null) {
                    mOnSettingChangedListener.onSwitcherChanged(Constants.SWITCHER_MOBILE_NETWORK_LIVE, isChecked);
                }
                break;
            case R.id.camera_switcher:
                editor.putBoolean(KEY_CAMERA_OPEN, isChecked);
                if (mOnSettingChangedListener != null) {
                    mOnSettingChangedListener.onSwitcherChanged(Constants.SWITCHER_CAMERA, isChecked);
                }
                break;
            case R.id.microphone_switcher:
                editor.putBoolean(KEY_MICROPHONE_OPEN, isChecked);
                if (mOnSettingChangedListener != null) {
                    mOnSettingChangedListener.onSwitcherChanged(Constants.SWITCHER_AUDIO, isChecked);
                }
                break;
            case R.id.class_room_talk:
                editor.putBoolean(KEY_MULTI_TALK_OPEN, isChecked);
                break;
        }
        editor.commit();
    }
}
