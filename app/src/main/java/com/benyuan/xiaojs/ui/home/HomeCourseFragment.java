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
 * Date:2016/11/4
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.HorizontalListView;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.FastBlur;

import butterknife.BindView;

/**
 * 我的小教室功能块
 */
public class HomeCourseFragment extends BlockFragment {

    @BindView(R.id.block_course_date)
    TextView mDate;
    @BindView(R.id.block_course_time)
    TextView mTime;
    @BindView(R.id.course_mark)
    TextView mTimeMark;

    @BindView(R.id.block_course_bg)
    RoundedImageView mBackground;
    @BindView(R.id.block_course_title)
    TextView mTitle;
    @BindView(R.id.block_course_num)
    TextView mCourseNum;
    @BindView(R.id.block_course_total)
    TextView mCourseTotal;

    @BindView(R.id.block_course_person2)
    LinearLayout mPersonWrapper2;

    @BindView(R.id.block_course_person1_image)
    ImageView mPersonImage1;
    @BindView(R.id.block_course_person1_name)
    TextView mPersonName1;
    @BindView(R.id.block_course_person1_desc)
    TextView mPersonDesc1;

    @BindView(R.id.block_course_person2_image)
    ImageView mPersonImage2;
    @BindView(R.id.block_course_person2_name)
    TextView mPersonName2;
    @BindView(R.id.block_course_person2_desc)
    TextView mPersonDesc2;

    @BindView(R.id.block_course_stu_list)
    HorizontalListView mStuList;
    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_home_course,null);
    }

    @Override
    protected void init() {
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(portrait,
//                portrait.getWidth() / 3,
//                portrait.getHeight() / 3,
//                false);
//        Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 10, true);
//        mBlurPortraitView.setImageBitmap(blurBitmap);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.default_portrait);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth() / 3,bitmap.getHeight() / 3,false);
        Bitmap b = FastBlur.doBlur(scaleBitmap,10,true);
        mBackground.setImageBitmap(b);
    }

    @Override
    public int getFragmentHeight(Context context) {
        int h = context.getResources().getDimensionPixelSize(R.dimen.px500) + context.getResources().getDimensionPixelSize(R.dimen.px30);
        return h;
    }
}
