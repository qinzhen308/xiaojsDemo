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
 * Date:2016/12/13
 * Desc:动态详情界面
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseScrollTabActivity;
import com.benyuan.xiaojs.ui.widget.ImageMatrixExpandableLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MomentDetailActivity extends BaseScrollTabActivity {

    private Unbinder mBinder;
    @BindView(R.id.moment_detail_image_expand)
    ImageMatrixExpandableLayout mExpand;
    @Override
    public void addHoverHeaderView() {

        setMiddleTitle(R.string.detail);
        setRightImage(R.drawable.ic_lesson_more);
        setRightImage2(R.drawable.share_selector);
        initList();
        mBinder = ButterKnife.bind(this);
        mTabHeader.setVisibility(View.GONE);
        mExpand.show(100);
    }

    private void initList(){
        ArrayList<String> mPagerTitles = new ArrayList<String>();
        mPagerTitles.add("title");
        //mPagerTitles.add("title1");
        View header = LayoutInflater.from(this).inflate(R.layout.layout_moment_detail_header,null);
        View footer = LayoutInflater.from(this).inflate(R.layout.layout_moment_detail_footer,null);
        ArrayList<MomentDetailAdapter> adapters = new ArrayList<>();
        MomentDetailAdapter adapter = new MomentDetailAdapter(this);
        //MomentDetailAdapter adapter1 = new MomentDetailAdapter(this);
        adapters.add(adapter);
        //adapters.add(adapter1);
        addTabListIntoContent(header,footer,mPagerTitles,adapters,0);
        setNeedTabView(false);
    }

    @OnClick({R.id.moment_detail_footer_click})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.moment_detail_footer_click:
                Intent comment = new Intent(this,MomentCommentActivity.class);
                comment.putExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_WRITE);
                startActivity(comment);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null){
            mBinder.unbind();
        }
        super.onDestroy();
    }
}
