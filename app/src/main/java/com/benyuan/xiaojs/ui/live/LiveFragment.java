package com.benyuan.xiaojs.ui.live;

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

import android.view.View;
import android.view.ViewGroup;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.core.GridViewWithHeaderAndFooter;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshGridView;
import com.benyuan.xiaojs.ui.base.BaseFragment;

import butterknife.BindView;

public class LiveFragment extends BaseFragment {

    @BindView(R.id.live_grid)
    PullToRefreshGridView mGrid;
    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_moment , null);
        return v;
    }

    @Override
    protected void init() {
        mGrid.getRefreshableView().setNumColumns(2);
        mGrid.getRefreshableView().setHorizontalSpacing(mContent.getResources().getDimensionPixelSize(R.dimen.px30));
        mGrid.getRefreshableView().setVerticalSpacing(mContent.getResources().getDimensionPixelSize(R.dimen.px30));
        View view = new View(mContext);
        GridViewWithHeaderAndFooter.LayoutParams lp = new GridViewWithHeaderAndFooter.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.px100));
        view.setLayoutParams(lp);
        mGrid.getRefreshableView().addFooterView(view);

        mGrid.setAdapter(new LiveAdapter(mContext,mGrid));
    }
}
