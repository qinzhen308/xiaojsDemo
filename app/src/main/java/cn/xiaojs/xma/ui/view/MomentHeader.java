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
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.model.social.Relation;
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
    @BindView(R.id.moment_header_role)
    TextView mTag;
    @BindView(R.id.moment_header_image)
    RoundedImageView mHead;
    @BindView(R.id.moment_header_focus)
    FollowView mFollow;

    private Dynamic.DynOwner mOwner;
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

    public void setData(Dynamic dynamic) {
        mOwner = dynamic.owner;
        if (mOwner.myself || VerifyUtils.isMyself(mOwner.account)){
            mName.setText(XiaojsConfig.mLoginUser.getName());
            mTag.setText("自己");
            mDesc.setText("");
        }else {
            //mDesc.setIcon(BitmapUtils.getDrawableWithText(getContext(), BitmapUtils.getBitmap(getContext(), R.drawable.ic_clz_remain), "22", R.color.white, R.dimen.font_20px));
            mDesc.setText("");
            mName.setText(dynamic.owner.alias);
            mTag.setText(dynamic.owner.tag);
        }
        mTime.setText(TimeUtil.getTimeByNow(dynamic.createdOn));
        if (dynamic.owner.followed || mOwner.myself || VerifyUtils.isMyself(mOwner.account)) {
            mFollow.setVisibility(GONE);
        } else {
            mFollow.setVisibility(VISIBLE);
        }
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
        SocialManager.followContact(getContext(), mOwner.id, Social.ContactGroup.FRIENDS, new APIServiceCallback<Relation>() {
            @Override
            public void onSuccess(Relation object) {
                ToastUtil.showToast(getContext(),R.string.followed);
                mFollow.setVisibility(GONE);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(),errorMessage);
            }
        });

    }
}
