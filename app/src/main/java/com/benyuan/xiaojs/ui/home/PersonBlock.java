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

public class PersonBlock extends FrameLayout {

    @BindView(R.id.person_block_list)
    HorizontalListView mList;
    public PersonBlock(Context context) {
        super(context);
        init();
    }

    public PersonBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersonBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PersonBlock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_person_block,this,true);
        ButterKnife.bind(this);
    }

    public void setData(){
        BaseAdapter adapter = new PersonAdapter(getContext());
        mList.setAdapter(adapter);
        ViewGroup.LayoutParams lp = mList.getLayoutParams();
        lp.height = getResources().getDimensionPixelSize(R.dimen.px260);
        mList.setLayoutParams(lp);
    }
}
