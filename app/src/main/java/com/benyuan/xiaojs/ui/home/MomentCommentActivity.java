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
 * Date:2016/12/18
 * Desc:动态评论、回复
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseCheckSoftInputActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MomentCommentActivity extends BaseCheckSoftInputActivity {

    @BindView(R.id.moment_comment_input)
    EditText mInput;
    @Override
    protected void onImShow() {

    }

    @Override
    protected void onImHide() {
        finish();
    }

    @Override
    protected void load() {
        needHeader(false);
        loadView(R.layout.activity_moment_comment);

        Intent intent = getIntent();
        if (intent != null){
            int type = intent.getIntExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_WRITE);
            if (type == HomeConstant.COMMENT_TYPE_WRITE){
                //写评论
                mInput.setHint(R.string.talk_about_your_comment);
            }else {
                String name = intent.getStringExtra(HomeConstant.KEY_COMMENT_REPLY_NAME);
                mInput.setHint(getString(R.string.comment_reply,name));
            }
        }
    }

    @OnClick({R.id.moment_comment_outside})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.moment_comment_outside:
                finish();
                break;
        }
    }

}
