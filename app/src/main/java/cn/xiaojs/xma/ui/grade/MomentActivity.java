package cn.xiaojs.xma.ui.grade;
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
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.home.HomeMomentAdapter;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;

public class MomentActivity extends BaseActivity {

    private final int REQUEST_POST_DYNAMIC = 1;

    @BindView(R.id.grade_moment_list)
    PullToRefreshSwipeListView mList;

    HomeMomentAdapter mAdapter;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_grade_momentt);
        setMiddleTitle(R.string.person_moment);
        mAdapter = new HomeMomentAdapter(this, mList);
        mList.setAdapter(mAdapter);
    }

    @OnClick({R.id.left_image,R.id.grade_moment_post})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.grade_moment_post:
                Intent post = new Intent(this, PostDynamicActivity.class);
                post.putExtra(PostDynamicActivity.KEY_POST_TYPE, Social.ShareScope.CLASSES);
                startActivityForResult(post,REQUEST_POST_DYNAMIC);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_POST_DYNAMIC:
                if (resultCode == RESULT_OK){
                    if (mAdapter != null){
                        mAdapter.doRefresh();
                    }
                }
                break;
        }
    }
}
