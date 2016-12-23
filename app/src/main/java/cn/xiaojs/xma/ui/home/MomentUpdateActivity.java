package cn.xiaojs.xma.ui.home;
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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.ui.base.BaseListActivity;

public class MomentUpdateActivity extends BaseListActivity {

    @Override
    protected void initData() {
        MomentUpdateAdapter adapter = new MomentUpdateAdapter(this,mList,true);
        mList.setAdapter(adapter);
    }
}
