package com.benyuan.xiaojs.ui.course;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/8
 * Desc:
 *
 * ======================================================================================== */

public class SitInOnActivity extends BaseActivity {
    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.sit_in_on_title);

        addView(R.layout.activity_sit_in_on);
    }
}
