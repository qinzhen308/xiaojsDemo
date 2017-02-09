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
 * Date:2016/11/16
 * Desc:
 *
 * ======================================================================================== */

import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseListActivity extends BaseActivity {

    @BindView(R.id.base_list)
    protected PullToRefreshSwipeListView mList;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_base_list);
        initData();
    }

    protected void setDividerHeight(int dimen) {
        mList.getRefreshableView().getWrappedList().setDividerHeight(getResources().getDimensionPixelSize(dimen));
    }

    protected abstract void initData();

    @OnClick({R.id.left_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
