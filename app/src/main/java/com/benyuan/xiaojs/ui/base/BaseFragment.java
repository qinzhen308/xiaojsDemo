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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.benyuan.xiaojs.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected Activity mContext;
    protected ViewGroup mContent;
    private Unbinder mBinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContent = (ViewGroup)inflater.inflate(R.layout.fragment_base, null);
        View content = getContentView();
        addContainerView(content);
        mBinder = ButterKnife.bind(this, content);
        init();
        return mContent;
    }

    protected abstract View getContentView();

    protected abstract void init();

    public final void addContainerView(View view) {
        if (view != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(params);
            mContent.removeAllViews();
            mContent.addView(view, 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinder != null){
            mBinder.unbind();
        }
    }
}
