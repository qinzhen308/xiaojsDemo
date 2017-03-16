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
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.LiveClass;
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
    HorizontalAdaptScrollerView mHorizontalListView;
    CanInScrollviewListView mLessonList;
    CanInScrollviewListView mLessonList2;

    View mTeacherWrapper;
    View mStudentWrapper;

    View mTeachLessonTitleView;
    View mEnrollLessonTitleView;

    LinearLayout mTeachLessonEmpty;
    TextView mEnrollLessonEmpty;

    View mTeachLessonAllView;
    View mEnrollLessonAllView;

    ImageView mOpenLesson;

    private int mUserType;
    private Criteria mCriteria;
    protected Pagination mPagination;
    private boolean mDataLoading;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_live, null);
        mHorizontalListView = (HorizontalAdaptScrollerView) v.findViewById(R.id.home_live_brilliant);

        mLessonList = (CanInScrollviewListView) v.findViewById(R.id.home_live_list);
        mLessonList2 = (CanInScrollviewListView) v.findViewById(R.id.home_live_list2);

        mTeacherWrapper = v.findViewById(R.id.teacher_wrapper);
        mStudentWrapper = v.findViewById(R.id.student_wrapper);

        mTeachLessonEmpty = (LinearLayout) v.findViewById(R.id.teach_lesson_empty);
        mEnrollLessonEmpty = (TextView) v.findViewById(R.id.enroll_lesson_empty);

        mTeachLessonAllView = v.findViewById(R.id.teach_lesson_all);
        mEnrollLessonAllView = v.findViewById(R.id.enroll_lesson_all);
        mTeachLessonTitleView = v.findViewById(R.id.lesson_all_1);
        mEnrollLessonTitleView = v.findViewById(R.id.lesson_all_2);
        mOpenLesson = (ImageView) v.findViewById(R.id.open_lesson);

        mLessonList.setNeedDivider(true);
        mLessonList.setDividerHeight(mContext.getResources().getDimensionPixelSize(R.dimen.px30));
        mLessonList.setDividerColor(R.color.main_bg);
        mLessonList2.setDividerColor(R.color.main_bg);
        mTeachLessonTitleView.setOnClickListener(this);
        mEnrollLessonTitleView.setOnClickListener(this);
        View search = v.findViewById(R.id.my_course_search);
        search.setOnClickListener(this);
        mOpenLesson.setOnClickListener(this);
        //ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
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

        initData();
    }

    @Override
    protected void reloadOnFailed() {
        initData();
    }

    private void initData() {
        if (mDataLoading) {
            return;
        }

        mDataLoading = true;
        showProgress(true);
        LessonDataManager.getLiveClasses(mContext, new APIServiceCallback<LiveClass>() {
            @Override
            public void onSuccess(LiveClass object) {
                cancelProgress();
                hideFailView();
                fillData(object);
                mDataLoading = false;
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                mDataLoading = false;
                //Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                showFailView();
            }
        });
    }

    private void fillData(final LiveClass liveClasses) {
        if (liveClasses != null) {
            if ((liveClasses.taught != null && !liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled == null || liveClasses.enrolled.isEmpty())) {
                //只有教的课，没有学的课
                mTeachLessonAllView.setVisibility(View.VISIBLE);
                mTeachLessonTitleView.setVisibility(View.VISIBLE);
                LiveClassAdapter adapter = new LiveClassAdapter(mContext, liveClasses.taught);
                mLessonList.setAdapter(adapter);
                mLessonList2.setVisibility(View.GONE);
                mLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(mContext, LessonHomeActivity.class);
                        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                        i.putExtra(CourseConstant.KEY_LESSON_ID, liveClasses.taught.get(position).id);
                        mContext.startActivity(i);
                    }
                });
                mStudentWrapper.setVisibility(View.GONE);
            } else if ((liveClasses.taught == null || liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled != null && !liveClasses.enrolled.isEmpty())) {
                //只有学的课，没有教的课
                mEnrollLessonAllView.setVisibility(View.VISIBLE);
                LiveClassAdapter adapter = new LiveClassAdapter(mContext, liveClasses.enrolled);
                mLessonList2.setAdapter(adapter);
                mLessonList.setVisibility(View.GONE);
                mLessonList2.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(mContext, LessonHomeActivity.class);
                        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                        i.putExtra(CourseConstant.KEY_LESSON_ID, liveClasses.enrolled.get(position).id);
                        mContext.startActivity(i);
                    }
                });
                mTeacherWrapper.setVisibility(View.GONE);
            } else if ((liveClasses.taught != null && !liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled != null && !liveClasses.enrolled.isEmpty())) {
                //有教的课也有学的课
                mTeachLessonAllView.setVisibility(View.VISIBLE);
                mEnrollLessonAllView.setVisibility(View.VISIBLE);
                mTeachLessonTitleView.setVisibility(View.VISIBLE);
                LiveClassAdapter teach = new LiveClassAdapter(mContext, liveClasses.taught);
                mLessonList.setAdapter(teach);
                LiveClassAdapter enroll = new LiveClassAdapter(mContext, liveClasses.enrolled);
                mLessonList2.setAdapter(enroll);
                mLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(mContext, LessonHomeActivity.class);
                        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                        i.putExtra(CourseConstant.KEY_LESSON_ID, liveClasses.taught.get(position).id);
                        mContext.startActivity(i);
                    }
                });
                mLessonList2.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(mContext, LessonHomeActivity.class);
                        i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
                        i.putExtra(CourseConstant.KEY_LESSON_ID, liveClasses.enrolled.get(position).id);
                        mContext.startActivity(i);
                    }
                });
            } else if ((liveClasses.taught == null || liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled == null || liveClasses.enrolled.isEmpty())) {
                //没有教的课也没有学的课
                mEnrollLessonTitleView.setVisibility(View.VISIBLE);
                if (SecurityManager.checkPermission(mContext, Su.Permission.COURSE_OPEN_CREATE)) {
                    //当前用户是老师
                    mTeachLessonTitleView.setVisibility(View.VISIBLE);
                    mTeacherWrapper.setVisibility(View.VISIBLE);
                    mStudentWrapper.setVisibility(View.GONE);
                    mTeachLessonEmpty.setVisibility(View.VISIBLE);
                    mEnrollLessonEmpty.setVisibility(View.GONE);
                } else {
                    mTeachLessonEmpty.setVisibility(View.GONE);
                    mEnrollLessonEmpty.setVisibility(View.VISIBLE);
                    mEnrollLessonEmpty.setText(Html.fromHtml(getString(R.string.student_no_lesson_tip)));
                    mTeacherWrapper.setVisibility(View.GONE);
                    mStudentWrapper.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //没有教的课也没有学的课
            mEnrollLessonTitleView.setVisibility(View.VISIBLE);
            if (SecurityManager.checkPermission(mContext, Su.Permission.COURSE_OPEN_CREATE)) {
                //当前用户是老师
                mTeachLessonTitleView.setVisibility(View.VISIBLE);
                mTeacherWrapper.setVisibility(View.VISIBLE);
                mStudentWrapper.setVisibility(View.GONE);
                mTeachLessonEmpty.setVisibility(View.VISIBLE);
                mEnrollLessonEmpty.setVisibility(View.GONE);
            } else {
                mTeachLessonEmpty.setVisibility(View.GONE);
                mEnrollLessonEmpty.setVisibility(View.VISIBLE);
                mEnrollLessonEmpty.setText(Html.fromHtml(getString(R.string.student_no_lesson_tip)));
                mTeacherWrapper.setVisibility(View.GONE);
                mStudentWrapper.setVisibility(View.VISIBLE);
            }
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
