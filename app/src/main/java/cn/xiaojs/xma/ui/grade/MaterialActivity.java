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
 * Desc:班级资料库/我的资料库
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class MaterialActivity extends BaseActivity {

    public static final String KEY_IS_MINE = "key_is_mine";

    private boolean mIsMine;

    @BindView(R.id.material_list)
    PullToRefreshSwipeListView mList;

    MaterialAdapter mAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_material);
        setRightImage(R.drawable.upload_selector);
        Intent intent = getIntent();
        if (intent != null) {
            mIsMine = intent.getBooleanExtra(KEY_IS_MINE, false);
        }
        if (mIsMine){
            setMiddleTitle(R.string.data_bank_of_mine);
        }else {
            setMiddleTitle(R.string.data_bank);
        }
        mAdapter = new MaterialAdapter(this, mList,mIsMine);
        mList.setAdapter(mAdapter);
    }

    @OnClick({R.id.left_image, R.id.right_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image://上传文件
                break;
        }
    }
}
