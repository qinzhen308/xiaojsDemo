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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.BitmapUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    public MomentHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_moment_header, this);
        ButterKnife.bind(this);
    }

    public void setData(Dynamic.DynOwner owner){
        mDesc.setIcon(BitmapUtils.getDrawableWithText(getContext(),BitmapUtils.getBitmap(getContext(),R.drawable.ic_clz_remain),"22",R.color.white,R.dimen.font_20px));
        mName.setText(owner.alias);
        mTag.setText(owner.tag);
        if (owner.followed){
            mFollow.setVisibility(GONE);
        }else {
            mFollow.setVisibility(VISIBLE);
        }
    }
}
