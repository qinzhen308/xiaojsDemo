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
 * Date:2016/10/26
 * Desc:
 *
 * ======================================================================================== */

import android.widget.Toast;

import com.benyuan.xiaojs.util.zxing.CaptureActivity;


public class QrcodeActivity extends CaptureActivity {

    @Override
    protected void onResult(String result) {
        Toast.makeText(this,result, Toast.LENGTH_SHORT).show();
    }
}
