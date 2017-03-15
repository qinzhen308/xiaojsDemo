package cn.xiaojs.xma.ui.base;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.http.PUT;

public abstract class BaseFragment extends Fragment {
    protected Activity mContext;
    protected ViewGroup mContent;
    private Unbinder mBinder;
    private ProgressHUD progress;
    private View mFailView;
    private View mFailReloadBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContent = (ViewGroup) inflater.inflate(R.layout.fragment_base, null);
        View content = getContentView();
        addContainerView(content);
        mBinder = ButterKnife.bind(this, content);
        init();
        return mContent;
    }

    protected abstract View getContentView();

    protected abstract void init();

    protected void reloadOnFailed() {
    }

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

    public void showFailView() {
        if (mFailView == null) {
            mFailView = LayoutInflater.from(mContext).inflate(R.layout.layout_failed, null);
            mFailReloadBtn = mFailView.findViewById(R.id.base_failed_click);
            if (mContent != null) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                mContent.addView(mFailView, params);
            }

            mFailReloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reloadOnFailed();
                }
            });
        }

        mFailView.setVisibility(View.VISIBLE);
    }

    public void hideFailView() {
        if (mFailView != null) {
            mFailView.setVisibility(View.GONE);
        }
    }

    public void showProgress(boolean cancelable) {
        if (progress == null) {
            progress = ProgressHUD.create(mContext);
        }
        progress.setCancellable(cancelable);
        progress.show();
    }

    public void cancelProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinder != null) {
            mBinder.unbind();
        }

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
            progress = null;
        }
    }
}
