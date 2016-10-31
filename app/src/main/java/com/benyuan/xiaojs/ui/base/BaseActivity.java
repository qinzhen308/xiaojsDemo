package com.benyuan.xiaojs.ui.base;
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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity extends FragmentActivity {

    private View mHeader;
    private FrameLayout mContent;
    private TextView mLeftText;
    private TextView mRightText;
    private TextView mMiddleText;

    private Unbinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mHeader = findViewById(R.id.header);
        mContent = (FrameLayout) findViewById(R.id.base_content);
        mLeftText = (TextView) findViewById(R.id.left_view);
        mRightText = (TextView) findViewById(R.id.right_view);
        mMiddleText = (TextView) findViewById(R.id.middle_view);
        addViewContent();
    }

    protected void needHeader(boolean need){
        if (!need){
            mHeader.setVisibility(View.GONE);
        }else {
            mHeader.setVisibility(View.VISIBLE);
        }
    }

    protected abstract void addViewContent();

    protected View addView(int layoutId){
        if (layoutId > 0){
            mContent.removeAllViews();
            View view = getLayoutInflater().inflate(layoutId, mContent);
            mBinder = ButterKnife.bind(this);
            return view;
        }else {
            try {
                throw new Exception("layoutId error!");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    protected View addView(View view){
        if (view != null){
            mContent.removeAllViews();
            mContent.addView(view);
            mBinder = ButterKnife.bind(this);
            return view;
        }else {
            try {
                throw new Exception("view is null!");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    protected final void setLeftText(int resId){
        if (resId > 0 && !TextUtils.isEmpty(getResources().getText(resId))){
            mLeftText.setVisibility(View.VISIBLE);
            mLeftText.setText(resId);
        }else {
            mLeftText.setVisibility(View.GONE);
        }
    }

    protected final void setMiddleTitle(int resId){
        if (resId > 0 && !TextUtils.isEmpty(getResources().getText(resId))){
            mMiddleText.setVisibility(View.VISIBLE);
            mMiddleText.setText(resId);
        }else {
            mMiddleText.setVisibility(View.GONE);
        }
    }

    protected final void setRightText(int resId){
        if (resId > 0 && !TextUtils.isEmpty(getResources().getText(resId))){
            mRightText.setVisibility(View.VISIBLE);
            mRightText.setText(resId);
        }else {
            mRightText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinder != null){
            mBinder.unbind();
        }
    }
}
