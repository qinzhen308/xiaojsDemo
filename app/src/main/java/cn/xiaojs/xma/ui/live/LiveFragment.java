package cn.xiaojs.xma.ui.live;

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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshGridView;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.model.Teacher;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.Enroll;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.lesson.EnrollLessonActivity;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.HorizontalAdaptScrollerView;

public class LiveFragment extends BaseFragment implements View.OnClickListener {

    PullToRefreshGridView mGrid;
    HorizontalAdaptScrollerView mHorizontalListView;
    CanInScrollviewListView mLessonList;
    CanInScrollviewListView mLessonList2;

    View mTeacherWrapper;
    View mStudentWrapper;

    View mTeacherEmpty;
    TextView mStudentEmpty;

    ImageView mOpenLesson;

    View mHeader;


    private int mUserType;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_moment, null);
        mGrid = (PullToRefreshGridView) v.findViewById(R.id.live_grid);

        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_live_header, null);
        mHorizontalListView = (HorizontalAdaptScrollerView) mHeader.findViewById(R.id.home_live_brilliant);
        mGrid.getRefreshableView().addHeaderView(mHeader);

        mLessonList = (CanInScrollviewListView) mHeader.findViewById(R.id.home_live_list);
        mLessonList2 = (CanInScrollviewListView) mHeader.findViewById(R.id.home_live_list2);

        mTeacherWrapper = mHeader.findViewById(R.id.teacher_wrapper);
        mStudentWrapper = mHeader.findViewById(R.id.student_wrapper);

        mTeacherEmpty = mHeader.findViewById(R.id.teach_empty);
        mStudentEmpty = (TextView) mHeader.findViewById(R.id.student_empty);

        mOpenLesson = (ImageView) mHeader.findViewById(R.id.open_lesson);

        mLessonList.setNeedDivider(true);
        mLessonList.setDividerHeight(mContext.getResources().getDimensionPixelSize(R.dimen.px30));
        mLessonList.setDividerColor(R.color.main_bg);
        View all1 = mHeader.findViewById(R.id.lesson_all_1);
        View all2 = mHeader.findViewById(R.id.lesson_all_2);
        all1.setOnClickListener(this);
        all2.setOnClickListener(this);
        View search = v.findViewById(R.id.my_course_search);
        search.setOnClickListener(this);
        mOpenLesson.setOnClickListener(this);
        //ButterKnife.bind(this,v);
        return v;
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUserType = bundle.getInt(LiveConstant.KEY_USER_TYPE, LiveConstant.USER_STUDENT);
        }
        RecyclerView.Adapter adapter = new LiveBrilliantAdapter(mContext);
        mHorizontalListView.setItemVisibleCountType(HorizontalAdaptScrollerView.ItemVisibleTypeCount.TYPE_FREE);
        mHorizontalListView.setItemVisibleCount(1.7f);
        mHorizontalListView.setAdapter(adapter);
        ViewGroup.LayoutParams lp = mHorizontalListView.getLayoutParams();
        lp.height = getResources().getDimensionPixelSize(R.dimen.px370);
        mHorizontalListView.setLayoutParams(lp);
        mGrid.setAdapter(new LiveAdapter(mContext, mGrid));

        List<TeachLesson> teachLessons = new ArrayList<>();
        TeachLesson tl1 = new TeachLesson();
        tl1.setTitle("希区柯克教你拍电影");
        Price price = new Price();
        price.free = true;
        tl1.setFee(price);
        Enroll enroll = new Enroll();
        enroll.max = 100;
        enroll.current = 19;
        tl1.setEnroll(enroll);
        tl1.setState(LessonState.PENDING_FOR_LIVE);

        Publish publish = new Publish();
        publish.accessible = false;

        Schedule schedule = new Schedule();
        schedule.setDuration(88);
        schedule.setStart(new Date(System.currentTimeMillis() + 3600 * 1000 * 12));
        tl1.setSchedule(schedule);
        tl1.setPublish(publish);
        teachLessons.add(tl1);

        TeachLesson tl2 = new TeachLesson();
        tl2.setTitle("灵枢针灸-中医入门");
        tl2.setFee(price);
        tl2.setEnroll(enroll);
        tl2.setState(LessonState.LIVE);
        tl2.setSchedule(schedule);
        tl2.setPublish(publish);
        teachLessons.add(tl2);
        mLessonList.setAdapter(new LiveTeachLessonAdapter(mContext, teachLessons));


        EnrolledLesson el1 = new EnrolledLesson();
        el1.setTitle("心理咨询师取证全攻略");
        el1.setFee(price);
        el1.setSchedule(schedule);
        Teacher teacher = new Teacher();
        Account.Basic basic = new Account.Basic();
        basic.setName("张大仙");
        teacher.setBasic(basic);
        el1.setTeacher(teacher);
        el1.setState(LessonState.PENDING_FOR_LIVE);

        List<EnrolledLesson> enrolls = new ArrayList<>();
        enrolls.add(el1);

        mLessonList2.setAdapter(new LiveEnrollLessonAdapter(mContext, enrolls));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lesson_all_1:
                Intent intent = new Intent(mContext, TeachLessonActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.lesson_all_2:
                Intent enroll = new Intent(mContext, EnrollLessonActivity.class);
                mContext.startActivity(enroll);
                break;
            case R.id.my_course_search:
                Intent search = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(search);
                break;
            case R.id.open_lesson:
                Intent open = new Intent(mContext, LessonCreationActivity.class);
                mContext.startActivity(open);
                break;
        }
    }

}
