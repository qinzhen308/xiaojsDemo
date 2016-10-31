package com.benyuan.xiaojs.ui;
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
 * Date:2016/10/31
 * Desc:
 *
 * ======================================================================================== */

import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import butterknife.BindView;

public class TeActivity extends BaseActivity {
    @BindView(R.id.te)
    TextView mTe;
    @Override
    protected void addViewContent() {
        addView(R.layout.te_activity);
        mTe.setText("te");
    }
}
