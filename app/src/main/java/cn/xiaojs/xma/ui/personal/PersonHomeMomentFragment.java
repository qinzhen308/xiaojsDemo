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

import android.os.Bundle;

import java.util.ArrayList;

import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

public class PersonHomeMomentFragment extends BaseScrollTabFragment {
    private PersonHomeMomentAdapter mAdapter;

    @Override
    protected void initData() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<Dynamic> lessons = (ArrayList<Dynamic>) bundle.getSerializable(PersonalBusiness.KEY_PERSONAL_ACTIVITY_LIST);
            if (lessons != null) {
                mAdapter = new PersonHomeMomentAdapter(getContext(), mList, lessons);
            }
        }
        if (mAdapter == null) {
            mAdapter = new PersonHomeMomentAdapter(getContext(), mList, new ArrayList<Dynamic>());
        }

        mList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mList.setAdapter(mAdapter);
    }
}
