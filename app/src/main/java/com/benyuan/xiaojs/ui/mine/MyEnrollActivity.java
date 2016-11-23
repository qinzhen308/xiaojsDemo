package com.benyuan.xiaojs.ui.mine;
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
 * Date:2016/11/23
 * Desc:我的报名
 *
 * ======================================================================================== */

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseListActivity;

public class MyEnrollActivity extends BaseListActivity{

    @Override
    protected void initData() {
        setMiddleTitle(R.string.my_enrollment);
        mList.setAdapter(new MyEnrollAdapter(this,mList));
    }
}
