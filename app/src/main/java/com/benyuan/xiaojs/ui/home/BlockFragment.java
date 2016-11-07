package com.benyuan.xiaojs.ui.home;
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
 * Date:2016/11/4
 * Desc:
 *
 * ======================================================================================== */

import com.benyuan.xiaojs.ui.base.BaseFragment;

public abstract class BlockFragment extends BaseFragment {

    /**
     * 子类需要计算当前fragment的高度给parent的ViewPager使用
     * @return
     */
    public abstract int getFragmentHeight();
}
