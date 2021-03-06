package cn.xiaojs.xma.ui.personal;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;

import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.ui.base.BaseActivity;

import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.IconTextView;

import cn.xiaojs.xma.util.JpushUtil;

public class PersonalInfoActivity extends BaseActivity {

    @BindView(R.id.personal_info_image)
    ImageView mImage;
    @BindView(R.id.personal_info_name)
    IconTextView mName;
    @BindView(R.id.personal_info_desc)
    TextView mDesc;

    @BindView(R.id.grade_home_material)
    TextView mInto;
    @BindView(R.id.personal_info_follow)
    Button mFollow;

    private String accountID;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_personal_info);
        setMiddleTitle(R.string.personal_info);
//        mName.setText("陈哲");
//        mName.setIcon(R.drawable.ic_male);


        Intent intent = getIntent();
        accountID = intent.getStringExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT);
        if(TextUtils.isEmpty(accountID)){
            showFailedView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData();
                }
            });
        }else{
            getData();
        }


    }

    private void getData() {

        showProgress(true);
        JMessageClient.getUserInfo(accountID, new GetUserInfoCallback() {
            @Override
            public void gotResult(int status, String responseMessage, UserInfo userInfo) {

                cancelProgress();

                if (status == JpushUtil.STATUS_OK && userInfo != null) {
                    initView(userInfo);
                } else {
                    showFailedView(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getData();
                        }
                    });
                }
            }
        });

    }

    private void initView(UserInfo info) {
        if (info == null)
            return;

        Glide.with(getApplicationContext())
                .load(Account.getAvatar(accountID, 300))
                .bitmapTransform(new CircleTransform(this))
                .error(R.drawable.default_avatar_grey)
                .placeholder(R.drawable.default_avatar_grey)
                .into(mImage);
        mName.setText(info.getNickname());

        UserInfo.Gender gender = info.getGender();
        if (gender != null) {
            if (gender == UserInfo.Gender.male) {
                mName.setIcon(R.drawable.ic_male);
            } else if (gender == UserInfo.Gender.female) {
                mName.setIcon(R.drawable.ic_female);
            } else {
                mName.setIcon(0);
            }
        }

        //FIXME 得到IM的性别
        //mFollow.setText(getString(R.string.follow_and_send_message, JpushUtil.getGenderTitle(gender)));

        String sign = info.getSignature();
        if (!TextUtils.isEmpty(sign)) {
            mDesc.setText(sign);
        }
    }


    @OnClick({R.id.left_image, R.id.personal_info_follow, R.id.grade_home_material})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.personal_info_follow://关注并发消息
                follow();
                break;
            case R.id.grade_home_material://他的主页
                Intent intent = new Intent(this, PersonHomeActivity.class);
                intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, accountID);
                startActivity(intent);
                break;
        }
    }

    private void follow() {
//        if (mBean != null) {//这里需要弹框选择分组
//            if (!mBean.isFollowed) {
//                BaseBusiness.showFollowDialog(this, new BaseBusiness.OnFollowListener() {
//                    @Override
//                    public void onFollow(long group) {
//                        if (group > 0) {
//                            follow(group);
//                        }
//                    }
//                });
//            } else {
//                //已关注，直接跳转到聊天界面
//                final Intent intent = new Intent(PersonalInfoActivity.this, ChatActivity.class);
//                intent.putExtra(ChatActivity.TARGET_ID, "1234567");
//                intent.putExtra(ChatActivity.TARGET_APP_KEY, "e87cffb332432eec3c0807ba");
//                startActivity(intent);
//            }
//
//        }
    }

    private void follow(long group) {
//        SocialManager.followContact(this, mAccount, group, new APIServiceCallback<Relation>() {
//            @Override
//            public void onSuccess(Relation object) {
//                ToastUtil.showToast(getApplicationContext(), R.string.followed);
//                mBean.isFollowed = true;
//                //跳转到聊天界面
//                final Intent intent = new Intent(PersonalInfoActivity.this, ChatActivity.class);
//                intent.putExtra(ChatActivity.TARGET_ID, "1234567");
//                intent.putExtra(ChatActivity.TARGET_APP_KEY, XiaojsConfig.JPUSH_APP_KEY);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                ToastUtil.showToast(getApplicationContext(), errorMessage);
//            }
//        });
    }
}
