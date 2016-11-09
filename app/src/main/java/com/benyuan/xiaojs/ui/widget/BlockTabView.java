package com.benyuan.xiaojs.ui.widget;
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
 * Date:2016/11/4
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.TabFragmentPagerAdapter;
import com.benyuan.xiaojs.ui.home.BlockFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlockTabView extends FrameLayout {

    @BindView(R.id.block_title)
    TextView mTitle;
    @BindView(R.id.block_tab)
    TabIndicatorView mTab;
    @BindView(R.id.block_fun)
    TextView mFun;
//    @BindView(R.id.block_pager)
//    LazyViewPager mPager;
    @BindView(R.id.block_tab_container)
    FrameLayout mContainer;

    public BlockTabView(Context context) {
        super(context);
        init();
    }

    public BlockTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlockTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BlockTabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_block_tab,this,true);
        ButterKnife.bind(this);

    }

    public void setViews(String title, String[] tabs, List<View> views,String fun){
        TextView t1 = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        t1.setLayoutParams(lp);
        t1.setText("我学的课");

        TextView t2 = new TextView(getContext());
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp2.leftMargin = 50;
        t2.setLayoutParams(lp2);
        t2.setText("我教的课");

        mTitle.setText(title);
        mFun.setText(fun);

        mTab.getTabScroller().addTabView(t1);
        mTab.addView(t1);
        mTab.getTabScroller().addTabView(t2);
        mTab.addView(t2);
        if (views == null){
            return;
        }
        for (int i = 0;i < views.size();i++){
            View v = views.get(i);
            mContainer.addView(v);
            if (i != 0){
                v.setVisibility(GONE);
            }
        }
//        PagerAdapter adapter = new TabFragmentPagerAdapter(fm,fs);
//        mPager.setAdapter(adapter);
//        ViewGroup.LayoutParams llp = mPager.getLayoutParams();
//        llp.height = fs.get(0).getFragmentHeight(getContext());
//        mPager.setScrollState(true);
//        mPager.setLayoutParams(llp);
        t1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                mPager.setCurrentItem(0);
                show(0);
            }
        });
        t2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                mPager.setCurrentItem(1);
                show(1);
            }
        });
    }

    public void show(int position){
        if (position >= getChildCount()){
            return;
        }
        for (int i = 0;i < getChildCount();i++){
            if (position == i){
                getChildAt(i).setVisibility(VISIBLE);
            }else {
                getChildAt(i).setVisibility(GONE);
            }
        }
    }
}
