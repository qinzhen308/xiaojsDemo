package com.benyuan.xiaojs.ui.message;
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

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;

public class MessageCategoryListActivity extends BaseActivity {

    @BindView(R.id.message_list)
    AutoPullToRefreshListView mList;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_message_list);


    }
}
