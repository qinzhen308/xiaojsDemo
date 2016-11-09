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
 * Date:2016/11/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.HorizontalListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveBlock extends FrameLayout {

    @BindView(R.id.live_block_list)
    HorizontalListView mList;

    public LiveBlock(Context context) {
        super(context);
        init();
    }

    public LiveBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LiveBlock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_live_block,this,true);
        ButterKnife.bind(this);
    }

    public void setData(){
        BaseAdapter adapter = new LiveAdapter(getContext());
        mList.setAdapter(adapter);
        ViewGroup.LayoutParams lp = mList.getLayoutParams();
        lp.height = getResources().getDimensionPixelSize(R.dimen.px370);
        mList.setLayoutParams(lp);
    }
}
