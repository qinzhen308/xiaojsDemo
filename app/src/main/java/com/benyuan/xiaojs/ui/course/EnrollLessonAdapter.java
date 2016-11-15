package com.benyuan.xiaojs.ui.course;
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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.common.xf_foundation.LessonState;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;
import com.benyuan.xiaojs.data.LessonDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.model.EnrolledLesson;
import com.benyuan.xiaojs.model.GELessonsResponse;
import com.benyuan.xiaojs.ui.widget.ListBottomDialog;
import com.benyuan.xiaojs.ui.widget.RedTipImageView;
import com.benyuan.xiaojs.ui.widget.RedTipTextView;
import com.benyuan.xiaojs.util.TimeUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;

public class EnrollLessonAdapter extends AbsSwipeAdapter<EnrolledLesson, EnrollLessonAdapter.Holder> {
    private Criteria mCriteria;
    private LessonFragment mFragment;

    public EnrollLessonAdapter(Context context, AutoPullToRefreshListView listView, LessonFragment fragment) {
        super(context, listView);
        mFragment = fragment;
    }

    public EnrollLessonAdapter(Context context, AutoPullToRefreshListView listView) {
        super(context, listView);
    }

    @Override
    protected void initParam() {
        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.now());

        mCriteria = new Criteria();
        mCriteria.setSource(Ctl.LessonSource.ALL);
        mCriteria.setDuration(duration);
    }

    @Override
    protected void setViewContent(Holder holder, final EnrolledLesson bean, int position) {
        holder.reset();
        holder.name.setText(bean.getTitle());
        holder.time.setText(TimeUtil.getTimeByNow(bean.getSchedule().getStart()) + " " + TimeUtil.getTimeFormat(bean.getSchedule().getStart(), bean.getSchedule().getDuration()));

        holder.clsFunction.setVisibility(View.VISIBLE);
        holder.circle.setOnClickListener(new View.OnClickListener() {//班级圈
            @Override
            public void onClick(View view) {
                circle(bean);
            }
        });
        holder.enter.setOnClickListener(new View.OnClickListener() {//进入教室
            @Override
            public void onClick(View view) {
                enterClass(bean);
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {//更多
            @Override
            public void onClick(View view) {
                more(bean);
            }
        });
        if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            holder.courseState.setText(R.string.course_state_cancel);
            holder.courseState.setBackgroundResource(R.drawable.course_state_cancel_bg);
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            holder.courseState.setText(R.string.course_state_end);
            holder.courseState.setBackgroundResource(R.drawable.course_state_end_bg);
        } else if (bean.getState().equalsIgnoreCase(LessonState.LIVE)) {
            holder.courseState.setText(R.string.living);
            holder.courseState.setBackgroundResource(R.drawable.course_state_on_bg);
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            holder.courseState.setText(R.string.pending_for_course);
            holder.courseState.setBackgroundResource(R.drawable.course_state_wait_bg);
        }
    }

    //查看详情
    private void detail(EnrolledLesson bean) {
        ListBottomDialog dialog = new ListBottomDialog(mContext);
        String[] ss = new String[]{"备课", "报名注册", "分享"};
        dialog.setItems(ss);
        dialog.show();
    }

    //班级圈
    private void circle(EnrolledLesson bean) {
        modifyLesson(bean);
    }

    //进入教室
    private void enterClass(EnrolledLesson bean) {
        Intent intent = new Intent(mContext, CancelLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        mContext.startActivity(intent);
    }

    private void modifyLesson(EnrolledLesson bean) {
        Intent intent = new Intent(mContext, ModifyLessonActivity.class);
        intent.putExtra(CourseConstant.KEY_LESSON_BEAN, bean);
        mContext.startActivity(intent);
    }

    //更多
    private void more(final EnrolledLesson bean) {

        if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {
            String[] items = new String[]{
                    mContext.getString(R.string.data_bank),
                    mContext.getString(R.string.leave_lesson)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {

                    }
                }
            });
            dialog.show();
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            String[] items = new String[]{mContext.getString(R.string.evaluate),
                    mContext.getString(R.string.data_bank)};
            ListBottomDialog dialog = new ListBottomDialog(mContext);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {

                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    protected View createContentView(int position) {
        View v = mInflater.inflate(R.layout.layout_my_course, null);
        return v;
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }

    @Override
    protected void onDataItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(mContext, "position = " + position, Toast.LENGTH_SHORT).show();
        mContext.startActivity(new Intent(mContext, LessionDetailActivity.class));

    }

    @Override
    protected void doRequest() {
        LessonDataManager.requestGetEnrolledLessons(mContext, XiaojsConfig.mLoginUser.getSessionID(), mCriteria, mPagination, new APIServiceCallback<GELessonsResponse>() {
            @Override
            public void onSuccess(GELessonsResponse object) {
                Logger.d("onSuccess-----------");
                if (object.getObjectsOfPage() != null && object.getObjectsOfPage().size() > 0) {
                    if (mFragment != null) {
                        mFragment.hideTop();
                    }
                }
                EnrollLessonAdapter.this.onSuccess(object.getObjectsOfPage());
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                EnrollLessonAdapter.this.onFailure(errorCode, errorMessage);
                Logger.d("onFailure-----------");
            }
        });

    }

    @Override
    protected void onDataEmpty() {
        if (mFragment != null) {
            mFragment.showTop();
        }
    }

    public void request(Criteria criteria) {
        mCriteria = criteria;
        mPagination.setPage(PAGE_FIRST);
        mClearItems = true;
        doRequest();
    }

    static class Holder extends BaseHolder {
        @BindView(R.id.course_item_image)
        ImageView image;
        @BindView(R.id.course_xbs_state)
        TextView xbsState;
        @BindView(R.id.course_name)
        TextView name;
        @BindView(R.id.course_desc)
        TextView desc;//用户小头像为40x40
        @BindView(R.id.course_state)
        TextView courseState;
        @BindView(R.id.time_mark)
        TextView time;
        @BindView(R.id.course_price)
        TextView price;

        @BindView(R.id.course_item_fun_circle)
        RedTipTextView circle;
        @BindView(R.id.course_item_fun_enter)
        RedTipTextView enter;

        @BindView(R.id.course_item_fun_shelves)
        TextView shelves;
        @BindView(R.id.course_item_fun_edit)
        TextView edit;
        @BindView(R.id.course_item_fun_cancel)
        TextView cancel;
        @BindView(R.id.course_item_fun_detail)
        TextView detail;

        @BindView(R.id.course_item_fun_error)
        RedTipImageView error;
        @BindView(R.id.course_item_fun_data_base)
        RedTipImageView databank;

        @BindView(R.id.course_item_fun_more)
        TextView more;

        @BindView(R.id.course_item_fun_cls)
        LinearLayout clsFunction;
        @BindView(R.id.course_item_fun_opera)
        LinearLayout operaFunction;

        @BindView(R.id.course_item_fun_shelves_line)
        View shelvesLine;
        @BindView(R.id.course_item_fun_edit_line)
        View editLine;
        @BindView(R.id.course_item_fun_cancel_line)
        View cancelLine;


        public void reset() {
            clsFunction.setVisibility(View.GONE);
            operaFunction.setVisibility(View.GONE);
            error.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            databank.setVisibility(View.GONE);
            shelves.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            shelvesLine.setVisibility(View.GONE);
            editLine.setVisibility(View.GONE);
            cancelLine.setVisibility(View.GONE);
            price.setVisibility(View.GONE);
        }

        public Holder(View view) {
            super(view);
        }
    }
}
