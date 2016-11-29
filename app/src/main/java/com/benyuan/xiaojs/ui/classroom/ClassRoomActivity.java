package com.benyuan.xiaojs.ui.classroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.drawer.DrawerLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
 * Date:2016/11/27
 * Desc:
 *
 * ======================================================================================== */

public class ClassRoomActivity extends FragmentActivity {
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private Unbinder mBinder;
    @BindView(R.id.drawer_right_layout)
    ViewGroup mDrawerRightLayout;

    private MessagePanel mMessagePanel;
    private SettingPanel mSettingPanel;
    private ContactPanel mContactPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        mBinder = ButterKnife.bind(this);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);


        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick({R.id.back_btn, R.id.setting_btn, R.id.notify_msg_btn, R.id.contact_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.setting_btn:
                openSetting();
                break;
            case R.id.notify_msg_btn:
                openAllMessage();
                break;
            case R.id.contact_btn:
                openContacts();
                break;
            default:
                break;
        }
    }

    private void openSetting() {
        if (mSettingPanel == null) {
            mSettingPanel = new SettingPanel(this);
        }
        mSettingPanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    private void openContacts() {
        if (mContactPanel == null) {
            mContactPanel = new ContactPanel(this);
        }
        mContactPanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    private void openAllMessage() {
        if (mMessagePanel == null) {
            mMessagePanel = new MessagePanel(this);
        }
        mMessagePanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBinder != null) {
            mBinder.unbind();
        }
    }
}
