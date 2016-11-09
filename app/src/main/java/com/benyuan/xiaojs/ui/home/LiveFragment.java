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
 * Date:2016/11/8
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.HorizontalListView;

import butterknife.BindView;

public class LiveFragment extends BlockFragment {

    @BindView(R.id.live_block_list)
    HorizontalListView mList;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_live_block, null);
    }

    @Override
    protected void init() {
        BaseAdapter adapter = new LiveAdapter(mContext);
        mList.setAdapter(adapter);
        ViewGroup.LayoutParams lp = mList.getLayoutParams();
        lp.height = 370;
        mList.setLayoutParams(lp);
    }

    @Override
    public int getFragmentHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.px370);
    }
}
