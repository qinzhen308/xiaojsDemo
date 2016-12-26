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

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabActivity;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;

public class ScrollTabActivity extends BaseScrollTabActivity {

    @Override
    protected void initView() {
        needHeader(false);
        setPagerScrollable(false);
        List<BaseScrollTabFragment> fragments = new ArrayList<>();
        BaseScrollTabFragment fragment1 = new ScrollTabFragment();
        fragment1.setPagePosition(0);
        BaseScrollTabFragment fragment2 = new ScrollTabFragment();
        fragment2.setPagePosition(1);
        BaseScrollTabFragment fragment3 = new ScrollTabFragment();
        fragment3.setPagePosition(2);

        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);

        String[] tabs = new String[]{"渝中","渝北","江北"};

        View header = LayoutInflater.from(this).inflate(R.layout.layout_moment_detail_header,null);
        View footer = LayoutInflater.from(this).inflate(R.layout.layout_person_home_footer,null);
        addContent(fragments,tabs,header,footer);
    }
}
