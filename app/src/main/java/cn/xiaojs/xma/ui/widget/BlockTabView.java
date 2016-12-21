package cn.xiaojs.xma.ui.widget;
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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlockTabView extends FrameLayout {

    @BindView(R.id.block_title)
    TextView mTitle;
    @BindView(R.id.block_fun)
    TextView mFun;
    @BindView(R.id.block_tab_bar)
    RelativeLayout mTabBar;
    @BindView(R.id.block_tab1)
    BottomLineTextView mTab1;
    @BindView(R.id.block_tab2)
    BottomLineTextView mTab2;
    @BindView(R.id.block_tab_container)
    FrameLayout mContainer;

    private List<BottomLineTextView> textViews;

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

    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_block_tab, this, true);
        ButterKnife.bind(this);
        textViews = new ArrayList<>();
        textViews.add(mTab1);
        textViews.add(mTab2);
    }

    public void setViews(String title, String[] tabs, List<? extends View> views, String fun) {

        if (views == null) {
            return;
        }

        if (tabs != null && tabs.length > 0){
            mTabBar.setVisibility(VISIBLE);
        }else {
            mTabBar.setVisibility(GONE);
        }
        for (int i = 0; i < views.size(); i++) {
            View v = views.get(i);
            mContainer.addView(v);
            if (i != 0) {
                v.setVisibility(GONE);
            }else {
                textViews.get(i).setSelected(true);
            }

            if (tabs != null && tabs.length == views.size()) {
                if (i >= textViews.size()){
                    break;
                }
                textViews.get(i).setText(tabs[i]);
                final int index = i;
                textViews.get(i).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectTab(index);
                    }
                });
            }

        }

        mTitle.setText(title);
        mFun.setText(fun);
    }

    private void show(int position) {
        if (position >= mContainer.getChildCount()) {
            return;
        }
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            if (position == i) {
                mContainer.getChildAt(i).setVisibility(VISIBLE);
            } else {
                mContainer.getChildAt(i).setVisibility(GONE);
            }
        }
    }

    private void selectTab(int position){
        if (position >= textViews.size())
            return;
        for (int i=0;i<textViews.size();i++){
            if (i == position){
                textViews.get(position).setSelected(true);
            }else {
                textViews.get(i).setSelected(false);
            }
        }
        show(position);
    }
}
