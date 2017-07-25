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
 * Modify:huangyong
 *        add data change listener
 *
 * ======================================================================================== */

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
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
    private View mEmptyView;
    private View mFailReloadBtn;
    private SimpleDataChangeListener mDataChangeListener;
    private boolean mDataChanged = false;
    private boolean rejectButterKnife=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContent = (ViewGroup) inflater.inflate(R.layout.fragment_base, null);
        View content = getContentView();
        addContainerView(content);
        if(!rejectButterKnife)
        mBinder = ButterKnife.bind(this, content);
        init();
        return mContent;
    }

    /**
     * 不想用butterknife
     */
    protected final void rejectButterKnife(){
        rejectButterKnife=true;
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

    public void showEmptyView(String emptyTitle) {
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_empty, null);
            TextView tipView = (TextView) mEmptyView.findViewById(R.id.empty_desc);

            if (TextUtils.isEmpty(emptyTitle)) {
                emptyTitle = "(空)";
            }

            tipView.setText(emptyTitle);
            tipView.setVisibility(View.VISIBLE);
            if (mContent != null) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                mContent.addView(mEmptyView, params);
            }
        }

        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        if (mDataChanged) {
            mDataChanged = false;
            onDataChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mDataChangeListener != null) {
            DataChangeHelper.getInstance().unregisterDataChangeListener(mDataChangeListener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int type = registerDataChangeListenerType();
        if (type == 0) {
            return;
        }

        if (mDataChangeListener == null) {
            mDataChangeListener = new SimpleDataChangeListener(type) {
                @Override
                public void onDataChange() {
                    mDataChanged = true;
                }
            };
        }
        DataChangeHelper.getInstance().registerDataChangeListener(mDataChangeListener);
    }

    protected int registerDataChangeListenerType() {
        return 0;
    }

    /**
     * @see #registerDataChangeListenerType()
     */
    protected void onDataChanged() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

}
