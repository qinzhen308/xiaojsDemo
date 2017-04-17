package cn.xiaojs.xma.ui.view;
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
 * Date:2016/12/11
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.home.HomeFragment;
import cn.xiaojs.xma.ui.home.HomeMomentAdapter;
import cn.xiaojs.xma.ui.home.MomentDetailActivity;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.VerifyUtils;

public class MomentHeader extends RelativeLayout {

    @BindView(R.id.moment_header_desc)
    IconTextView mDesc;
    @BindView(R.id.moment_header_name)
    TextView mName;
    @BindView(R.id.moment_header_time)
    TextView mTime;
    @BindView(R.id.home_header_desc_wrapper)
    View mDescWrapper;
    @BindView(R.id.moment_header_role)
    TextView mTag;
    @BindView(R.id.moment_header_image)
    RoundedImageView mHead;
    @BindView(R.id.moment_header_focus)
    FollowView mFollow;

    private MomentDetailActivity detailActivity;

    private Dynamic.DynOwner mOwner;



    public void setDetailActivity(MomentDetailActivity detailActivity) {
        this.detailActivity = detailActivity;
    }



    public MomentHeader(Context context) {
        super(context);
        init();
        PullToRefreshSwipeListView list = new PullToRefreshSwipeListView(getContext());
    }

    public MomentHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public MomentHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_moment_header, this);
        ButterKnife.bind(this);
    }

    public RoundedImageView getPortraitView() {
        return mHead;
    }

    public void setData(Dynamic dynamic) {

        //mHead.setImageResource(DeviceUtil.getPor());
        if (dynamic == null)
            return;
        mOwner = dynamic.owner;

        String url = mOwner != null ? Account.getAvatar(mOwner.account, XiaojsConfig.PORTRAIT_SIZE) : null;
        if (TextUtils.isEmpty(url)) {
            url = Account.getAvatar(dynamic.createdBy, XiaojsConfig.PORTRAIT_SIZE);
        }
        if (mOwner != null) {
            if (mOwner.myself || VerifyUtils.isMyself(mOwner.account)) {

                if (XiaojsConfig.mLoginUser == null) {
                    XiaojsConfig.mLoginUser = AccountDataManager.getUserInfo(getContext());
                }

                mName.setText(XiaojsConfig.mLoginUser.getName());
                mDescWrapper.setVisibility(GONE);
                mDesc.setText("");
            } else {
                //mDesc.setIcon(BitmapUtils.getDrawableWithText(getContext(), BitmapUtils.getBitmap(getContext(), R.drawable.ic_clz_remain), "22", R.color.white, R.dimen.font_20px));
                mDesc.setText("");
                mDescWrapper.setVisibility(VISIBLE);
                mName.setText(dynamic.owner.alias);
                mTag.setText(dynamic.owner.tag);
            }
        }
        mTime.setText(TimeUtil.getTimeFromNow(dynamic.createdOn));
        if (mOwner == null || mOwner.followed || mOwner.myself || VerifyUtils.isMyself(mOwner.account)) {
            mFollow.setVisibility(GONE);
        } else {
            mFollow.setVisibility(VISIBLE);
        }

        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(mHead);
    }

    @OnClick({R.id.moment_header_focus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moment_header_focus:
                follow();
                break;
        }
    }

    //关注
    private void follow() {
        if (mOwner == null || mOwner.followed)
            return;

        BaseBusiness.showFollowDialog(getContext(), new BaseBusiness.OnFollowListener() {
            @Override
            public void onFollow(long group) {
                if (group > 0) {
                    follow(group);
                }
            }
        });
    }

    private void follow(final long group) {
        if (mOwner == null) {
            return;
        }

        SocialManager.followContact(getContext(), mOwner.account,mOwner.alias, group, new APIServiceCallback<Relation>() {
            @Override
            public void onSuccess(Relation object) {
                ToastUtil.showToast(getContext(), R.string.followed);
                mFollow.setVisibility(GONE);

                if (detailActivity !=null) {
                    detailActivity.followSuccessed(mOwner.account);
                }

                Contact contact = new Contact();
                contact.alias = mOwner.alias;
                contact.account = mOwner.id;

                DataManager.addContact(getContext(), group, contact);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(), errorMessage);
            }
        });
    }
}
