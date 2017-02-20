package cn.xiaojs.xma.ui.mine;
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
 * Date:2017/2/20
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseListActivity;

public class MyOrderActivity extends BaseListActivity {

    private MyOrderAdapter mAdapter;

    @Override
    protected void initData() {
        setMiddleTitle(R.string.my_order);
        mAdapter = new MyOrderAdapter(this,mList);
        mList.setAdapter(mAdapter);
    }
}
