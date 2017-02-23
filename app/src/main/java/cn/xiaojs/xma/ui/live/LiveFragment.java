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

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshGridView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.EnrollLessonActivity;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.HorizontalAdaptScrollerView;
import cn.xiaojs.xma.util.TimeUtil;

public class LiveFragment extends BaseFragment implements View.OnClickListener {

    private final int MAX = 3;

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
    private Criteria mCriteria;
    protected Pagination mPagination;

    private List<TeachLesson> mTeachLessons;
    private List<EnrolledLesson> mEnrollLessons;

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

//        List<TeachLesson> teachLessons = new ArrayList<>();
//        TeachLesson tl1 = new TeachLesson();
//        tl1.setTitle("希区柯克教你拍电影");
//        Price price = new Price();
//        price.free = true;
//        tl1.setFee(price);
//        Enroll enroll = new Enroll();
//        enroll.max = 100;
//        enroll.current = 19;
//        tl1.setEnroll(enroll);
//        tl1.setState(LessonState.PENDING_FOR_LIVE);
//
//        Publish publish = new Publish();
//        publish.accessible = false;
//
//        Schedule schedule = new Schedule();
//        schedule.setDuration(88);
//        schedule.setStart(new Date(System.currentTimeMillis() + 3600 * 1000 * 12));
//        tl1.setSchedule(schedule);
//        tl1.setPublish(publish);
//        teachLessons.add(tl1);
//
//        TeachLesson tl2 = new TeachLesson();
//        tl2.setTitle("灵枢针灸-中医入门");
//        tl2.setFee(price);
//        tl2.setEnroll(enroll);
//        tl2.setState(LessonState.LIVE);
//        tl2.setSchedule(schedule);
//        tl2.setPublish(publish);
//        teachLessons.add(tl2);
//        mLessonList.setAdapter(new LiveTeachLessonAdapter(mContext, teachLessons));
//
//
//        EnrolledLesson el1 = new EnrolledLesson();
//        el1.setTitle("心理咨询师取证全攻略");
//        el1.setFee(price);
//        el1.setSchedule(schedule);
//        Teacher teacher = new Teacher();
//        Account.Basic basic = new Account.Basic();
//        basic.setName("张大仙");
//        teacher.setBasic(basic);
//        el1.setTeacher(teacher);
//        el1.setState(LessonState.PENDING_FOR_LIVE);
//
//        List<EnrolledLesson> enrolls = new ArrayList<>();
//        enrolls.add(el1);
//
//        mLessonList2.setAdapter(new LiveEnrollLessonAdapter(mContext, enrolls));

        mCriteria = new Criteria();
        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.yearAfter(10));

        mCriteria = new Criteria();
        mCriteria.setSource(Ctl.LessonSource.ALL);
        mCriteria.setDuration(duration);

        mPagination = new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(3);
        getEnrollLessons();
    }

    private void getTeachLessons() {
        LessonDataManager.requestGetLessons(mContext, mCriteria, mPagination, new APIServiceCallback<GetLessonsResponse>() {
            @Override
            public void onSuccess(GetLessonsResponse object) {
                Logger.d("onSuccess-----------");
                if (object != null) {
                    mTeachLessons = object.getObjectsOfPage();
                }
                initLessons();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Logger.d("onFailure-----------");
                initLessons();
            }
        });
    }

    private void getEnrollLessons() {
        LessonDataManager.requestGetEnrolledLessons(mContext, mCriteria, mPagination, new APIServiceCallback<GELessonsResponse>() {
            @Override
            public void onSuccess(GELessonsResponse object) {
                Logger.d("onSuccess-----------");
                if (object != null) {
                    mEnrollLessons = object.getObjectsOfPage();
                }
                getTeachLessons();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Logger.d("onFailure-----------");
                getTeachLessons();
            }
        });
    }

    private void initLessons() {
        if ((mEnrollLessons == null && mTeachLessons == null) || (mEnrollLessons.size() <= 0 && mTeachLessons.size() <= 0)) {
            //没有教的课也没有学的课
            if (SecurityManager.checkPermission(mContext, Su.Permission.COURSE_OPEN_CREATE)) {
                //当前用户是老师
                mTeacherWrapper.setVisibility(View.VISIBLE);
                mStudentWrapper.setVisibility(View.GONE);
                mTeacherEmpty.setVisibility(View.VISIBLE);
                mStudentEmpty.setVisibility(View.GONE);
            } else {
                mTeacherEmpty.setVisibility(View.GONE);
                mStudentEmpty.setVisibility(View.VISIBLE);
                mTeacherWrapper.setVisibility(View.GONE);
                mStudentWrapper.setVisibility(View.VISIBLE);
            }
        } else if ((mEnrollLessons == null || mEnrollLessons.size() <= 0) && (mTeachLessons != null && mTeachLessons.size() > 0)) {
            //只有教的课，没有学的课
            LiveTeachLessonAdapter adapter = new LiveTeachLessonAdapter(mContext, mTeachLessons);
            mLessonList.setAdapter(adapter);
            mLessonList2.setVisibility(View.GONE);
            mLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(mContext, LessonHomeActivity.class);
                    i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                    i.putExtra(CourseConstant.KEY_LESSON_ID, mTeachLessons.get(position).getId());
                    mContext.startActivity(i);
                }
            });
            mStudentWrapper.setVisibility(View.GONE);
        } else if ((mEnrollLessons != null && mEnrollLessons.size() > 0) && (mTeachLessons == null || mTeachLessons.size() <= 0)) {
            //只有学的课，没有教的课
            LiveEnrollLessonAdapter adapter = new LiveEnrollLessonAdapter(mContext, mEnrollLessons);
            mLessonList2.setAdapter(adapter);
            mLessonList.setVisibility(View.GONE);
            mLessonList2.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(mContext, LessonHomeActivity.class);
                    i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                    i.putExtra(CourseConstant.KEY_LESSON_ID, mEnrollLessons.get(position).getId());
                    mContext.startActivity(i);
                }
            });
            mTeacherWrapper.setVisibility(View.GONE);
        } else {
            //有教的课也有学的课
            if (mTeachLessons.size() == 1 && mEnrollLessons.size() == 3) {
                mEnrollLessons = mEnrollLessons.subList(0, MAX - 1);
            } else if (mTeachLessons.size() == 2 && mEnrollLessons.size() > 1) {
                mEnrollLessons = mEnrollLessons.subList(0, MAX - 2);
            } else if (mTeachLessons.size() == 3) {
                mTeachLessons = mTeachLessons.subList(0, MAX - 1);
                if (mEnrollLessons.size() > 1) {
                    mEnrollLessons = mEnrollLessons.subList(0, MAX - 2);
                }
            }
            LiveTeachLessonAdapter teach = new LiveTeachLessonAdapter(mContext, mTeachLessons);
            mLessonList.setAdapter(teach);
            LiveEnrollLessonAdapter enroll = new LiveEnrollLessonAdapter(mContext, mEnrollLessons);
            mLessonList2.setAdapter(enroll);
            mLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(mContext, LessonHomeActivity.class);
                    i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                    i.putExtra(CourseConstant.KEY_LESSON_ID, mTeachLessons.get(position).getId());
                    mContext.startActivity(i);
                }
            });
            mLessonList2.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(mContext, LessonHomeActivity.class);
                    i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                    i.putExtra(CourseConstant.KEY_LESSON_ID, mEnrollLessons.get(position).getId());
                    mContext.startActivity(i);
                }
            });
        }
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
