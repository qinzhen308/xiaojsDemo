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
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.ui.widget.flow.ImageFlowLayout;
import com.benyuan.xiaojs.util.FastBlur;
import com.benyuan.xiaojs.util.StringUtil;
import com.benyuan.xiaojs.util.TimeUtil;
import com.benyuan.xiaojs.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @BindView(R.id.image_flow)
    ImageFlowLayout mImageFlow;

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

    public void setData(Date date){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_portrait);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth() / 3,bitmap.getHeight() / 3,false);
        Bitmap b = FastBlur.doBlur(scaleBitmap,10,true);
        mBackground.setImageBitmap(b);
        mDate.setText(TimeUtil.format(date,TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        mTime.setText("时长：95分钟");
        String timeMark = getContext().getString(R.string.block_cls_time_mark,57);
        String time = String.valueOf(57);
        mTimeMark.setText(StringUtil.getSpecialString(timeMark,time,getResources().getDimensionPixelSize(R.dimen.font_26px),getResources().getColor(R.color.font_orange)));
        mTitle.setText("人力资源管理师3级冬季2班人力资源管理师3级冬季2班");
        String courseNum = getContext().getString(R.string.block_cls_course_num,3);
        mCourseNum.setText(StringUtil.getSpecialString(courseNum,String.valueOf(3),getResources().getDimensionPixelSize(R.dimen.font_26px),getResources().getColor(R.color.font_orange)));
        mCourseTotal.setText(getContext().getString(R.string.block_cls_course_total,8));
        mPersonImage1.setImageResource(R.drawable.default_portrait);
        mPersonImage2.setImageResource(R.drawable.default_portrait);
        mPersonName1.setText("张晓丽张晓丽张晓丽");
        mPersonName2.setText("李明博张晓丽张晓丽张晓丽");
        mPersonDesc1.setText("高级讲师");
        mPersonDesc2.setText("高级助教");

        Bitmap b1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b2 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b3 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b4 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b5 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b6 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b7 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);
        Bitmap b8 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_center_shader);

        List<Bitmap> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);
        list.add(b5);
        list.add(b6);
        list.add(b7);
        list.add(b8);

        mImageFlow.show(list);
        mImageFlow.setOnItemClickListener(new ImageFlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToastUtil.showToast(getContext(),"image position" + position);
            }
        });
    }
}
