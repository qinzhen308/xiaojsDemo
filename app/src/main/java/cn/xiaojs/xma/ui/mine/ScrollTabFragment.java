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
 * Date:2016/12/23
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

public class ScrollTabFragment extends BaseScrollTabFragment {

    @Override
    protected void initData() {
        cn.xiaojs.xma.ui.home.MomentDetailAdapter adapter1 = new cn.xiaojs.xma.ui.home.MomentDetailAdapter(getContext(),mList);
        mList.setAdapter(adapter1);
    }
}
