package cn.xiaojs.xma.ui.personal;
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
 * Date:2017/2/21
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

public class PersonHomeMomentFragment extends BaseScrollTabFragment {

    @Override
    protected void initData() {
        mList.getRefreshableView().getWrappedList().setDividerHeight(getResources().getDimensionPixelSize(R.dimen.px30));
        PersonHomeMomentAdapter adapter = new PersonHomeMomentAdapter(getContext(), mList);
        mList.setAdapter(adapter);
    }
}
