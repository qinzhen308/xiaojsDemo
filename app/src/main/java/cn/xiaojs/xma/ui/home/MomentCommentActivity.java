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
 * Date:2016/12/18
 * Desc:动态评论、回复
 *
 * ======================================================================================== */

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.ui.base.BaseCheckSoftInputActivity;
import cn.xiaojs.xma.util.ToastUtil;

public class MomentCommentActivity extends BaseCheckSoftInputActivity {

    @BindView(R.id.moment_comment_input)
    EditText mInput;

    private int mType;
    private String mMomentId;
    private String mReplyId;

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
            mType = intent.getIntExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_WRITE);
            if (mType == HomeConstant.COMMENT_TYPE_WRITE){
                //写评论
                mInput.setHint(R.string.talk_about_your_comment);
            }else {
                String name = intent.getStringExtra(HomeConstant.KEY_COMMENT_REPLY_NAME);
                mInput.setHint(getString(R.string.comment_reply,name));
                mReplyId = intent.getStringExtra(HomeConstant.KEY_MOMENT_REPLY_ID);
            }
            mMomentId = intent.getStringExtra(HomeConstant.KEY_MOMENT_ID);
        }
    }

    @OnClick({R.id.moment_comment_outside,R.id.moment_comment_send})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.moment_comment_outside:
                finish();
                break;
            case R.id.moment_comment_send:
                send();
                break;
        }
    }

    private void send(){
        String comment = mInput.getText().toString();
        if (TextUtils.isEmpty(comment))
            return;
        if (mType == HomeConstant.COMMENT_TYPE_WRITE){
            //写评论
            SocialManager.commentActivity(this, mMomentId, comment, new APIServiceCallback<Comment>() {
                @Override
                public void onSuccess(Comment object) {
                    ToastUtil.showToast(MomentCommentActivity.this,"评论成功!");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ToastUtil.showToast(MomentCommentActivity.this,errorMessage);
                }
            });
        }else if (mType == HomeConstant.COMMENT_TYPE_REPLY){
            SocialManager.replyComment(this, mReplyId, comment, new APIServiceCallback<Comment>() {
                @Override
                public void onSuccess(Comment object) {
                    ToastUtil.showToast(MomentCommentActivity.this,"评论成功!");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ToastUtil.showToast(MomentCommentActivity.this,errorMessage);
                }
            });
        }else if (mType == HomeConstant.COMMENT_TYPE_REPLY_REPLY){
            SocialManager.reply2Reply(this, mReplyId, comment, new APIServiceCallback<Comment>() {
                @Override
                public void onSuccess(Comment object) {
                    ToastUtil.showToast(MomentCommentActivity.this,"评论成功!");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ToastUtil.showToast(MomentCommentActivity.this,errorMessage);
                }
            });
        }
    }

}
