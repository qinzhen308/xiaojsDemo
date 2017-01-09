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

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

public class PersonalInfoActivity extends BaseActivity {

    @BindView(R.id.personal_info_image)
    RoundedImageView mImage;
    @BindView(R.id.personal_info_name)
    IconTextView mName;
    @BindView(R.id.personal_info_desc)
    TextView mDesc;

    @BindView(R.id.grade_home_material)
    TextView mInto;
    @BindView(R.id.personal_info_follow)
    Button mFollow;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_personal_info);
        setMiddleTitle(R.string.personal_info);
    }


    @OnClick({R.id.left_image, R.id.personal_info_follow, R.id.grade_home_material})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.personal_info_follow://关注并发消息
                break;
            case R.id.grade_home_material://他的主页
                break;
        }
    }
}
