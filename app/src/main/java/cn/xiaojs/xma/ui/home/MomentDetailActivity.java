package cn.xiaojs.xma.ui.home;
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

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.ImageMatrixExpandableLayout;

public class MomentDetailActivity extends BaseActivity {

    private Unbinder mBinder;

    PullToRefreshSwipeListView mList;
    @BindView(R.id.moment_detail_image_expand)
    ImageMatrixExpandableLayout mExpand;

    private String mMomentId;
    private MomentDetailAdapter mAdapter;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_moment_detail);
        setMiddleTitle(R.string.detail);
        setRightImage(R.drawable.ic_lesson_more);
        setRightImage2(R.drawable.share_selector);
        Intent intent = getIntent();
        if (intent != null){
            mMomentId = intent.getStringExtra(HomeConstant.KEY_MOMENT_ID);
        }
        initList();
        mBinder = ButterKnife.bind(this);
        mExpand.show(100);
    }

    private void initList(){
        mList = (PullToRefreshSwipeListView) findViewById(R.id.moment_detail_list);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_moment_detail_header,null);
        mList.getRefreshableView().addHeaderView(header);
        mAdapter = new MomentDetailAdapter(this,mList,true,mMomentId,"PostActivity");
        mList.setAdapter(mAdapter);
    }

    @Override
    protected boolean delayBindView() {
        return true;
    }

    @OnClick({R.id.left_image,R.id.moment_detail_footer_click})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.moment_detail_footer_click:
                Intent comment = new Intent(this,MomentCommentActivity.class);
                comment.putExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_WRITE);
                comment.putExtra(HomeConstant.KEY_MOMENT_ID,mMomentId);
                startActivityForResult(comment,HomeConstant.REQUEST_CODE_COMMENT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case HomeConstant.REQUEST_CODE_COMMENT:
                if (resultCode == Activity.RESULT_OK){
                    if (mAdapter != null){
                        mAdapter.doRefresh();
                    }
                }
                break;
        }
    }
}
