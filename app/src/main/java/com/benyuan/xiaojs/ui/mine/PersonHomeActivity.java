package com.benyuan.xiaojs.ui.mine;
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
 * Date:2016/12/13
 * Desc:个人主页
 *
 * ======================================================================================== */

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseScrollTabActivity;
import com.benyuan.xiaojs.ui.base.BaseScrollTabListAdapter;
import com.benyuan.xiaojs.util.BitmapUtils;
import com.benyuan.xiaojs.util.FastBlur;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PersonHomeActivity extends BaseScrollTabActivity {

    private Unbinder mBinder;

    @BindView(R.id.person_home_header_blur)
    ImageView mBlur;
    @BindView(R.id.person_home_look_material)
    View mMaterial;
    @BindView(R.id.person_home_header_image_holder)
    View mImageHolder;

    @Override
    public void addHoverHeaderView() {
        ArrayList<String> mPagerTitles = new ArrayList<String>();
        mPagerTitles.add(getString(R.string.person_lesson));
        mPagerTitles.add(getString(R.string.person_comment));
        mPagerTitles.add(getString(R.string.person_moment));
        View header = LayoutInflater.from(this).inflate(R.layout.layout_person_home_header, null);
        ArrayList<BaseScrollTabListAdapter> adapters = new ArrayList<>();
        PersonHomeLessonAdapter adapter = new PersonHomeLessonAdapter(this);
        PersonHomeLessonAdapter adapter1 = new PersonHomeLessonAdapter(this);
        PersonHomeLessonAdapter adapter2 = new PersonHomeLessonAdapter(this);
        adapters.add(adapter);
        adapters.add(adapter1);
        adapters.add(adapter2);
        addTabListIntoContent(header, null, mPagerTitles, adapters, 0);
        setNeedTabView(true);

        mBinder = ButterKnife.bind(this);
        initHeader();
    }

    private void initHeader(){
        Bitmap blur = FastBlur.smartBlur(BitmapUtils.getBitmap(this,R.drawable.default_portrait),2,true);
        mBlur.setImageBitmap(blur);
        needHeader(false);
        mTabHeader.setBackgroundResource(R.drawable.ic_home_title_bg);
        mTabRightText.setText("关注他");
        mTabRightText.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onScrollY(int y) {
        if (y >= 0) {
            int bannerHeight = mImageHolder.getMeasuredHeight();
            if (y >= 0 && y <= bannerHeight) {
                mTabHeader.setBackgroundResource(R.drawable.ic_home_title_bg);
                mTabRightText.setTextColor(getResources().getColor(R.color.white));
            }else{
                mTabHeader.setBackgroundColor(getResources().getColor(R.color.white));
                mTabRightText.setTextColor(getResources().getColor(R.color.common_text));
            }
        }
    }

    @Override
    protected int hoverMarginTop() {
        return getResources().getDimensionPixelSize(R.dimen.px90);
    }

    @OnClick({R.id.left_image})
    public void onClick(View view){

        switch (view.getId()){
            case R.id.left_image:
                if (mMaterial.getVisibility() == View.VISIBLE){
                    mMaterial.setVisibility(View.GONE);
                }else {
                    mMaterial.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unbind();
        }
        super.onDestroy();
    }
}
