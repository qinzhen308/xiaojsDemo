package com.benyuan.xiaojs.ui.view;
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
 * Date:2016/12/20
 * Desc:个人主页与我相关中同心圆
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RelationshipCircleView extends FrameLayout {

    @BindView(R.id.relationship_other)
    RoundedImageView mOther;
    @BindView(R.id.relationship_myself)
    RoundedImageView mMyself;
    @BindView(R.id.relationship_desc)
    TextView mDesc;
    @BindView(R.id.relationship_distance)
    TextView mDistance;

    private int mSameCityMargin;
    private int mDifferentCityMargin;

    public RelationshipCircleView(Context context) {
        super(context);
        init();
    }

    public RelationshipCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RelationshipCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public RelationshipCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_person_relationship_circle, this);
        ButterKnife.bind(this);
        mSameCityMargin = getResources().getDimensionPixelSize(R.dimen.px40);
        mDifferentCityMargin = getResources().getDimensionPixelSize(R.dimen.px20);
    }

    public void sameCity(boolean same) {
        MarginLayoutParams mlp = (MarginLayoutParams) mOther.getLayoutParams();
        if (same) {
            mlp.leftMargin = mSameCityMargin;
            mlp.topMargin = mSameCityMargin;
        } else {
            mlp.leftMargin = mDifferentCityMargin;
            mlp.topMargin = mDifferentCityMargin;
        }

        mOther.setLayoutParams(mlp);
    }

    public void setDesc(String desc) {
        mDesc.setText(desc);
    }

    public void setDistance(String distance) {
        mDistance.setText(distance);
    }
}
