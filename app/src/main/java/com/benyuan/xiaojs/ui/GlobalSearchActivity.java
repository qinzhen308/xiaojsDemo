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
 * Date:2016/12/12
 * Desc:搜索个人、机构、课
 *
 * ======================================================================================== */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.IconTextView;
import com.benyuan.xiaojs.ui.widget.ImageMatrixExpandableLayout;
import com.benyuan.xiaojs.util.BitmapUtils;

import butterknife.BindView;

public class GlobalSearchActivity extends BaseActivity {

    @BindView(R.id.tt)
    IconTextView tt;
    @BindView(R.id.image_expand)
    ImageMatrixExpandableLayout mExpand;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_global_search);
        tt.setText("这个是中文");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_msg_bg);
        tt.setIcon(BitmapUtils.getDrawableWithText(this,bitmap.copy(Bitmap.Config.ARGB_8888,true),"22"));
        mExpand.show(28);
    }
}
