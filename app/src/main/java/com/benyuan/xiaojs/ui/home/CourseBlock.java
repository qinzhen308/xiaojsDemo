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
 * Date:2016/11/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.HorizontalListView;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.FastBlur;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseBlock extends FrameLayout {

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

    public CourseBlock(Context context) {
        super(context);
        init();
    }

    public CourseBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CourseBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CourseBlock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_course_block,this,true);
        ButterKnife.bind(this);
    }

    public void setData(String t){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_portrait);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth() / 3,bitmap.getHeight() / 3,false);
        Bitmap b = FastBlur.doBlur(scaleBitmap,10,true);
        mBackground.setImageBitmap(b);
        mDate.setText(t);
    }
}
