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
 * Date:2017/1/8
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.DeviceUtil;

public class PersonalProfile extends LinearLayout {

    @BindView(R.id.personal_profile_image)
    RoundedImageView mImage;
    @BindView(R.id.personal_profile_type)
    TextView mType;
    @BindView(R.id.personal_profile_name)
    TextView mName;


    private boolean teacher;

    public PersonalProfile(Context context) {
        super(context);
        init();
    }

    public PersonalProfile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersonalProfile(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public PersonalProfile(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.layout_personal_profile, this);
        ButterKnife.bind(this);
    }

    public void show(boolean isTeacher) {
        if (isTeacher) {
            mType.setVisibility(VISIBLE);
        } else {
            mType.setVisibility(GONE);
        }
        mImage.setImageResource(DeviceUtil.getPor());
    }
}
