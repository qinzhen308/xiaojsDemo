package cn.xiaojs.xma.ui.grade;
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
 * Date:2017/2/8
 * Desc:
 *
 * ======================================================================================== */

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseListActivity;

public class MaterialDownloadActivity extends BaseListActivity {

    private MaterialDownloadAdapter mAdapter;

    @Override
    protected void initData() {
        setMiddleTitle(R.string.download_of_mine);
        setDividerHeight(R.dimen.px1);
        mAdapter = new MaterialDownloadAdapter(this, mList);
        mList.setAdapter(mAdapter);
    }
}
