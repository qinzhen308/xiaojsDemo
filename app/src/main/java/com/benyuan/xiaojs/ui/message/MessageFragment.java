package com.benyuan.xiaojs.ui.message;

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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.util.BitmapUtils;

import butterknife.BindView;

public class MessageFragment extends BaseFragment {

    @BindView(R.id.tt)
    TextView tt;
    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_live, null);
        return v;
    }

    @Override
    protected void init() {
        Bitmap iconBitmap = BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.ic_center_tab_bg).copy(Bitmap.Config.ARGB_8888,true);
        Bitmap chat = BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.ic_tab_chat).copy(Bitmap.Config.ARGB_8888,true);
        tt.setCompoundDrawablesWithIntrinsicBounds(null, BitmapUtils.getTabDrawable(mContext,iconBitmap,chat,"聊天"),null,null);
        //tt.setCompoundDrawables();
    }


}
