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
 * Date:2017/1/8
 * Desc:班级主页
 *
 * ======================================================================================== */

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshScrollView;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.view.MomentContent;
import cn.xiaojs.xma.ui.view.MomentHeader;
import cn.xiaojs.xma.ui.widget.CustomScrollView;
import cn.xiaojs.xma.util.DeviceUtil;

public class GradeHomeActivity extends BaseActivity {

    @BindView(R.id.grade_home_scroller)
    CustomScrollView mScroller;

    @BindView(R.id.grade_home_title_bar)
    RelativeLayout mHeader;
    @BindView(R.id.grade_home_title_divider)
    View mDivider;
    @BindView(R.id.grade_home_middle_view)
    TextView mTitle;


    @BindView(R.id.grade_home_top)
    LinearLayout mTop;
    @BindView(R.id.grade_home_notice)
    LinearLayout mNotice;
    @BindView(R.id.grade_home_cover)
    ImageView mCover;
    @BindView(R.id.grade_home_teacher_grid)
    GridView mTeacherGrid;
    @BindView(R.id.grade_home_student_grid)
    GridView mStudentGrid;
    @BindView(R.id.grade_home_left_image)
    ImageView mBack;

    @BindView(R.id.moment_header)
    MomentHeader mMomentHeader;
    @BindView(R.id.moment_content)
    MomentContent mContent;


    private int mHeaderHeight;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_grade_home);
        needHeader(false);
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeaderHeight = mTop.getHeight() - mNotice.getHeight() / 2;
                mCover.getLayoutParams().height = mHeaderHeight;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        //mCover.setImageResource(DeviceUtil.getLesson());

        List<Date> dates = new ArrayList<>();
        dates.add(new Date());
        dates.add(new Date());
        dates.add(new Date());

        int gridWidth = DeviceUtil.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.px30);
        int numColumns = gridWidth / getResources().getDimensionPixelSize(R.dimen.px130);
        mTeacherGrid.setNumColumns(numColumns);
        PersonalProfileAdapter adapter = new PersonalProfileAdapter(dates, this,true);
        mTeacherGrid.setAdapter(adapter);
        mTeacherGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//                startActivity(intent);
            }
        });

        List<Date> dates1 = new ArrayList<>();
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());
        dates1.add(new Date());

        mStudentGrid.setNumColumns(numColumns);
        if (dates1.size() > numColumns) {
            dates1 = dates1.subList(0, numColumns);
        }
        PersonalProfileAdapter stu = new PersonalProfileAdapter(dates1, this,false);
        mStudentGrid.setAdapter(stu);

        mScroller.setOnScrollChangedListener(new PullToRefreshScrollView.onScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                if (y > mHeaderHeight) {
                    mHeader.setBackgroundColor(getResources().getColor(R.color.white));
                    mDivider.setVisibility(View.VISIBLE);
                    mTitle.setText("2016年人力资源一级");
                    mBack.setImageResource(R.drawable.back_arrow);
                } else {
                    mHeader.setBackgroundResource(R.drawable.ic_home_title_bg);
                    mDivider.setVisibility(View.GONE);
                    mBack.setImageResource(R.drawable.ic_white_back);
                    mTitle.setText("");
                }
            }
        });
        mBack.setImageResource(R.drawable.ic_white_back);

        mMomentHeader.setData(null);
        mContent.show(null);
    }

    @OnClick({R.id.grade_home_left_image, R.id.grade_home_schedule, R.id.grade_home_student,
            R.id.grade_home_moment, R.id.grade_home_material})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.grade_home_left_image:
                finish();
                break;
            case R.id.grade_home_schedule:
                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.grade_home_student:
                Intent stu = new Intent(this, RoomMatesActivity.class);
                startActivity(stu);
                break;
            case R.id.grade_home_moment:
                Intent moment = new Intent(this, MomentActivity.class);
                startActivity(moment);
                break;
            case R.id.grade_home_material:
                Intent material = new Intent(this, MaterialActivity.class);
                startActivity(material);
                break;
        }
    }
}
